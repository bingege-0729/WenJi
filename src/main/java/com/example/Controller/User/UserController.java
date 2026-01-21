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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
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
        log.info("用户注册：{}",userRegistDTO);
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
        log.info("用户登录：{}",userLoginDTO);
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
        log.info("获取用户信息：{}",userId);
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
        log.info("修改用户信息：{}",userUpdateDTO);
        userService.updateById(userId, userUpdateDTO);
        return Result.success("信息更新成功");
    }

    /**
     * 修改密码
     * @Param userId
     * @Param oldPassword
     * @Param newPassword
     * @Param rePassword
     * @Return
     */
    @PutMapping("/updatePassword/{userId}")
    public Result updatePassword(@PathVariable Integer userId, @RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String rePassword){
        if(userMapper.selectByUserId(userId)==null){
            return Result.error("用户不存在");
        }
        log.info("修改密码：{}",userId);
        userService.updatePassword(userId, oldPassword, newPassword, rePassword);
        return Result.success("密码修改成功");
    }

}
