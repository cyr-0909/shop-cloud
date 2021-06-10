package com.fh.shop.member.param;

import com.fh.shop.member.po.Member;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberParam extends Member implements Serializable {

    private String confirmPassword;

    private String code;

    private Long count;
}
