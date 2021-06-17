package com.fh.shop.cate.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class Book implements Serializable {

    private String name;

    private Addr addr=new Addr();
}
