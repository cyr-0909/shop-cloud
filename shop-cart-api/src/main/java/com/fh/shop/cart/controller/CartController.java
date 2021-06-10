package com.fh.shop.cart.controller;


import com.fh.shop.BaseController;
import com.fh.shop.cart.biz.ICartService;
import com.fh.shop.common.Constant;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.member.vo.MemberVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/carts")
@RestController
@Api(tags = "购物车")
public class CartController extends BaseController {

    @Resource(name = "cartService")
    private ICartService cartService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/addCart")
    @ApiOperation("购物车添加商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "商品id",dataType = "java.lang.Long",required = true),
            @ApiImplicitParam(name = "count",value = "商品数量",dataType = "java.lang.Long",required = true),
            @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse addCart(Long skuId, Long count){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.addCart(memberId,skuId,count);
    }

    @GetMapping("/findCart")
    @ApiOperation("查找购物车")
    @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    public ServerResponse findCart(){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.findCart(memberId);
    }

    @GetMapping("/findCartCount")
    @ApiOperation("查找购物车数量")
    @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    public ServerResponse findCartCount(){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.findCartCount(memberId);
    }

    @DeleteMapping("/deleteCart")
    @ApiOperation("删除商品")
    @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    public ServerResponse deleteCart(Long skuId){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.deleteCart(memberId,skuId);
    }

    @DeleteMapping("/deleteCartBatch")
    @ApiOperation("删除商品")
    @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    public ServerResponse deleteCartBatch(String ids){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.deleteCartBatch(memberId,ids);
    }


}
