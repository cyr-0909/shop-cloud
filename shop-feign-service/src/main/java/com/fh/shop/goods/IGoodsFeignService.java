package com.fh.shop.goods;

import com.fh.shop.common.ServerResponse;
import com.fh.shop.goods.po.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "shop-goods-api")
public interface IGoodsFeignService {

    @GetMapping("/api/skus/findSku")
    public ServerResponse<Sku> findSku(@RequestParam("id") Long id);
}
