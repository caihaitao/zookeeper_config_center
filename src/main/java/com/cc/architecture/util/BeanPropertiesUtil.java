package com.cc.architecture.util;

import com.alibaba.fastjson.JSON;
import com.cc.architecture.annotation.ConfigValue;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import sun.reflect.Reflection;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-28 11:04:24
 */
//@Component
public class BeanPropertiesUtil  implements InitializingBean,PriorityOrdered,BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(BeanPropertiesUtil.class);
    @Value("${server.zkUrl}")
    private String zkUrl;
    @Value("${server.zkTimeOut}")
    private Integer zkTimeOut;
    @Value("${server.id}")
    private String serviceId;
    private BeanFactory beanFactory;

    public static Map<String,Object> configMap = new HashMap<>();

    @PostConstruct
    public void init() throws Exception {
       /* System.setProperty("spring.datasource.password","123321");
        logger.info("获取配置开始……");
        ZkClient zkClient = new ZkClient(zkUrl,zkTimeOut);
        final List<String> children = zkClient.getChildren("/"+serviceId);
        logger.info("获取的子节点：{}",children.toString());
        for(String childName : children) {
            logger.info("子节点名：{}",childName);
            final Object data = zkClient.readData("/"+serviceId+"/"+childName);
            logger.info("子节点值：{}",data);
            configMap.put(childName,data);
        }*/
       configMap.put("spring.datasource.password","123321");
       configMap.put("orderNo","2018051929991");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("config map : "+ JSON.toJSONString(configMap));
        ApplicationContext applicationContext = BeanNameUtil.getApplicationContext();
        final String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : beanDefinitionNames) {
            Object bean = beanFactory.getBean(beanName);

            final Field[] declaredFields = getField(bean);
            if(declaredFields.length > 0) {
                for(Field field : declaredFields) {
                    final ConfigValue configValue = field.getAnnotation(ConfigValue.class);
                    if(configValue != null) {
                        field.setAccessible(true);
                        logger.info("field name :"+field.getName() +" field value:"+configMap.get(configValue.name()));
                        field.set(bean,configMap.get(configValue.name()));
                        logger.info("...");
                    }
                }
            }
        }
    }

    private Field[] getField(Object bean) {
        List<Field> fields = new ArrayList<>();
        Class<?> aClass = bean.getClass();
        for( ;aClass != Object.class; aClass = aClass.getSuperclass()) {
            fields.addAll(Arrays.asList(aClass.getDeclaredFields()));
        }
        return fields.toArray(new Field[fields.size()]);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
