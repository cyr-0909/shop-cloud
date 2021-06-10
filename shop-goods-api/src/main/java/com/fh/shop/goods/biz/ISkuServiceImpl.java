package com.fh.shop.goods.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.goods.mapper.ISkuMapper;
import com.fh.shop.goods.po.Sku;
import com.fh.shop.goods.vo.SkuVo;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("skuService")
public class ISkuServiceImpl implements ISkuService {

    @Autowired
    private ISkuMapper skuMapper;

    @Override
    public ServerResponse findRecommendNewProductList() {
        String skuListJson = RedisUtil.get("skuList");
        if (StringUtils.isNoneEmpty(skuListJson)){
            //将json转换为java
            List<Sku> skuList = JSON.parseArray(skuListJson, Sku.class);
            //json工具包【没有对bigDecimal进行特殊处理】
            return ServerResponse.success(skuList);
        }
        //从数据库查找
        List<Sku> skuList=skuMapper.findRecommendNewProductList();
        List<SkuVo> skuVoList = skuList.stream().map(x -> {
            SkuVo skuVo = new SkuVo();
            skuVo.setId(x.getId());
            skuVo.setSkuName(x.getSkuName());
            skuVo.setPrice(x.getPrice().toString());
            skuVo.setImage(x.getImage());
            return skuVo;
        }).collect(Collectors.toList());
        //将java转换为json
        String skuVoListJson = JSON.toJSONString(skuList);
        RedisUtil.setEx("skuList",skuVoListJson,30);
        //json工具包
        return ServerResponse.success(skuVoList);
    }

    @Override
    public ServerResponse findSku(Long id) {
        Sku sku = skuMapper.selectById(id);
        return ServerResponse.success(sku);
    }


}
