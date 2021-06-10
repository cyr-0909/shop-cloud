package com.fh.shop.member.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.common.Constant;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.member.mapper.IMemberMapper;
import com.fh.shop.member.po.Member;
import com.fh.shop.member.vo.MemberVo;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service("memberService")
@Transactional(rollbackFor = Exception.class)
public class IMemberServiceImpl implements IMemberService {

    @Autowired
    private IMemberMapper memberMapper;

    @Override
    public ServerResponse login(String memberName, String password) {
        //非空验证
        if (StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_IS_NULL);
        }
        //判断用户名是否存在
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("memberName",memberName);
        Member member = memberMapper.selectOne(memberQueryWrapper);
        if (member == null){
            return ServerResponse.error(ResponseEnum.MEMBERNAME_LOGIN_IS_NULL);
        }
        //判断密码是否正确
        if (!Md5Util.md5(password).equals(member.getPassword())){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_IS_NOT_SAME);
        }
        String status = member.getStatus();
        if (status.equals(Constant.INACTIVE)){
            String mail = member.getMail();
            Map<String,String> result=new HashMap<>();
            result.put("mail",mail);
            result.put("id",member.getId()+"");
            return ServerResponse.error(ResponseEnum.STATUS_IS_ZERO,result);
        }
        //=====================生成签名
        //创建memberVo
        MemberVo memberVo = new MemberVo();
        memberVo.setId(member.getId());
        memberVo.setMemberName(member.getMemberName());
        memberVo.setNickName(member.getNickName());
        //将用户信息转换为json
        String memberVoJSON = JSON.toJSONString(memberVo);
        //生成签名
        String sign = Md5Util.sign(memberVoJSON, Constant.SECRET);
        //=====================生成签名
        //转换为base64编码
        String memberJSON64 = Base64.getEncoder().encodeToString(memberVoJSON.getBytes());
        String sign64 = Base64.getEncoder().encodeToString(sign.getBytes());
        //将用户信息放入到redis
        RedisUtil.setEx(KeyUtil.buildMemberKey(member.getId()),"",Constant.REDIS_TIME);
        //将用户信息和签名拼接响应给前台
        //将用户信息分割成一个字符串
        return ServerResponse.success(memberJSON64+"."+sign64);
    }

}
