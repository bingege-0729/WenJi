package com.example.VO;

import lombok.Data;

@Data
class UserInfo {
    private String nickname;
    private Integer level;
    private Integer points;
    private String avatar;
}