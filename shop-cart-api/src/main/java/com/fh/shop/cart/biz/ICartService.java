package com.fh.shop.cart.biz;

import com.fh.shop.common.ServerResponse;

public interface ICartService {

    ServerResponse addCart(Long memberId, Long skuId, Long count);

    ServerResponse findCart(Long memberId);

    ServerResponse findCartCount(Long memberId);

    ServerResponse deleteCart(Long memberId, Long skuId);

    ServerResponse deleteCartBatch(Long memberId, String ids);
}
