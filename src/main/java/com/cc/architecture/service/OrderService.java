package com.cc.architecture.service;

import com.cc.architecture.dao.OrderMapper;
import com.cc.architecture.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-23 19:38:47
 */
public interface OrderService {
    Order getById(Integer id);
}
