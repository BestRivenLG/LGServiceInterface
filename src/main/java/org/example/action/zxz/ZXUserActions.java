package org.example.action.zxz;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.CommonTool;
import org.example.common.PatterRegexType;
import org.example.common.PatternMatcher;
import org.example.common.RespErrorCode;
import org.example.config.UserLoginInterceptor;
import org.example.entity.Account;
import org.example.entity.RespResult;
import org.example.mapper.AccountMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/zxz/user")
public class ZXUserActions {

    AccountMapper accountMapper;

    /*用户登录*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/login")
    public RespResult<Account> userLogin(String username, String password, HttpServletRequest request) {
        RespResult<Account> result = new RespResult<Account>();
        if (username.isEmpty()) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The user name cannot be empty");
            return result;
        } else if (password.isEmpty()) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The password cannot be empty");
            return result;
        }

        if (!PatternMatcher.textInputPass(username, PatterRegexType.USERNAME)) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.USERNAMEERROR.getStatus());
            result.setMsg(RespErrorCode.USERNAMEERROR.getMessage());
            return result;
        } else if (!PatternMatcher.textInputPass(password, PatterRegexType.PASSWORD)) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.PASSWORDERROR.getStatus());
            result.setMsg(RespErrorCode.PASSWORDERROR.getMessage());
            return result;
        }

        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("username", username);
        Account account = accountMapper.selectOne(query);
        if (account == null) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.UNREGISTER.getStatus());
            result.setMsg(RespErrorCode.UNREGISTER.getMessage());
            return result;
        } else {
            String validCode = UserLoginInterceptor.RequestUriUtils.mdfive(password);
            if (!account.getValid().equals(validCode)) {
                result.setCode(RespErrorCode.ERROR.getCode());
                result.setStatus(RespErrorCode.PASSWORDERROR.getStatus());
                result.setMsg(RespErrorCode.PASSWORDERROR.getMessage());
                return result;
            }
            account.setValid(null);
            request.getSession().setAttribute("user", account);
            result.setCode(RespErrorCode.OK.getCode());
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMsg(RespErrorCode.OK.getMessage());
        }
        result.setData(account);
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/logout")
    public RespResult<String> userLogout(@RequestHeader("token") String token, HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        RespResult result = new RespResult<String>();
        result.setData("Log out successfully");
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    /*用户注册*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/register")
    public RespResult<Account> userRegister(String username, String password) {
        RespResult<Account> result = new RespResult<Account>();
        if (username.isEmpty() || password.isEmpty()) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The format of the user name or password is incorrect");
            return result;
        }

        if (!PatternMatcher.textInputPass(username, PatterRegexType.USERNAME)) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.USERNAMEERROR.getStatus());
            result.setMsg(RespErrorCode.USERNAMEERROR.getMessage());
            return result;
        }
        else if (!PatternMatcher.textInputPass(password, PatterRegexType.PASSWORD)) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.PASSWORDERROR.getStatus());
            result.setMsg(RespErrorCode.PASSWORDERROR.getMessage());
            return result;
        }

        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("username", username);
        query.last("limit 1");
        Account account = accountMapper.selectOne(query);
        if (account == null) {
            account = new Account();
            account.setPhone(username);

            String code = UserLoginInterceptor.RequestUriUtils.mdfive(password);
            if (code != null) {
                account.setValid(code);
            }

            account.setAvatar("https://tenfei05.cfp.cn/creative/vcg/800/new/VCG211185552079.jpg");
            String nickName = CommonTool.generateLimitString(4);
            account.setNickname("user" + nickName);
            String token = CommonTool.generateRandomString();
            account.setToken(token);
            try {
                accountMapper.insert(account);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            account.setValid(null);
            result.setData(account);
            result.setCode(RespErrorCode.OK.getCode());
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMsg(RespErrorCode.OK.getMessage());
            return result;

        } else {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The user is registered");
            return result;
        }
    }
}
