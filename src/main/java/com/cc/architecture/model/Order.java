package com.cc.architecture.model;

import com.cc.architecture.annotation.FiledValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-23 19:40:31
 */
@Getter
@Setter
@ToString
public class Order {
    private Integer id;
    private String code;
    private Integer count;
    @FiledValue(sourceClassName = "productServiceImpl",sourceMethod = "getByCode",bindClass = Product.class,bindProperty = "name",param = "code")
    private String productName;
    @FiledValue(sourceClassName = "productServiceImpl",sourceMethod = "getByCode",bindClass = Product.class,bindProperty = "price",param = "code")
    private BigDecimal price;
}
