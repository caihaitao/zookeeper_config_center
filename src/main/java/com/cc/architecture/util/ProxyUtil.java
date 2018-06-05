package com.cc.architecture.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-25 13:51:07
 */
public class ProxyUtil implements InvocationHandler {

    private Object object;

    public ProxyUtil(Object object) {
        this.object = object;
    }

    public Object getInstance() {
       return Proxy.newProxyInstance(object.getClass().getClassLoader(),object.getClass().getInterfaces(),this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(object,args);
    }
}
