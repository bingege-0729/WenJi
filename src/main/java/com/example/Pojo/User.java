package com.example.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotBlank
    private String username;
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
