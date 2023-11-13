package org.example.action.zxz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.CommonTool;
import org.example.common.RespErrorCode;
import org.example.entity.*;
import org.example.mapper.AccountMapper;
import org.example.mapper.BannerMapper;
import org.example.mapper.PhotoCategoryMapper;
import org.example.mapper.PhotoMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ZXPhototActions {

    @Resource
    BannerMapper bannerMapper;
    @Resource
    PhotoCategoryMapper categoryMapper;
    @Resource
    AccountMapper accountMapper;
    @Resource
    PhotoMapper photoMapper;

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/zxz/category/index")
    public RespResult<Map<String, Object>> homeIndex(HttpServletRequest request) {
        QueryWrapper<BannerEntity> query = new QueryWrapper<BannerEntity>();
        query.eq("type", 1);
        List<BannerEntity> banners = bannerMapper.selectList(query);

        QueryWrapper<PhotoCategory> query1 = new QueryWrapper<PhotoCategory>();
        List<PhotoCategory> cates = categoryMapper.selectList(query1);

        QueryWrapper<BannerEntity> query2 = new QueryWrapper<BannerEntity>();
        query2.eq("type", 3);
        BannerEntity banner = bannerMapper.selectOne(query2);

        Map<String, Object> maps = new HashMap<>();
        maps.put("banner", banners);
        maps.put("category", cates);
        maps.put("popup", banner);
        RespResult<Map<String, Object>> result = new RespResult<>();
        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setCode(RespErrorCode.OK.getCode());
        result.setMsg(RespErrorCode.OK.getMessage());
        return  result;
    }

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/resource/home")
    public RespResult<IPage<Photo>> getPhotoListV3(HttpServletRequest request,
                                                   @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                   @RequestParam(value = "page", defaultValue = "1") Long page,
                                                   @RequestParam(value = "size", defaultValue = "5") Long size) {
        RespResult<IPage<Photo>> result = new RespResult<>();
        Page<Photo> ipage = new Page<>(page, size);
        Account account = CommonTool.tokenIsVaild(accountMapper, request);
        if (account == null) {
            QueryWrapper<Photo> query = new QueryWrapper<Photo>();
            if (categoryId != null) {
                /// 字段需要跟数据库字段对应，不能使用驼峰 categoryId, 查询异常
                query.eq("category_id", categoryId);
            }
            Page<Photo> listss = photoMapper.selectPage(ipage, query);
            result.setData(listss);
            result.setCode(RespErrorCode.OK.getCode());
            result.setStatus(RespErrorCode.OK.getStatus());
            result.setMsg(RespErrorCode.OK.getMessage());
            return result;
        }
        Page<Photo> pagePhoto;
        if (categoryId == null) {
            pagePhoto = photoMapper.selectPageMyAllPhotoList(ipage, account.getId());
        } else  {
            if (categoryId == 4) { //最近浏览
                pagePhoto = photoMapper.selectPageMyPhotoList(ipage, categoryId, account.getId());
            } else {
                pagePhoto = photoMapper.selectPageMyPhotoList(ipage, categoryId, account.getId());
            }
        }
        result.setData(pagePhoto);
        result.setCode(RespErrorCode.OK.getCode());
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMsg(RespErrorCode.OK.getMessage());
        return result;
    }

}
