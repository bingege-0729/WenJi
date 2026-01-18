package com.example.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class verifyCode {
    private int id;
    private String receiver;//手机号或邮箱
    private String receiverType;//phone/email
    private String code;//6位验证码
    private String codeType;//register/login/reset
    private int used;//0-未用 1-已用
    private String expireTime;// 过期时间
    private int sendCount;// 发送次数
    private String createTime;
}
