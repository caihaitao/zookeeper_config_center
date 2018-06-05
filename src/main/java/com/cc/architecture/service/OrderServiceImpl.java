package com.cc.architecture.service;

import com.cc.architecture.annotation.ConfigValue;
import com.cc.architecture.dao.OrderMapper;
import com.cc.architecture.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-25 15:16:18
 */
@Service
public class OrderServiceImpl implements OrderService{

    @ConfigValue(name = "orderNo")
    private static String orderNo;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order getById(Integer id) {
        System.err.println("xxxxxxxxxx====>orderNo:"+orderNo);
        return orderMapper.getOrderById(id);
    }
}
