package com.example.DTO;

import lombok.Data;

@Data
public class SendVerifyCodeDTO {
    private String phone;
    private String type;
}
