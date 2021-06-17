package com.fh.shop.member.controller;

import com.fh.shop.BaseController;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.member.biz.IMemberService;
import com.fh.shop.member.vo.MemberVo;
import com.fh.shop.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/member")
@Api(tags = "用户登录")
public class MemberController extends BaseController {

    @Resource(name = "memberService")
    private IMemberService memberService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/login")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName",value = "会员名",dataType = "java.lang.String",required = true),
            @ApiImplicitParam(name="password",value = "密码",dataType = "java.lang.String",required = true)
    })
    public ServerResponse login(String memberName,String password){
        return memberService.login(memberName,password);
    }

    @GetMapping("/findMember")
    @ApiOperation("获取会员信息")
    @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    public ServerResponse findMember(){
//        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%");
//        MemberVo memberVo = (MemberVo) request.getAttribute(Constant.CURR_MEMBER);
        MemberVo memberVo = buildMemberVo(request);
        return ServerResponse.success(memberVo);
    }

    @GetMapping("/loginOut")
    public ServerResponse loginOut(){
//        MemberVo memberVo = (MemberVo) request.getAttribute(Constant.CURR_MEMBER);
        MemberVo memberVo = buildMemberVo(request);
        RedisUtil.del(KeyUtil.buildMemberKey(memberVo.getId()));
        return ServerResponse.success();
    }

}
