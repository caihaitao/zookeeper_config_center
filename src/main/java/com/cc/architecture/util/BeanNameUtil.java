package com.cc.architecture.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-24 18:49:23
 */
@Component
public class BeanNameUtil implements ApplicationContextAware {
    public static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(System.err::println);
        BeanNameUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }
}
