package org.example.action.zxz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.RespErrorCode;
import org.example.entity.Account;
import org.example.entity.BannerEntity;
import org.example.entity.PhotoCategory;
import org.example.entity.RespResult;
import org.example.mapper.BannerMapper;
import org.example.mapper.PhotoCategoryMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zxz/category")
public class ZXPhototActions {

    @Resource
    BannerMapper bannerMapper;

    @Resource
    PhotoCategoryMapper categoryMapper;

    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/index")
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
}
