package com.example.DTO;

import lombok.Data;

@Data
public class LoginDTO {
    private String phone;
    private String code;
    private String passWord;

    private String loginType;// phone/email
}
