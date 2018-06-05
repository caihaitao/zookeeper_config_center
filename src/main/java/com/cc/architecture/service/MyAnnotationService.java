package com.cc.architecture.service;

import com.cc.architecture.annotation.ConfigValue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-28 18:21:52
 */
@Service
public class MyAnnotationService {

    @ConfigValue(name = "spring.datasource.password")
    private String password;
    @ConfigValue(name = "orderNo")
    private String orderNo;

    public void deSth() {
        System.err.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx:"+password);
    }

    @Scheduled(initialDelay = 1000,fixedDelay = 10000)
    private void doSth() {
        System.err.println("get OrderNo :"+orderNo);
    }
}
