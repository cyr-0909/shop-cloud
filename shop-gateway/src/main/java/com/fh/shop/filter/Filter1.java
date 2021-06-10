package com.fh.shop.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

@Slf4j
public class Filter1 extends ZuulFilter {


    /***
     * pre 所有客户端请求在访问微服务 之前执行
     * route
     * post 所有客户端请求在访问微服务之后执行
     * error
     * @return
     */

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /***
     * 顺序、数值越小，优先级越高
     * @return
     */

    @Override
    public int filterOrder() {
        return 7;
    }

    /**
     * true开启过滤器
     * false关闭过滤器
     * @return
     */

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 所有业务逻辑处理
     * 永远返回null
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        log.info("============执行pre");
        return null;
    }
}
