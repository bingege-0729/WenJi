package com.example.Controller.User;

import com.example.Common.Result;
import com.example.DTO.UserLoginDTO;
import com.example.DTO.UserRegistDTO;
import com.example.Mapper.UserMapper;
import com.example.Pojo.User;
import com.example.Service.UserService;
import com.example.VO.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;
    /**
     * 用户注册
     * @Param RegisterDTO
     * @Return
     */
    @PostMapping("/register")

    public Result register(@Validated @RequestBody UserRegistDTO userRegistDTO){
        //判断是否已注册
        User user = userMapper.selectByPhone(userRegistDTO.getPhone());
        if(user != null){
            return Result.error("该手机号已注册");
        }
        userService.register(userRegistDTO);
        return Result.success();
    }
    /**
     * 发送验证码
     * @Param phone
     * @Return
     */
    @PostMapping("/send-code")
    public Result sendCode(@RequestBody String phone){
        //生成验证码
        String code = String.valueOf((int)((Math.random()*9+1)*100000));
        //保存验证码
        //redis.set(phone, code);
        //发送验证码
        //SMSUtils.sendMessage("瑞吉外卖", "", phone, code);
        return Result.success();
    }
    /**
     * 用户登录
     * @Param LoginDTO
     * @Return
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Validated @RequestBody UserLoginDTO userLoginDTO){
        Result<UserLoginVO> result = userService.login(userLoginDTO);
        return result;
    }

}
