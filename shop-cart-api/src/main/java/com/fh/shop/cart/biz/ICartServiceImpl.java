package com.fh.shop.cart.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.cart.vo.CartItemVo;
import com.fh.shop.cart.vo.CartVo;
import com.fh.shop.common.Constant;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.goods.IGoodsFeignService;
import com.fh.shop.goods.po.Sku;
import com.fh.shop.util.BigdacimalUtil;
import com.fh.shop.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("cartService")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ICartServiceImpl implements ICartService {

    @Autowired
    private IGoodsFeignService goodsFeignService;
    @Value("${count.limit}")
    private int countLimit;

    @Override
    public ServerResponse addCart(Long memberId, Long skuId, Long count) {
        //购买商品不超过十个
        if (count > countLimit){
            return ServerResponse.error(ResponseEnum.BUY_GOODS_LIMIT);
        }
        //判断有没有这个商品
        ServerResponse<Sku> skuResponse = goodsFeignService.findSku(skuId);
        log.info("==========={}",skuResponse);
        Sku sku = skuResponse.getData();
        if (sku == null){
            return ServerResponse.error(ResponseEnum.GOODS_IS_NOT_EXIST);
        }
        // 判断 商品是否上架
        if (sku.getStatus().equals(Constant.STATUS_DOWN)){
            return ServerResponse.error(ResponseEnum.GOODS_STATUS_IS_ERROR);
        }
        // 库存要大于等于商品数量
        Integer stock = sku.getStock();
        if (stock.intValue() < count){
            return ServerResponse.error(ResponseEnum.GOODS_STOCK_NOT);
        }
        //判断会员是否有购物车
        String key = KeyUtil.buildCartKey(memberId);
        boolean exist = RedisUtil.exist(key);
        //没有购物车的话，先创建购物车，然后直接将商品添加进去
        if (!exist){
            //如果有购物车，没有该商品
            if (count <0){
                return ServerResponse.error(ResponseEnum.BUY_GOODS_ERROR);
            }
            CartVo cartVo = new CartVo();
            CartItemVo cartItemVo = new CartItemVo();
            Long id = sku.getId();
            cartItemVo.setSkuName(sku.getSkuName());
            cartItemVo.setCount(count);
            cartItemVo.setImage(sku.getImage());
            cartItemVo.setSkuId(id);
            String price = sku.getPrice().toString();
            cartItemVo.setPrice(price);
            BigDecimal subPrice = BigdacimalUtil.mul(price, count + "");
            cartItemVo.setSubPrice(subPrice.toString());
            cartVo.getCartItemVoList().add(cartItemVo);
            cartVo.setTotalCount(count);
            cartVo.setTotalPrice(cartItemVo.getSubPrice());
            //更新购物车
            //RedisUtil.set(key,JSON.toJSONString(cartVo));
            RedisUtil.hset(key,Constant.CART_FILED_JSON,JSON.toJSONString(cartVo));
            RedisUtil.hset(key,Constant.CART_FILED_COUNT,cartVo.getTotalCount()+"");
        }else {
            String cartJson = RedisUtil.hget(key, Constant.CART_FILED_JSON);
            //如果有购物车
            //从redis中取出购物车
            CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
            //获取cartItemVoList数据
            List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
            Optional<CartItemVo> item = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
            if (item.isPresent()){
                // 购物车中有该商品，有就直接增加数量，更新小计
                CartItemVo cartItemVo = item.get();
                long itemCount = cartItemVo.getCount() + count;
                //最多购买十个商品
                if (itemCount > countLimit){
                    return ServerResponse.error(ResponseEnum.BUY_GOODS_LIMIT);
                }
                if (itemCount <=0){
                    cartItemVoList.removeIf(x ->x.getSkuId().longValue()==skuId.longValue());
                    if (cartItemVoList.size()==0){
                        //如果购物车里面没有东西，把整个购物车干掉
                        RedisUtil.del(key);
                        return ServerResponse.success();
                    }
                    //更新购物车
                    updateCart(key, cartVo);
                    return ServerResponse.success();
                }
                cartItemVo.setCount(cartItemVo.getCount()+count);
                BigDecimal subPrice = new BigDecimal(cartItemVo.getSubPrice());
                String subPriceStr = subPrice.add(BigdacimalUtil.mul(cartItemVo.getPrice(), count + "")).toString();
                cartItemVo.setSubPrice(subPriceStr);
                //更新购物车
                updateCart(key, cartVo);

            }else {
                if (count <0){
                    return ServerResponse.error(ResponseEnum.BUY_GOODS_ERROR);
                }
                // 购物车没有该商品，添加进去
                CartItemVo cartItemVo = new CartItemVo();
                Long id = sku.getId();
                cartItemVo.setSkuName(sku.getSkuName());
                cartItemVo.setCount(count);
                cartItemVo.setImage(sku.getImage());
                cartItemVo.setSkuId(id);
                String price = sku.getPrice().toString();
                cartItemVo.setPrice(price);
                BigDecimal subPrice = BigdacimalUtil.mul(price, count + "");
                cartItemVo.setSubPrice(subPrice.toString());
                cartVo.getCartItemVoList().add(cartItemVo);
                updateCart(key, cartVo);
            }
        }
        return ServerResponse.success();
    }

    //更新购物车方法
    private void updateCart(String key, CartVo cartVo) {
        //更新购物车
        List<CartItemVo> cartItemVos = cartVo.getCartItemVoList();
        long totalCount=0;
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartItemVo itemVo : cartItemVos) {
            totalCount += itemVo.getCount();
            totalPrice = totalPrice.add(new BigDecimal(itemVo.getSubPrice()));
        }
        cartVo.setTotalCount(totalCount);
        cartVo.setTotalPrice(totalPrice.toString());
        //更新购物车【redis中的购物车】
        RedisUtil.hset(key,Constant.CART_FILED_JSON,JSON.toJSONString(cartVo));
        RedisUtil.hset(key,Constant.CART_FILED_COUNT,cartVo.getTotalCount()+"");
    }

    @Override
    public ServerResponse findCart(Long memberId) {
        String cartJson = RedisUtil.hget(KeyUtil.buildCartKey(memberId),Constant.CART_FILED_JSON);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        return ServerResponse.success(cartVo);
    }

    @Override
    public ServerResponse findCartCount(Long memberId) {
        String count = RedisUtil.hget(KeyUtil.buildCartKey(memberId),Constant.CART_FILED_COUNT);
        return ServerResponse.success(count);
    }

    @Override
    public ServerResponse deleteCart(Long memberId, Long skuId) {
        //获取会员对应的购物车
        String key = KeyUtil.buildCartKey(memberId);
        String cartJson = RedisUtil.hget(key, Constant.CART_FILED_JSON);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Optional<CartItemVo> itemVo = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
        if (!itemVo.isPresent()){
            return ServerResponse.error(ResponseEnum.BUY_GOODS_ERROR);
        }
        cartItemVoList.removeIf(x ->x.getSkuId().longValue()==skuId.longValue());
        if (cartItemVoList.size()==0){
            RedisUtil.del(key);
            return ServerResponse.success();
        }
        //更新数据库
        updateCart(key, cartVo);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteCartBatch(Long memberId,String ids) {
        if (StringUtils.isEmpty(ids)){
            return ServerResponse.error(ResponseEnum.BUY_GOODS_BATCH_ERROR);
        }
        //获取会员对应的购物车
        String key = KeyUtil.buildCartKey(memberId);
        String cartJson = RedisUtil.hget(key, Constant.CART_FILED_JSON);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Arrays.stream(ids.split(",")).forEach(x ->cartItemVoList.removeIf(y ->y.getSkuId().longValue()==Long.parseLong(x)));
        if (cartItemVoList.size()==0){
            RedisUtil.del(key);
            return ServerResponse.success();
        }
        //更新购物车
        updateCart(key,cartVo);
        return ServerResponse.success();
    }
}