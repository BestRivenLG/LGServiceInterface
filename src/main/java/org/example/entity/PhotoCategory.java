package org.example.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("photos_category")
public class PhotoCategory {
    @TableId(type = IdType.AUTO)
    private Long id;

//    private String cnTitle;

    private String name;

    private int sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public String getCnTitle() {
//        return cnTitle;
//    }
//
//    public void setCnTitle(String cnTitle) {
//        this.cnTitle = cnTitle;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
