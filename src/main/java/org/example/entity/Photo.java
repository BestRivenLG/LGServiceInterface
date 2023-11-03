package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;

import java.util.List;

@TableName("photos")
public class Photo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String icon;

    private float ratio;

    private String title;

    private Boolean isCollect;

    @Getter
    private Long resourceType;

    @Getter
    private String images;

    @Getter
    private String videoUrl;

    @Getter
    private Long pageView;

    @Getter
    private String pageText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCollect() {
        return isCollect;
    }

    public void setCollect(Boolean collect) {
        isCollect = collect;
    }

    public void setResourceType(Long resourceType) {
        this.resourceType = resourceType;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setPageView(Long pageView) {
        this.pageView = pageView;
    }

    public void setPageText(String pageText) {
        this.pageText = pageText;
    }
}
