package com.cc.architecture.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-24 09:45:01
 */
@Getter
@Setter
@ToString
public class Product {
    private Integer id;
    private String name;
    private String code;
    private BigDecimal price;
}
