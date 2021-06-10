package com.fh.shop.filter;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.Constant;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import com.fh.shop.vo.MemberVo;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class JWTFilter extends ZuulFilter {

    @Value("${fh.shop.checkUrls}")
    private List<String> checkUrls;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
        log.info("=========={}",checkUrls);
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();

        String requestMethod = request.getMethod();
        if (requestMethod.equalsIgnoreCase("options")){
            //不进行处理，拦截
            currentContext.setSendZuulResponse(false);
            return null;
        }


        StringBuffer requestURL = request.getRequestURL();
        boolean isCheck=false;
        for (String checkUrl : checkUrls){
            if (requestURL.indexOf(checkUrl) >0){
                isCheck=true;
                break;
            }
        }
        if (!isCheck){
            return null;
        }
        //验证
        //判断是否有头信息
        String header = request.getHeader("x-auth");
        if (StringUtils.isEmpty(header)){
            //不仅拦截了【不往后面走了】，而且还能给前台提示
            buildResponse(ResponseEnum.TOKEN_IS_MISS);
//            throw new ShopException(ResponseEnum.TOKEN_IS_MISS);
            return null;
        }
        //判断头信息是否完整
        String[] headerArr = header.split("\\.");
        if (headerArr.length !=2){
            buildResponse(ResponseEnum.TOKEN_IS_NOT_FULL);
//            throw new ShopException(ResponseEnum.TOKEN_IS_NOT_FULL);
        }
        //验签【核心】
        String memberVoJSON64 = headerArr[0];
        String signBase64 = headerArr[1];
        //进行base64解码
        String memberVoJSON = new String(Base64.getDecoder().decode(memberVoJSON64),"utf-8");
        String sign = new String(Base64.getDecoder().decode(signBase64),"utf-8");

        String newSign = Md5Util.sign(memberVoJSON, Constant.SECRET);
        //比较
        if (!newSign.equals(sign)){
            buildResponse(ResponseEnum.TOKEN_IS_FALL);
//            throw new ShopException(ResponseEnum.TOKEN_IS_FALL);
        }
        //将json转换为java对象
        MemberVo memberVo = JSON.parseObject(memberVoJSON, MemberVo.class);
        Long id = memberVo.getId();
        //判断是否过期
        if(!RedisUtil.exist(KeyUtil.buildMemberKey(id))){
            buildResponse(ResponseEnum.TOKEN_IS_TIME_OUT);
//            throw new ShopException(ResponseEnum.TOKEN_IS_TIME_OUT);
        }
        //续命
        RedisUtil.expire(KeyUtil.buildMemberKey(id),Constant.REDIS_TIME);
        //将memberVo存入request
//        request.setAttribute(Constant.CURR_MEMBER,memberVo);
        currentContext.addZuulRequestHeader(Constant.CURR_MEMBER, URLEncoder.encode(memberVoJSON,"utf-8"));
        //放行
        return null;
    }

    private void buildResponse(ResponseEnum responseEnum) {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        response.setContentType("application/json;charset=utf-8");
        currentContext.setSendZuulResponse(false); //不会再由路由器转发
        ServerResponse error = ServerResponse.error(responseEnum);
        String res = JSON.toJSONString(error);
        currentContext.setResponseBody(res);
    }
}
