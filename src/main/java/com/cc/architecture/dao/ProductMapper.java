package com.cc.architecture.dao;

import com.cc.architecture.model.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-24 09:48:33
 */
@Mapper
public interface ProductMapper {
    Product getByCode(String code);
}
