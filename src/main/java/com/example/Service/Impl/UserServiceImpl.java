package com.example.Service.Impl;

import com.example.DTO.LoginDTO;
import com.example.DTO.RegisterDTO;
import com.example.Mapper.UserMapper;
import com.example.Pojo.User;
import com.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    /**
     * 用户注册/登录
     * @Param RegisterDTO
     * @Return
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        //注册的时候要判断数据库里是否有存储该账号
        User user = userMapper.selectByPhone(registerDTO.getPhone());
        if(user != null){
            throw new RuntimeException("该手机号已注册");
        }
        String newPassword = registerDTO.getPassword();
        registerDTO.setPassword(newPassword);
        userMapper.register(registerDTO.getPhone(), registerDTO.getNickname(), registerDTO.getPassword());
    }

}
