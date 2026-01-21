package com.example.Controller.User;

import com.example.Common.Result;
import com.example.DTO.UserLoginDTO;
import com.example.DTO.UserRegistDTO;
import com.example.DTO.UserUpdateDTO;
import com.example.Mapper.UserMapper;
import com.example.Pojo.User;
import com.example.Service.UserService;
import com.example.VO.UserInfoVO;
import com.example.VO.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
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
        User user = userMapper.selectByUserName(userRegistDTO.getUsername());
        if(user != null){
            return Result.error("用户名已被注册");
        }
        userService.register(userRegistDTO);
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

    /**
     * 获取用户信息
     * @Param userId
     * @Return
     */
    @GetMapping("/info/{userId}")
    public Result getById(@PathVariable Integer userId){
        UserInfoVO userInfo = userService.getByUserId(userId);
        return Result.success(userInfo);
    }

    /**
     * 修改用户信息
     * @Param userId
     * @Return
     */
    @PutMapping("/update/{userId}")
    public Result update(@PathVariable Integer userId,@Validated @RequestBody UserUpdateDTO userUpdateDTO){
        userService.updateById(userId, userUpdateDTO);
        return Result.success("信息更新成功");
    }
}
