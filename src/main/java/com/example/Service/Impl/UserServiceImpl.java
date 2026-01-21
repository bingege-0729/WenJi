package com.example.Service.Impl;

import com.example.Common.Result;
import com.example.DTO.UserLoginDTO;
import com.example.DTO.UserRegistDTO;
import com.example.DTO.UserUpdateDTO;
import com.example.Mapper.UserMapper;
import com.example.Pojo.User;
import com.example.Service.UserService;
import com.example.Utils.JwtUtils;
import com.example.Utils.Md5Util;
import com.example.VO.UserInfoVO;
import com.example.VO.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
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
        String password = userRegistDTO.getPassword();
        if(!java.util.Objects.equals(rePassword, password)){
            throw new RuntimeException("两次输入的密码不一致");
        }
        String Password = Md5Util.getMD5String(userRegistDTO.getPassword());

        userMapper.register(userRegistDTO.getUsername(), userRegistDTO.getPhone(), Password);
    }

    /**
     * 用户登录
     * @Param LoginDTO
     * @Return
     */
    @Override
    public Result<UserLoginVO> login(UserLoginDTO userLoginDTO) {
        //查找用户是否存在
        User user = userMapper.selectByUserName(userLoginDTO.getUsername());
        if(user == null){
            return Result.error("用户不存在");
        }

        String newPassword = Md5Util.getMD5String(userLoginDTO.getPassword());

        //检查密码是否错误
        if(!newPassword.equals(user.getPassword())){
            return Result.error("密码错误");
        }

        //账号被禁用
        if(user.getStatus().equals("0")){
            return Result.error("用户被禁用,请联系管理员");
        }

        //更新最后登录时间
        userMapper.setUserLastLoginTime(user);
        
        //创建包含用户信息的Map生成Token
        Map<String, Object> claims = new HashMap<>();
        // 不要在JWT中包含密码信息，这是不安全的, 因为JWT的payload部分是明文可见的
        claims.put("userId", user.getUserId());
        claims.put("username", userLoginDTO.getUsername());

        String token = JwtUtils.generateToken(claims);
        
        // 将token存储到Redis中，设置过期时间与JWT过期时间一致（24小时）
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(token, token, java.time.Duration.ofHours(24)); // 存储token并设置24小时过期
        
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .userId(user.getUserId())
                .accessToken(token)
                .lastLoginTime(LocalDateTime.now())
                .build();
        return Result.success(userLoginVO);
    }

    /**
     * 获取用户信息
     * @Param userId
     * @Return
     */
    @Override
    public UserInfoVO getByUserId(Integer userId) {
        UserInfoVO userInfoVO = userMapper.getByUserId(userId);
        return userInfoVO;
    }


    /**
     * 修改用户信息
     *
     * @Param userId
     * @Return
     */
    @Override
    public void updateById(Integer userId, UserUpdateDTO userUpdateDTO) {
        userMapper.updateById(userId, userUpdateDTO);
    }

}
