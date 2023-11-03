package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.jdbc.Null;
import org.example.entity.*;
import org.example.mapper.*;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TotalActions {
    @Resource
    AccountMapper accountMapper;

    @Resource
    PhotoMapper photoMapper;

    @Resource
    BannerMapper bannerMapper;

    @Resource
    PhotoCategoryMapper categoryMapper;

    @Resource
    PhotoCollectMapper photoCollectMapper;


    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/userLogin")
    public RespResult<Account> userLogin(String username, String password, HttpServletRequest request) {
        RespResult<Account> result = new RespResult<Account>();
        if (username.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("The user name cannot be empty");
            return result;
        } else if (password.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("The password cannot be empty");
            return result;
        }

        if (!PatternMatcher.textInputPass(username, PatterRegexType.USERNAME)) {
            result.setStatus(RespErrorCode.USERNAMEERROR.getMessage());
            result.setMessage(RespErrorCode.USERNAMEERROR.getDetail());
//            result.setMessage("The format of the user name is incorrect");
            return result;
        } else if (!PatternMatcher.textInputPass(password, PatterRegexType.PASSWORD)) {
            result.setStatus(RespErrorCode.PASSWORDERROR.getMessage());
            result.setMessage(RespErrorCode.PASSWORDERROR.getDetail());
//            result.setMessage("The password format is incorrect");
            return result;
        }

        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("username", username);
        Account account = accountMapper.selectOne(query);
        if (account == null) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage(RespErrorCode.UNREGISTER.getMessage());
            return result;
        } else {
            String validCode = RequestUriUtils.mdfive(password);
            if (account.getValid().equals(validCode) == false) {
                result.setStatus(RespErrorCode.ERROR.getMessage());
                result.setMessage("Incorrect password");
                return result;
            }
            account.setValid(null);
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
        result.setData("Log out successfully");
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
    public RespResult<Account> userRegister(String username, String password) {
        RespResult<Account> result = new RespResult<Account>();
        if (username.isEmpty() || password.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("The format of the user name or password is incorrect");
            return result;
        }

        if (!PatternMatcher.textInputPass(username, PatterRegexType.USERNAME)) {
            result.setStatus(RespErrorCode.USERNAMEERROR.getMessage());
            result.setMessage(RespErrorCode.USERNAMEERROR.getDetail());
//            result.setMessage("The format of the user name is incorrect");
            return result;
        }
        else if (!PatternMatcher.textInputPass(password, PatterRegexType.PASSWORD)) {
            result.setStatus(RespErrorCode.PASSWORDERROR.getMessage());
            result.setMessage(RespErrorCode.PASSWORDERROR.getDetail());
//            result.setMessage("The password format is incorrect");
            return result;
        }

        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("username", username);
        query.last("limit 1");
        Account account = accountMapper.selectOne(query);
        if (account == null) {
            account = new Account();
            account.setPhone(username);

            String code = RequestUriUtils.mdfive(password);
            if (code != null) {
                account.setValid(code);
            }

            account.setAvatar("https://tenfei05.cfp.cn/creative/vcg/800/new/VCG211185552079.jpg");
            String nickName = SecureRandomStringGenerator.generateLimitString(4);
            account.setNickname("user" + nickName);
            String token = SecureRandomStringGenerator.generateRandomString();
            account.setToken(token);
            try {
                accountMapper.insert(account);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            account.setValid(null);
            result.setData(account);
            result.setStatus(RespErrorCode.OK.getMessage());
            result.setMessage(RespErrorCode.SUCCESS.getMessage());
            return result;

        } else {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("The user is registered");
            return result;
        }
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
        account.setToken(null);
        request.getSession().setAttribute("user", account);
        RespResult<Account> result = new RespResult<Account>();
        account.setPhone(null);
        account.setValid(null);
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
//        query.select("id", "nickname"); // 指定要返回的字段
        query.last("limit 1");
        Account account = accountMapper.selectOne(query);
        request.getSession().setAttribute("user", account);
        return account;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoList")
    public RespResult<IPage<Photo>> getPhotoList(@RequestParam(value = "id", required = false) Integer id,
                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "size", defaultValue = "5") Integer size) {
    RespResult<IPage<Photo>> result = new RespResult<>();
        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
        if (id != null) {
            /// 字段需要跟数据库字段对应，不能使用驼峰 categoryId, 查询异常
            query.eq("category_id", id);
        }
        Page<Photo> pageMap = new Page<>(page, size);
        IPage<Photo> pagePhoto = photoMapper.selectPage(pageMap, query);
        result.setData(pagePhoto);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoList/v2")
    public RespResult<IPage<Photo>> getPhotoListV1(@RequestHeader("token") String token,
                                                   HttpServletRequest request,
                                                   @RequestParam(value = "id", required = false) Integer id,
                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "5") Integer size) {
        RespResult<IPage<Photo>> result = new RespResult<>();

        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }

        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
        if (id != null) {
            /// 字段需要跟数据库字段对应，不能使用驼峰 categoryId, 查询异常
            query.eq("category_id", id);
        }

        List<Long> photoIds = getMyCollectionPhotoIds(account);

        Page<Photo> pageMap = new Page<>(page, size);
        IPage<Photo> pagePhoto = photoMapper.selectPage(pageMap, query);
        List<Photo> photoRecords = pagePhoto.getRecords();
        for(Photo oto: photoRecords) {
            if (photoIds.contains(oto.getId())) {
                oto.setCollect(true);
            }
        }
//        result.setData(pagePhoto);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/bannerList")
    public RespResult<Map<String, List<BannerEntity>>> getBannerList(Integer id) {
        RespResult<Map<String, List<BannerEntity>>> result = new RespResult<>();
        QueryWrapper<BannerEntity> query = new QueryWrapper<BannerEntity>();
        List<BannerEntity> banners = bannerMapper.selectList(query);
        Map<String, List<BannerEntity>> maps = new HashMap<>();
        maps.put("list", banners);
        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoCategory")
    public RespResult<Map<String, List<PhotoCategory>>> getPhotoCategory() {
        RespResult<Map<String, List<PhotoCategory>>> result = new RespResult<>();
        QueryWrapper<PhotoCategory> query = new QueryWrapper<PhotoCategory>();
        List<PhotoCategory> cates = categoryMapper.selectList(query);
        Map<String, List<PhotoCategory>> maps = new HashMap<>();
        maps.put("list", cates);
        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoSearch")
    public RespResult<Map<String, List<Photo>>> searchPhotos(String text) {
        RespResult<Map<String, List<Photo>>> result = new RespResult<>();
        if (text.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("Please enter the text you want to search for");
            return result;
        }
        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
        query.like("title", text);
        List<Photo> cates = photoMapper.selectList(query);
        Map<String, List<Photo>> maps = new HashMap<>();
        maps.put("list", cates);
        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getMessage());
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoCollect")
    @PostMapping("/photoCollect")
    public RespResult<Map<String, Boolean>> collectOperation(@RequestHeader("token") String token, HttpServletRequest request, Long photoId, Boolean collect) {
        RespResult<Map<String, Boolean>> result = new RespResult<>();
        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }

        if (photoId == null || collect == null) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("Parameter exception");
            return result;
        }

        Photo photo = photoMapper.selectById(photoId);
        if (photo == null) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage("Resource does not exist");
            return result;
        }

        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
        query.eq("photo_id", photoId);
        query.eq("user_id", account.getId());
        PhotoCollect collects = photoCollectMapper.selectOne(query);

        if (collects == null) {
            if (collect) {
                PhotoCollect colletion = new PhotoCollect();
                colletion.setUserId(account.getId());
                colletion.setPhotoId(photoId);
                photoCollectMapper.insert(colletion);
            }
        } else {
            if (!collect) {
                photoCollectMapper.deleteById(collects.getId());
            }
        }

        String message = collect ? "Successful collection" : "Uncollect successfully";
        Map<String, Boolean> data = new HashMap<>();
        data.put("result", collect);
        result.setData(data);
        result.setMessage(message);
        result.setStatus(RespErrorCode.OK.getMessage());
        return  result;
    }


    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/myPhotoCollects")
    public RespResult<Map<String, List<Photo>>> myCollectList(@RequestHeader("token") String token, HttpServletRequest request) {
        RespResult<Map<String, List<Photo>>> result = new RespResult<>();
        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setStatus(RespErrorCode.ERROR.getMessage());
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }

        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
        query.eq("user_id", account.getId());
        List<PhotoCollect> collects = photoCollectMapper.selectList(query);
        List<Long> listIds = mapPhotoIds(collects);
        Map<String, List<Photo>> maps = new HashMap<>();

        if (!listIds.isEmpty()) {
            List<Photo> list = photoMapper.selectBatchIds(listIds);
            for (Photo otp : list) {
                otp.setCollect(true);
            }
            maps.put("list", list);


        } else {
            maps.put("list", new ArrayList<Photo>());

        }
        result.setData(maps);
        result.setMessage(RespErrorCode.SUCCESS.getMessage());
        result.setStatus(RespErrorCode.OK.getMessage());
        return  result;
    }


    public List<Long>getMyCollectionPhotoIds(Account account) {
        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
        query.eq("user_id", account.getId());
        List<PhotoCollect> collects = photoCollectMapper.selectList(query);
        List<Long> listIds = mapPhotoIds(collects);
        return listIds;
    }

    public List<Long>mapPhotoIds(List<PhotoCollect> collects) {
        List<Long> listIds = collects.stream()
                .map(collect -> {
                    return collect.getPhotoId();
                })
                .collect(Collectors.toList());
        return listIds;
    }


}
