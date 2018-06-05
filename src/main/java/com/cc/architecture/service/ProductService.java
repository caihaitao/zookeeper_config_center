package com.cc.architecture.service;

import com.cc.architecture.model.Product;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-24 09:48:19
 */
public interface ProductService {
    Product getByCode(String code);
}
