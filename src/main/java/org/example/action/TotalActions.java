package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.jdbc.Null;
import org.example.entity.*;
import org.example.mapper.AccountMapper;

import org.example.mapper.PhotoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TotalActions {
    @Resource
    AccountMapper accountMapper;

    @Resource
    PhotoMapper photoMapper;


    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/userLogin")
    public RespResult<Account> userLogin(String phone, String code, HttpServletRequest request) {
        RespResult<Account> result = new RespResult<Account>();
        if (phone.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("手机号不能为空");
            return result;
        } else if (!code.equals("80008")) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("验证码不正确");
            return result;
        }
        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("phone", phone);
        Account account = accountMapper.selectOne(query);
        if (account == null) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage(RespErrorCode.UNREGISTER.getMessage());
        } else  {
            request.getSession().setAttribute("user", account);
            result.setStatus(RespErrorCode.OK.getMessage());
            result.setMessage(RespErrorCode.SUCCESS.getMessage());
        }
        result.setData(account);
        return result;
    }

    @GetMapping("/userLogout")
    public RespResult<String> userLogout(@RequestHeader("token") String token, HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        RespResult result = new RespResult<String>();
        result.setData("退出登录成功");
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        result.setStatus(RespErrorCode.OK.getMessage());
        return result;
    }

    @GetMapping("/tokenInvail")
    public RespEmptyResult tokenInvail(HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        RespEmptyResult result = new RespEmptyResult();
        result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
        result.setStatus(RespErrorCode.ERROR.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/userRegister")
    public RespResult<Account> userRegister(String phone, String nickname) {
        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("phone", phone);
        query.last("limit 1");
        Account account = accountMapper.selectOne(query);
        RespResult<Account> result = new RespResult<Account>();
        if (account == null) {
            account = new Account();
            account.setPhone(phone);
            account.setNickname(nickname);
            String token = SecureRandomStringGenerator.generateRandomString();
            account.setToken(token);
            try {
                accountMapper.insert(account);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        result.setData(account);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

    // 查看我的用户信息
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/myUserInfo")
    public RespResult<Account> myUserInfo(@RequestHeader("token") String token, HttpServletRequest request) {
        Account account = tokenIsVaild(request);
        if (account == null) {
            RespResult<Account> result = new RespResult<Account>();
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }
        request.getSession().setAttribute("user", account);
        RespResult<Account> result = new RespResult<Account>();
        result.setData(account);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

    @Value("${server.port}")
    private int serPort;

    @GetMapping("/hello")
    public RespResult<String> hello() {
        RespResult<String> result = new RespResult<String>();
        String ress = "";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            ress = ipAddress + ":" + serPort;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        result.setData(ress);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }


    public Account tokenIsVaild(HttpServletRequest request) {
        String token = request.getHeader("token");
        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("token", token);
        query.select("id", "nickname"); // 指定要返回的字段
        query.last("limit 1");
        Account account =  accountMapper.selectOne(query);
        request.getSession().setAttribute("user", account);
        return account;
    }


    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoList")
    public RespResult<Map<String, List<Photo>>> getPhotoList() {
        RespResult<Map<String, List<Photo>>> result = new RespResult<>();
        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
        List<Photo> photos = photoMapper.selectList(query);
        Map<String, List<Photo>> maps = new HashMap<>();
        maps.put("list", photos);
        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

}
