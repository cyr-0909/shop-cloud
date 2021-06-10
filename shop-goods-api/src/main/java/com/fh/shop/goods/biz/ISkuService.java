package com.fh.shop.goods.biz;

import com.fh.shop.common.ServerResponse;

public interface ISkuService {

    ServerResponse findRecommendNewProductList();

    ServerResponse findSku(Long id);
}
