package com.fh.shop.cate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "user")
@Data
public class CateConfig {

    private String name;

    private Integer age;

    private Book book=new Book();


}
