package com.fh.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.fh.shop.member.mapper")
public class ShopMemberApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopMemberApp.class,args);
    }
}
