package com.cc.architecture.util;

import com.cc.architecture.annotation.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
@Component
public class ConfigAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware, InitializingBean {

    public static Logger logger = LoggerFactory.getLogger(ConfigAnnotationBeanPostProcessor.class);

    public static Map<ConfigurableListableBeanFactory, List<String>> beanNames = new HashMap<>(2);

    // 优先级要小于 AutowiredAnnotationBeanPostProcessor PostProcessorRegistrationDelegate
    private int order = Ordered.LOWEST_PRECEDENCE;

    private ConfigurableListableBeanFactory beanFactory;

    private static TypeConverter typeConverter;

    public static Map<String,Object> configMap = new HashMap<>();

    /*@Value("${server.id}")
    private String serviceId;*/

   /* @Autowired
    private ZKUtil zkUtil;*/

    /*static {
        configMap.put("spring.datasource.password","123321");
        configMap.put("orderNo","2018051929991");
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        beanNames.put(beanFactory, new ArrayList<String>(16));
        loadConfigs();
    }

    public static void loadConfigs() {
        final String serviceId = ZKUtil.serverId;
        logger.info("获取配置开始……");
        final List<String> children = ZKUtil.getChildren("/"+serviceId);
        logger.info("获取的子节点：{}",children.toString());
        for(String childName : children) {
            logger.info("子节点名：{}",childName);
            final Object data = ZKUtil.getValue("/"+serviceId+"/"+childName);
            logger.info("子节点值：{}",data);
            configMap.put(childName,data);
        }
        ZKUtil.dataChangeListener("/"+serviceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
                                                    String beanName) throws BeansException {
        // 兼容value原生注解可以继续使用
        /*if (!ConfigBeanFactoryPostProcessor.isOpen) {
            return pvs;
        }*/
        List<Field> fields = getFields(bean.getClass());
        boolean isExist = false;
        try {
            // 遍历bean中所有属性，看是否带有ConfigFieldAnnotion注解
            for (Field field : fields) {
                field.setAccessible(true);
                ConfigValue annotion = field.getAnnotation(ConfigValue.class);
                if (null != annotion) {
                    Object newValue = doConverter(bean, annotion, field);
                    isExist = true;
                    logger.info("configCenter process: ({} -> {} -> {})", beanName, field.getName(), newValue);
                }
            }
            if (isExist) {
                beanNames.get(beanFactory).add(beanName);
            }
        } catch (Exception e) {
            logger.error("配置中心替换属性值出现异常，beanName:{}", beanName, e);
            // 如果系统启动时候报错，需要把异常抛到上层，从而终止服务的启动。原则上如果系统启动调用配置中心报错是不允许成功的。
            throw new BeansException("配置中心替换属性值出现异常,请检查配置项！") {
                private static final long serialVersionUID = 1L;
            };
        }
        return pvs;
    }

    // 动态更新配置参数
    public static void afterProcess() {
        try {
            if (!beanNames.isEmpty()) {
                Iterator<Map.Entry<ConfigurableListableBeanFactory, List<String>>> iter = beanNames.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<ConfigurableListableBeanFactory, List<String>> entry = iter.next();
                    ConfigurableListableBeanFactory factory = entry.getKey();
                    Iterator<String> beanNameIter = entry.getValue().iterator();
                    // 遍历每个上下文中所有需要替换属性值的bean
                    while (beanNameIter.hasNext()) {
                        String beanName = beanNameIter.next();
                        // spring用代理对象包装的bean需要获取target object
                        Object bean = AopTargetUtils.getTarget(factory.getBean(beanName));
                        List<Field> fields = getFields(bean.getClass());
                        for (Field field : fields) {
                            field.setAccessible(true);
                            ConfigValue annotion = field.getAnnotation(ConfigValue.class);
                            if (null != annotion) {
                                Object newValue = doConverter(bean, annotion, field);
                                logger.info("configCenter dynamic process: ({} -> {} -> {})", beanName, field.getName(),
                                        newValue);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("配置中心动态替换属性值出现异常", e);
        }
    }

    private static Object doConverter(Object bean, ConfigValue annotion, Field field) throws Exception {
        String value = configMap.get(annotion.name()).toString();
        value = (null == value) ? annotion.value() : value;
        Object newValue = typeConverter.convertIfNecessary(value, field.getType(), field);
        field.set(bean, newValue);
        return newValue;
    }

    private static List<Field> getFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>(16);
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            list.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        typeConverter = this.beanFactory.getTypeConverter();
        if (null == typeConverter) {
            typeConverter = new SimpleTypeConverter();
        }
    }

}
