package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Photo;

import java.util.List;


public interface PhotoMapper extends BaseMapper<Photo> {

    @Select("SELECT * FROM photos p WHERE id IN (SELECT pc.photo_id FROM photos_collect pc WHERE pc.user_id = #{userId} ) AND resource_type = #{resourceType}")
//    @Select("SELECT * FROM photos p WHERE id IN (SELECT pc.photo_id FROM photos_collect pc WHERE pc.user_id = #{userId} )")// AND resource_type = #{type}
    List<Photo> getPhotosCollectWithCombinedId(@Param("userId") Long userId, @Param("resourceType") int resourceType);


    @Select("SELECT * FROM photos p WHERE id IN (SELECT pc.photo_id FROM photos_collect pc WHERE pc.user_id = #{userId} ) AND resource_type = #{resourceType}")
    Page<Photo> selectPageMyPhotoCollect(@Param("page") Page<Photo> page, @Param("userId") Long userId, @Param("resourceType") int resourceType);


}
