package com.example.Mapper;


import com.example.DTO.UserUpdateDTO;
import com.example.Pojo.User;
import com.example.VO.UserInfoVO;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Select("select * from user where username=#{username}")
    User selectByUserName(@NotBlank(message = "用户名不能为空") String username);

    /**
     * 注册
     * @param username
     * @param phone
     * @param password
     */
    @Insert("INSERT INTO user(username, password, phone, create_time, status) VALUES(#{username}, #{password}, #{phone}, NOW(), '1')")
    void register(@Param("username") String username, @Param("phone") String phone, @Param("password") String password);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @Select("select username,real_name,gender,birthday,avatar_url,level,experience,is_real_name_verified,last_login_time,create_time,update_time from user where user_id=#{userId}")
    UserInfoVO getByUserId(@Param("userId") Integer userId);
    /**
     * 更新用户最后登录时间
     * @param user
     */
    @Update("update user set last_login_time=NOW() where user_id=#{userId}")
    void setUserLastLoginTime(User user);

    /**
     * 修改用户信息
     *
     * @param userId
     * @param userUpdateDTO
     * @return
     */
    @Update("UPDATE user SET username=#{userUpdateDTO.username},real_name=#{userUpdateDTO.realName},gender=#{userUpdateDTO.gender},birthday=#{userUpdateDTO.birthday},avatar_url=#{userUpdateDTO.avatarUrl},update_time=NOW() WHERE user_id=#{userId}")
    void updateById(@Param("userId") Integer userId, @Param("userUpdateDTO") @RequestBody @Validated UserUpdateDTO userUpdateDTO);
}
