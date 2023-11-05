package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.example.common.*;
import org.example.config.UserLoginInterceptor;
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class TotalActions {
    @Value("${server.port}")
    private int serPort;
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
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The user name cannot be empty");
            return result;
        } else if (password.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The password cannot be empty");
            return result;
        }

        if (!PatternMatcher.textInputPass(username, PatterRegexType.USERNAME)) {
            result.setStatus(RespErrorCode.USERNAMEERROR.getStatus());
            result.setMessage(RespErrorCode.USERNAMEERROR.getMessage());
//            result.setMessage("The format of the user name is incorrect");
            return result;
        } else if (!PatternMatcher.textInputPass(password, PatterRegexType.PASSWORD)) {
            result.setStatus(RespErrorCode.PASSWORDERROR.getStatus());
            result.setMessage(RespErrorCode.PASSWORDERROR.getMessage());
//            result.setMessage("The password format is incorrect");
            return result;
        }

        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("username", username);
        Account account = accountMapper.selectOne(query);
        if (account == null) {
            result.setStatus(RespErrorCode.UNREGISTER.getStatus());
            result.setMessage(RespErrorCode.UNREGISTER.getMessage());
            return result;
        } else {
            String validCode = UserLoginInterceptor.RequestUriUtils.mdfive(password);
            if (!account.getValid().equals(validCode)) {
                result.setStatus(RespErrorCode.PASSWORDERROR.getStatus());
                result.setMessage(RespErrorCode.PASSWORDERROR.getMessage());
//                result.setStatus(RespErrorCode.ERROR.getStatus());
//                result.setMessage("The login password is incorrect");
                return result;
            }
            account.setValid(null);
            request.getSession().setAttribute("user", account);
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMessage(RespErrorCode.OK.getMessage());
        }
        result.setData(account);
        return result;
    }

    @GetMapping("/userLogout")
    public RespResult<String> userLogout(@RequestHeader("token") String token, HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        RespResult result = new RespResult<String>();
        result.setData("Log out successfully");
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

    @GetMapping("/tokenInvail")
    public RespEmptyResult tokenInvail(HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        RespEmptyResult result = new RespEmptyResult();
        result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
        result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/userRegister")
    public RespResult<Account> userRegister(String username, String password) {
        RespResult<Account> result = new RespResult<Account>();
        if (username.isEmpty() || password.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The format of the user name or password is incorrect");
            return result;
        }

        if (!PatternMatcher.textInputPass(username, PatterRegexType.USERNAME)) {
            result.setStatus(RespErrorCode.USERNAMEERROR.getStatus());
            result.setMessage(RespErrorCode.USERNAMEERROR.getMessage());
//            result.setMessage("The format of the user name is incorrect");
            return result;
        }
        else if (!PatternMatcher.textInputPass(password, PatterRegexType.PASSWORD)) {
            result.setStatus(RespErrorCode.PASSWORDERROR.getStatus());
            result.setMessage(RespErrorCode.PASSWORDERROR.getMessage());
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
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMessage(RespErrorCode.OK.getMessage());
            return result;

        } else {
            result.setStatus(RespErrorCode.ERROR.getStatus());
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
            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }
        account.setToken(null);
        request.getSession().setAttribute("user", account);
        RespResult<Account> result = new RespResult<Account>();
        account.setPhone(null);
        account.setValid(null);
        result.setData(account);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

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
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

    public Account tokenIsVaild(HttpServletRequest request) {
        String token = request.getHeader("token");
        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("token", token);
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
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
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
            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
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
        result.setData(pagePhoto);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
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
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
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
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoSearch")
    public RespResult<Map<String, List<Photo>>> searchPhotos(String text) {
        RespResult<Map<String, List<Photo>>> result = new RespResult<>();
        if (text.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("Please enter the text you want to search for");
            return result;
        }
        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
        query.like("title", text);
        List<Photo> cates = photoMapper.selectList(query);
        Map<String, List<Photo>> maps = new HashMap<>();
        maps.put("list", cates);
        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoCollect")
    @PostMapping("/photoCollect")
    public RespResult<Map<String, Boolean>> collectOperation(@RequestHeader("token") String token, HttpServletRequest request, Long photoId, Boolean collect) {
        RespResult<Map<String, Boolean>> result = new RespResult<>();
        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }

        if (photoId == null || collect == null) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("Parameter exception");
            return result;
        }

        Photo photo = photoMapper.selectById(photoId);
        if (photo == null) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
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
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(message);
        return  result;
    }
    
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/myPhotoCollects")
    public RespResult<Map<String, Object>> myCollectList(@RequestHeader("token") String token,
                                                              HttpServletRequest request,
                                                              Integer resourceType,
                                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "5") Integer size) {

        RespResult<Map<String, Object>> result = new RespResult<>();
        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }

        Page<PhotoCollect> ipage = new Page<>(page, size);

        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
//        query.eq("resource_type", resourceType);
        query.eq("user_id", account.getId());
        IPage<PhotoCollect> collects = photoCollectMapper.selectPage(ipage, query);
        List<Long> listIds = CommonTool.mapPhotoIds(collects.getRecords());
        List<Photo> list = photoMapper.selectBatchIds(listIds);

        Map<String, Object> maps = new HashMap<>();
        maps.put("records", list);
        maps.put("total", collects.getTotal());
        maps.put("current", collects.getCurrent());
        maps.put("pages", collects.getPages());
        maps.put("size", collects.getSize());

        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

//    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
//    @GetMapping("/myPhotoCollects")
//    public RespResult<IPage<Photo>> myCollectList(@RequestHeader("token") String token, HttpServletRequest request, Integer resourceType,
//                                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
//                                                              @RequestParam(value = "size", defaultValue = "5") Integer size) {
//
//        RespResult<Map<String, List<Photo>>> result = new RespResult<>();
//        Account account = tokenIsVaild(request);
//        if (account == null) {
//            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
//            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
//            return result;
//        }
//
//        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
//        query.eq("user_id", account.getId());
//        List<PhotoCollect> collects = photoCollectMapper.selectList(query);
//        List<Long> listIds = CommonTool.mapPhotoIds(collects);
//        Map<String, List<Photo>> maps = new HashMap<>();
//
//        if (!listIds.isEmpty()) {
//            List<Photo> list = photoMapper.selectBatchIds(listIds);
//            for (Photo otp : list) {
//                otp.setCollect(true);
//            }
//            maps.put("list", list);
//        } else {
//            maps.put("list", new ArrayList<Photo>());
//        }
//        result.setData(maps);
//        result.setStatus(RespErrorCode.OK.getStatus());
//        result.setMessage(RespErrorCode.OK.getMessage());
//        return  result;
//
//        //    public RespResult<IPage<Photo>> getPhotoList(@RequestParam(value = "id", required = false) Integer id,
////                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
////                                                 @RequestParam(value = "size", defaultValue = "5") Integer size) {
////        RespResult<IPage<Photo>> result = new RespResult<>();
////        Account account = tokenIsVaild(request);
////        if (account == null) {
////            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
////            result.setMessage(RespErrorCode.INVAILTOKEN.getMessage());
////            return result;
////        }
////
////        Page<Photo> pageMap = new Page<>(page, size);
////
////        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
////        query.eq("user_id", account.getId());
////        if (resourceType != null) {
////            /// 字段需要跟数据库字段对应，不能使用驼峰 categoryId, 查询异常
////            query.eq("resource_type", resourceType);
////        }
////        List<PhotoCollect> collects = photoCollectMapper.selectPage(pageMap, query);
////        List<Long> listIds = CommonTool.mapPhotoIds(collects);
////
////        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
////        if (resourceType != null) {
////            /// 字段需要跟数据库字段对应，不能使用驼峰 categoryId, 查询异常
////            query.eq("resource_type", resourceType);
////        }
////        IPage<Photo> pagePhoto = photoMapper.selectPage(pageMap, query);
////        result.setData(pagePhoto);
////        result.setStatus(RespErrorCode.OK.getStatus());
////        result.setMessage(RespErrorCode.OK.getMessage());
////        return result;
//
//
//    }


    private List<Long>getMyCollectionPhotoIds(Account account) {
        QueryWrapper<PhotoCollect> query = new QueryWrapper<PhotoCollect>();
        query.eq("user_id", account.getId());
        List<PhotoCollect> collects = photoCollectMapper.selectList(query);
        List<Long> listIds = CommonTool.mapPhotoIds(collects);
        return listIds;
    }


    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/addVideo")
    public RespResult<Photo> addVideo(String title, String videoUrl, String cover, Long categoryId) {
        RespResult<Photo> result = new RespResult<>();

        if (title.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The title cannot be empty");
            return result;
        } else if (cover.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The video cover cannot be empty");
            return result;
        } else if (videoUrl.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The video link cannot be empty");
            return result;
        }

        Photo photo = new Photo();
        photo.setVideoUrl(videoUrl);
        photo.setIcon(cover);
        photo.setRatio(1.2F);
        photo.setResourceType(2L);
        photo.setTitle(title);

        QueryWrapper<PhotoCategory> query = new QueryWrapper<PhotoCategory>();
        List<PhotoCategory> cates = categoryMapper.selectList(query);

        Long catId = getNearPhotoCategoryId(categoryId);
        if (catId == null) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The resource cannot be added because there is no class");
            return result;
        }
        photo.setCategoryId(catId);

        photoMapper.insert(photo);
        result.setData(photo);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/addImages")
    public RespResult<Photo> addPhotoImages(String title, String images, Long categoryId) {
        RespResult<Photo> result = new RespResult<>();
        List<String> imgs = Arrays.asList(images.split(">>>"));

        if (title.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The title cannot be empty");
            return result;
        } else if (imgs.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The picture cannot be empty");
            return result;
        }
        Photo photo = new Photo();
        photo.setImages(images);
        String img = imgs.get(0);
        photo.setIcon(img);
        photo.setRatio(1F);
        photo.setResourceType(1L);
        photo.setTitle(title);

        Long catId = getNearPhotoCategoryId(categoryId);
        if (catId == null) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMessage("The resource cannot be added because there is no class");
            return result;
        }
        photo.setCategoryId(catId);
        photoMapper.insert(photo);
        result.setData(photo);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

    private Long getNearPhotoCategoryId(Long categoryId) {
        QueryWrapper<PhotoCategory> query = new QueryWrapper<PhotoCategory>();
        List<PhotoCategory> cates = categoryMapper.selectList(query);

        if (!cates.isEmpty()) {
            if (categoryId != null) {

                for (PhotoCategory catt : cates) {
                    if (catt.getId().equals(categoryId)) {
                        return categoryId;
                    }
                }

                PhotoCategory cat = cates.get(0);
                return cat.getId();
            } else {
                PhotoCategory cat = cates.get(0);
                return cat.getId();
            }
        } else  {
            return null;
        }
    }

}
