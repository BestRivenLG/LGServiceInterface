package org.example.entity;

import com.baomidou.mybatisplus.extension.service.IService;

public interface PhotoService extends IService<Photo> {
    Object findPage(Integer index, Integer pageSize);
}

