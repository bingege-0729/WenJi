package com.example.Controller.User;

import com.example.Common.Result;
import com.example.DTO.LoginDTO;
import com.example.DTO.RegisterDTO;
import com.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @Param RegisterDTO
     * @Return
     */
    @PostMapping("/register")
    public Result register(@Validated @RequestBody RegisterDTO registerDTO){
        userService.register(registerDTO);
        return Result.success();
    }

}
