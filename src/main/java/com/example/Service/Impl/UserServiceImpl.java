package com.example.Service.Impl;

import com.example.Common.Result;
import com.example.DTO.UserLoginDTO;
import com.example.DTO.UserRegistDTO;
import com.example.Mapper.UserMapper;
import com.example.Pojo.User;
import com.example.Service.UserService;
import com.example.Utils.JwtUtils;
import com.example.Utils.Md5Util;
import com.example.VO.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    @Transactional
    public void register(UserRegistDTO userRegistDTO) {
        //两次确认密码
        String rePassword = userRegistDTO.getRePassword();
        if(!rePassword.equals(userRegistDTO.getPassword())){
            throw new RuntimeException("两次输入的密码不一致");
        }
        String Password = Md5Util.getMD5String(userRegistDTO.getPassword());

        userMapper.register(userRegistDTO.getPhone(), userRegistDTO.getNickname(), Password);
    }

    /**
     * 用户登录
     * @Param LoginDTO
     * @Return
     */
    @Override
    public Result<UserLoginVO> login(UserLoginDTO userLoginDTO) {
        User user = userMapper.selectByPhone(userLoginDTO.getPhone());
        if(user == null){
            return Result.error("用户不存在");
        }
        String newPassword = Md5Util.getMD5String(userLoginDTO.getPassword());
        //密码错误
        if(!newPassword.equals(user.getPassword())){
            return Result.error("密码错误");
        }
        //创建包含用户信息的Map生成Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("phone", userLoginDTO.getPhone());
        String token = JwtUtils.generateToken(claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .userId(user.getId())
                .accessToken(token)
                .lastLoginTime(LocalDateTime.now())
                .build();
        return Result.success(userLoginVO);
    }

}
