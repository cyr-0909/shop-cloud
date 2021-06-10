package com.fh.shop;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.Constant;
import com.fh.shop.member.vo.MemberVo;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BaseController {

    public MemberVo buildMemberVo(HttpServletRequest request){
        String memberVoJSON = null;
        try {
            memberVoJSON = URLDecoder.decode(request.getHeader(Constant.CURR_MEMBER),"utf-8");
            MemberVo memberVo = JSON.parseObject(memberVoJSON, MemberVo.class);
            return memberVo;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
