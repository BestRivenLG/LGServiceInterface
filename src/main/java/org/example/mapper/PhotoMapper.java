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

    // 不使用子查询

    @Select("SELECT p.id, p.category_id, p.icon, COALESCE(p.`collect` , TRUE) AS collect, p.ratio, p.title, p.title, p.resource_type, p.video_url, p.page_view, p.page_text, p.images, p.en_title  FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType} ORDER BY pc.create_time DESC")
//    @Select("SELECT *, (CASE WHEN p.collect IS NULL THEN true ELSE false END) AS collect FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType}")
//    @Select("SELECT *, (CASE WHEN p.is_collect IS NULL THEN true ELSE false END) AS is_collect FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType}")
//    @Select("SELECT * FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type =  #{resourceType}")
//    @Select("SELECT * FROM photos p WHERE id IN (SELECT pc.photo_id FROM photos_collect pc WHERE pc.user_id = #{userId} ) AND resource_type = #{resourceType}")
    Page<Photo> selectPageMyPhotoCollect(@Param("page") Page<Photo> page, @Param("userId") Long userId, @Param("resourceType") int resourceType);

    @Select("SELECT * FROM photos p WHERE id IN (SELECT pc.photo_id FROM photos_collect pc WHERE pc.user_id = #{userId} ) AND resource_type = #{resourceType}")
    Page<Photo> selectPagePhotoListHasCollect(@Param("page") Page<Photo> page, @Param("userId") Long userId, @Param("categoryType") int categoryType);


    @Select("SELECT p.id, p.category_id, p.icon, p.ratio, p.title, p.resource_type, p.video_url, p.page_view, p.page_text, p.en_title, p.images, p.create_time, p.update_time , COALESCE (p.`collect`, pc.user_id = #{userId}) AS collect  FROM photos p LEFT JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id = #{userId} WHERE p.category_id = #{categoryId} ORDER BY p.update_time DESC")
//    @Select("SELECT p.id, p.category_id, p.icon, p.ratio, p.title, p.resource_type, p.video_url, p.page_view, p.page_text, p.en_title, p.images, p.create_time, p.update_time , COALESCE (p.`collect`, pc.user_id = #{userId}) AS collect  FROM photos p LEFT JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id = #{userId} WHERE p.resource_type = #{resourceType} ORDER BY p.update_time DESC")
//    @Select("SELECT *, (CASE WHEN p.collect IS NULL THEN true ELSE false END) AS collect FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType}")
//    @Select("SELECT *, (CASE WHEN p.is_collect IS NULL THEN true ELSE false END) AS is_collect FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType}")
//    @Select("SELECT * FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type =  #{resourceType}")
//    @Select("SELECT * FROM photos p WHERE id IN (SELECT pc.photo_id FROM photos_collect pc WHERE pc.user_id = #{userId} ) AND resource_type = #{resourceType}")
    Page<Photo> selectPageMyPhotoList(@Param("page") Page<Photo> page, @Param("categoryId") Long categoryId, @Param("userId") Long userId);

    @Select("SELECT p.id, p.category_id, p.icon, p.ratio, p.title, p.resource_type, p.video_url, p.page_view, p.page_text, p.en_title, p.images, p.create_time, p.update_time , COALESCE (p.`collect`, pc.user_id = #{userId}) AS collect  FROM photos p LEFT JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id = #{userId} ORDER BY p.update_time DESC")
    Page<Photo> selectPageMyAllPhotoList(@Param("page") Page<Photo> page, @Param("userId") Long userId);

    @Select("SELECT p.id, p.category_id, p.icon, (pc.user_id = #{userId} AND pc.photo_id = #{id}) AS collect, p.ratio, p.title, p.resource_type, p.video_url, p.page_view, p.page_text, p.en_title, p.images, p.create_time, p.update_time AS collect FROM photos p LEFT JOIN photos_collect pc ON (p.id = pc.photo_id AND pc.user_id = #{userId}) WHERE p.id = #{id}")
    Photo selectLoginPhotoDetail(@Param("userId") Long userId, @Param("id") Long id);

}
