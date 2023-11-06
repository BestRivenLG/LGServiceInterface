package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Account;
import org.example.entity.PageView;
import org.example.entity.Photo;

public interface PageViewMapper extends BaseMapper<PageView> {


    @Select("SELECT p.id, p.category_id, COALESCE(p.`collect` , pc.user_id  = pv.user_id) AS collect, p.icon, p.ratio, p.title, p.title, p.resource_type, p.video_url, p.page_view, p.page_text, p.images, p.en_title  FROM photos p JOIN page_view pv ON p.id = pv.photo_id JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id = pv.user_id WHERE  pv.user_id  = #{userId} ORDER BY pv.update_time DESC")
//    @Select("SELECT p.id, p.category_id, p.icon, COALESCE(p.`collect` , TRUE) AS collect, p.ratio, p.title, p.title, p.resource_type, p.video_url, p.page_view, p.page_text, p.images, p.en_title  FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType} ORDER BY pc.create_time DESC")
//    @Select("SELECT *, (CASE WHEN p.collect IS NULL THEN true ELSE false END) AS collect FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType}")
//    @Select("SELECT *, (CASE WHEN p.is_collect IS NULL THEN true ELSE false END) AS is_collect FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type = #{resourceType}")
//    @Select("SELECT * FROM photos p JOIN photos_collect pc ON p.id = pc.photo_id AND pc.user_id  = #{userId} WHERE p.resource_type =  #{resourceType}")
//    @Select("SELECT * FROM photos p WHERE id IN (SELECT pc.photo_id FROM photos_collect pc WHERE pc.user_id = #{userId} ) AND resource_type = #{resourceType}")
    Page<Photo> selectPageMyRecentlyView(@Param("page") Page<Photo> page, @Param("userId") Long userId);


}
