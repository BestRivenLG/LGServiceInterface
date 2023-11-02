package org.example.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.mapper.PhotoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("photoQryService")
public class PhotoCategoryImp extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    @Resource
    PhotoMapper photoMapper;

    @Override
    public Object findPage(Integer index, Integer pageSize) {

        photoMapper.selectById(1);
        Page<Photo> page = Page.of(index,pageSize);

        return null;
    }

}
