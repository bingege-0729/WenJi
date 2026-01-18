package com.example.VO;

import lombok.Data;

@Data
class UserLoginVO {
    private Integer userId;
    private String accessToken;
    private UserInfo userInfo;
}