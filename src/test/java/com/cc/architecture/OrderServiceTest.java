package com.cc.architecture;

import com.cc.architecture.service.OrderService;
import com.cc.architecture.util.ProxyUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-23 20:03:40
 */
public class OrderServiceTest extends ArchitectureApplicationTests {
    @Autowired
    private OrderService orderService;

    @Test
    public void testGetOrderById() {
        printResult(orderService.getById(1));
    }

    @Test
    public void testProxy() {
        ProxyUtil proxyUtil = new ProxyUtil(orderService);
        OrderService orderService1 = (OrderService)proxyUtil.getInstance();
        printResult(orderService1.getById(1));
    }
}
