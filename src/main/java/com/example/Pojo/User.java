package com.example.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String phone;
    @Email
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private int points;
    private String level;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String lastLoginTime;

}
