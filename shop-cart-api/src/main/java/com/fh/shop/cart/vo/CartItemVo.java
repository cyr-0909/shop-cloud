package com.fh.shop.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItemVo implements Serializable {

    private String skuName;

    private Long skuId;

    private String price;

    private Long count;

    private String subPrice;

    private String image;

}
