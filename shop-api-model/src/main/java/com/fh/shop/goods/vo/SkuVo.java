package com.fh.shop.goods.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuVo implements Serializable {

    private Long id;

    private String skuName;

    private String price;

    private String image;
}
