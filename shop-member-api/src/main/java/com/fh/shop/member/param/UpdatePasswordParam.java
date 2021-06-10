package com.fh.shop.member.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordParam implements Serializable {

    private Long id;

    private String oldPassword;

    private String newPassword;

    private String confirmNewPassword;


}
