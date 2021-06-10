package com.fh.shop.member.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class Member implements Serializable {

    @ApiModelProperty(value = "会员id",example = "0")
    private Long id;

    @ApiModelProperty(value = "会员账号",example = "0")
    private String memberName;
    @ApiModelProperty(value = "会员密码",example = "0")
    private String password;
    @ApiModelProperty(value = "会员名",example = "0")
    private String nickName;
    @ApiModelProperty(value = "会员电话",example = "0")
    private String phone;
    @ApiModelProperty(value = "会员邮箱",example = "0")
    private String mail;
    @ApiModelProperty(value = "会员状态",example = "0")
    private String status;//0:未激活；1:已激活
    @ApiModelProperty(value = "会员积分",example = "0")
    private Integer grade;
}
