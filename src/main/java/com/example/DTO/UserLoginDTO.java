package com.example.DTO;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String phone;//手机号
    private String code;//验证码
    private String password;//密码

  //  private String loginType;// phone/email
}
