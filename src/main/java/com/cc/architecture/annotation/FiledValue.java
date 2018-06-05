package com.cc.architecture.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-25 11:31:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FiledValue {
    String sourceMethod();

    String sourceClassName();

    Class bindClass();

    String bindProperty();

    String param() default "";
}
