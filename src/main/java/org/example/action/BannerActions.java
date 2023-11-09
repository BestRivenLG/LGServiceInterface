package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.RespErrorCode;
import org.example.entity.BannerEntity;
import org.example.entity.RespResult;
import org.example.mapper.BannerMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BannerActions {

    @Resource
    BannerMapper bannerMapper;

    /*banner 列表*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/bannerList")
    public RespResult<Map<String, List<BannerEntity>>> getBannerList(Integer id) {
        RespResult<Map<String, List<BannerEntity>>> result = new RespResult<>();
        QueryWrapper<BannerEntity> query = new QueryWrapper<BannerEntity>();
        query.eq("type", 1);
        List<BannerEntity> banners = bannerMapper.selectList(query);
        Map<String, List<BannerEntity>> maps = new HashMap<>();
        maps.put("list", banners);
        result.setData(maps);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

    /*活动弹框*/
    @CrossOrigin(origins = "*") // 设置允许来自任何源的跨域请求
    @GetMapping("/activity")
    public RespResult<BannerEntity> getActivityList() {
        RespResult<BannerEntity> result = new RespResult<>();
        QueryWrapper<BannerEntity> query = new QueryWrapper<BannerEntity>();
        query.eq("type", 3);
        BannerEntity banner = bannerMapper.selectOne(query);
        Map<String, List<BannerEntity>> maps = new HashMap<>();
        result.setData(banner);
        result.setStatus(RespErrorCode.OK.getStatus());
        result.setMessage(RespErrorCode.OK.getMessage());
        return result;
    }

}
