package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.BannerEntity;
import org.example.entity.Photo;

import java.util.List;

public interface BannerMapper extends BaseMapper<BannerEntity> {

    @Select("SELECT * FROM banners b WHERE b.type = 1")
    List<BannerEntity> selectBannerList();

    @Select("SELECT * FROM banners b WHERE b.type = 1")
    List<BannerEntity> selectActivity();
}
