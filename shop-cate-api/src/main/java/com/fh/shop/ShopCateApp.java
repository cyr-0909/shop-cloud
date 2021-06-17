package com.fh.shop;

import com.fh.shop.cate.config.CateConfig;
import com.fh.shop.cate.po.Cate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.fh.shop.cate.mapper")
@EnableConfigurationProperties({CateConfig.class})
public class ShopCateApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopCateApp.class,args);
    }
}
