package com.cc.architecture.service;

import com.cc.architecture.dao.ProductMapper;
import com.cc.architecture.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-25 16:05:11
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product getByCode(String code) {
        return productMapper.getByCode(code);
    }
}
