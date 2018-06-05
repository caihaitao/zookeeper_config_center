package com.cc.architecture.annotation;

import java.lang.annotation.*;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-26 16:00:09
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {
    String name();
    String value() default "";
}
