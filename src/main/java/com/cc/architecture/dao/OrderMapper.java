package com.cc.architecture.dao;

import com.cc.architecture.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-23 19:51:05
 */
@Mapper
public interface OrderMapper {
    Order getOrderById(@Param("id") Integer id);
}
