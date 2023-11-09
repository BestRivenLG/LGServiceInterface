package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.CommonTool;
import org.example.common.RespErrorCode;
import org.example.entity.*;
import org.example.mapper.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PhotoActions {

    @Resource
    AccountMapper accountMapper;
    @Resource
    PhotoMapper photoMapper;
    @Resource
    PhotoCollectMapper photoCollectMapper;
    @Resource
    PhotoCategoryMapper categoryMapper;

    @Resource
    PageViewMapper pageViewMapper;


    /*获取图片分类*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoCategory")
    public RespResult<Map<String, List<PhotoCategory>>> getPhotoCategory() {
        RespResult<Map<String, List<PhotoCategory>>> result = new RespResult<>();
        QueryWrapper<PhotoCategory> query = new QueryWrapper<PhotoCategory>();
        List<PhotoCategory> cates = categoryMapper.selectList(query);
        Map<String, List<PhotoCategory>> maps = new HashMap<>();
        maps.put("list", cates);
        result.setData(maps);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    /*获取图片列表，未登录*/
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
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }


    /*获取图片列表，需要登录 v2版本*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoList/v2")
    public RespResult<IPage<Photo>> getPhotoListV1(HttpServletRequest request,
                                                   @RequestParam(value = "id", required = false) Integer id,
                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "5") Integer size) {
        RespResult<IPage<Photo>> result = new RespResult<>();
        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
        if (id != null) {
            /// 字段需要跟数据库字段对应，不能使用驼峰 categoryId, 查询异常
            query.eq("category_id", id);
        }
        Account account = tokenIsVaild(request);
        if (account == null) {
            Page<Photo> ipage = new Page<>(page, size);
            Page<Photo> listss = photoMapper.selectPage(ipage, query);
            result.setData(listss);
            result.setCode(RespErrorCode.OK.getCode());
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMsg(RespErrorCode.OK.getMessage());
            return result;
        }

        List<Long> photoIds = CommonTool.getMyCollectionPhotoIds(photoCollectMapper, account);
        Page<Photo> pageMap = new Page<>(page, size);
        IPage<Photo> pagePhoto = photoMapper.selectPage(pageMap, query);
        List<Photo> photoRecords = pagePhoto.getRecords();
        for(Photo oto: photoRecords) {
            if (photoIds.contains(oto.getId())) {
                oto.setCollect(true);
            }
        }
        pagePhoto.setRecords(photoRecords);
        result.setData(pagePhoto);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    /*获取图片列表，需要登录，SQL查询，优化v3版本*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoList/v3")
    public RespResult<IPage<Photo>> getPhotoListV3(HttpServletRequest request,
                                                   @RequestParam(value = "id", required = false) Long id,
                                                   @RequestParam(value = "page", defaultValue = "1") Long page,
                                                   @RequestParam(value = "size", defaultValue = "5") Long size) {
        RespResult<IPage<Photo>> result = new RespResult<>();
        Page<Photo> ipage = new Page<>(page, size);
        Account account = tokenIsVaild(request);
        if (account == null) {
            QueryWrapper<Photo> query = new QueryWrapper<Photo>();
            if (id != null) {
                /// 字段需要跟数据库字段对应，不能使用驼峰 categoryId, 查询异常
                query.eq("category_id", id);
            }
            Page<Photo> listss = photoMapper.selectPage(ipage, query);
            result.setData(listss);
            result.setCode(RespErrorCode.OK.getCode());
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMsg(RespErrorCode.OK.getMessage());
            return result;
        }
        Page<Photo> pagePhoto;
        if (id == null) {
            pagePhoto = photoMapper.selectPageMyAllPhotoList(ipage, account.getId());
        } else  {
            if (id == 4) { //最近浏览
                pagePhoto = photoMapper.selectPageMyPhotoList(ipage, id, account.getId());
            } else {
                pagePhoto = photoMapper.selectPageMyPhotoList(ipage, id, account.getId());
            }
        }
        result.setData(pagePhoto);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    /*我的收藏列表*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/myPhotoCollects")
    public RespResult<Page<Photo>> myCollectList(HttpServletRequest request,
                                                 Integer resourceType,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "5") Integer size) {
        RespResult<Page<Photo>> result = new RespResult<>();
        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
            result.setMsg(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }

        Page<Photo> ipage = new Page<>(page, size);
        Page<Photo> listss = photoMapper.selectPageMyPhotoCollect(ipage, account.getId(), resourceType);

        result.setData(listss);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    /*添加、取消图片收藏*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoCollect")
    @PostMapping("/photoCollect")
    public RespResult<Map<String, Boolean>> collectOperation(@RequestHeader("token") String token, HttpServletRequest request, Long photoId, Boolean collect) {
        RespResult<Map<String, Boolean>> result = new RespResult<>();
        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
            result.setMsg(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }

        if (photoId == null || collect == null) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("Parameter exception");
            return result;
        }

        Photo photo = photoMapper.selectById(photoId);
        if (photo == null) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("Resource does not exist");
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
        result.setCode(RespErrorCode.ERROR.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(message);
        return  result;
    }

    /*搜索图片*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/photoSearch")
    public RespResult<Map<String, List<Photo>>> searchPhotos(String text) {
        RespResult<Map<String, List<Photo>>> result = new RespResult<>();
        if (text.isEmpty()) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("Please enter the text you want to search for");
            return result;
        }
        QueryWrapper<Photo> query = new QueryWrapper<Photo>();
        query.like("title", text);
        List<Photo> cates = photoMapper.selectList(query);
        Map<String, List<Photo>> maps = new HashMap<>();
        maps.put("list", cates);
        result.setData(maps);
        result.setCode(RespErrorCode.ERROR.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("photo/detail")
    public RespResult<Photo> photoDetail(HttpServletRequest request, Long photoId) {
        RespResult<Photo> result = new RespResult<>();
        Photo photo;
        Account account = tokenIsVaild(request);
        if (account != null) {
            updatePageView(account, photoId);
            photo = updatePhotoPageView(account, photoId);
            result.setData(photo);
            result.setCode(RespErrorCode.OK.getCode());
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMsg(RespErrorCode.OK.getMessage());
            return result;
        } else {
            // 未登录
            QueryWrapper<Photo> query = new QueryWrapper<>();
            query.eq("id", photoId);
            photo = photoMapper.selectOne(query);
            if (photo == null) {
                result.setCode(RespErrorCode.ERROR.getCode());
                result.setStatus(RespErrorCode.ERROR.getStatus());
                result.setMsg("Data does not exist");
                return result;
            }
            result.setData(photo);
            result.setCode(RespErrorCode.OK.getCode());
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMsg(RespErrorCode.OK.getMessage());
            return result;
        }

    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("photo/recentlyView")
    public RespResult<IPage<Photo>> recentlyViewed(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") Long page,
                                                   @RequestParam(value = "size", defaultValue = "5") Long size) {
        RespResult<IPage<Photo>> result = new RespResult<>();
        Account account = tokenIsVaild(request);
        if (account == null) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.INVAILTOKEN.getStatus());
            result.setMsg(RespErrorCode.INVAILTOKEN.getMessage());
            return result;
        }
        Map<String, Object> maps = new HashMap<>();
        QueryWrapper<Page<Photo>> query = new QueryWrapper<>();
        Page<Photo> ipage = new Page<>(page, size);
        IPage<Photo> pagee = pageViewMapper.selectPageMyRecentlyView(ipage, account.getId());
        result.setData(pagee);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    private void updatePageView(Account account, Long photoId) {
        QueryWrapper<PageView> pageQuery = new QueryWrapper<>();
        pageQuery.eq("user_id", account.getId());
        pageQuery.eq("photo_id", photoId);
        PageView ppview = pageViewMapper.selectOne(pageQuery);
        Boolean isExit = true;
        if (ppview == null) {
            ppview = new PageView();
            isExit = false;
        }

        Long page = ppview.getPageView();
        if (page == null) {
            page = 0L;
        }
        page += 1L;
        ppview.setPageView(page);

        if (isExit) {
            pageViewMapper.updateById(ppview);
        } else {
            ppview.setPhotoId(photoId);
            ppview.setUserId(account.getId());
            pageViewMapper.insert(ppview);
        }
    }

    private Photo updatePhotoPageView(Account account, Long photoId) {
        Photo photo = photoMapper.selectLoginPhotoDetail(account.getId(), photoId);
        Long pagee = photo.getPageView();
        if (pagee == null) {
            pagee = 0L;
        }
        pagee += 1L;
        photo.setPageView(pagee);
        photo.setPageText(pagee + "");
        Boolean last = photo.getCollect();
        photo.setCollect(null);
        photoMapper.updateById(photo);
        photo.setCollect(last);
        return photo;
    }


    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/addVideo")
    public RespResult<Photo> addVideo(String title, String videoUrl, String cover, Long categoryId) {
        RespResult<Photo> result = new RespResult<>();

        if (title.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The title cannot be empty");
            return result;
        } else if (cover.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The video cover cannot be empty");
            return result;
        } else if (videoUrl.isEmpty()) {
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The video link cannot be empty");
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
            result.setMsg("The resource cannot be added because there is no class");
            return result;
        }
        photo.setCategoryId(catId);

        photoMapper.insert(photo);
        result.setData(photo);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @PostMapping("/addImages")
    public RespResult<Photo> addPhotoImages(String title, String images, Long categoryId) {
        RespResult<Photo> result = new RespResult<>();
        List<String> imgs = Arrays.asList(images.split(">>>"));

        if (title.isEmpty()) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The title cannot be empty");
            return result;
        } else if (imgs.isEmpty()) {
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The picture cannot be empty");
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
            result.setCode(RespErrorCode.ERROR.getCode());
            result.setStatus(RespErrorCode.ERROR.getStatus());
            result.setMsg("The resource cannot be added because there is no class");
            return result;
        }
        photo.setCategoryId(catId);
        photoMapper.insert(photo);
        result.setData(photo);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

    /*获取用户token*/
    private Account tokenIsVaild(HttpServletRequest request) {
        return CommonTool.tokenIsVaild(accountMapper, request);
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
