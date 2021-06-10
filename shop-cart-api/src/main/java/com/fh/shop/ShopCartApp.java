package com.fh.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShopCartApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopCartApp.class,args);
    }

}
