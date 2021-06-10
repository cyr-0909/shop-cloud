package com.fh.shop.goods.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.goods.po.Sku;

import java.util.List;

public interface ISkuMapper extends BaseMapper<Sku> {

    List<Sku> findRecommendNewProductList();

}
