package com.example.DTO;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRegistDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String code;

    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "密码不能为空")
    private String rePassword;
    @NotBlank(message = "昵称不能为空")
    private String nickname;
}
