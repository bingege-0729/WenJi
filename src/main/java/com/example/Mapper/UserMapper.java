package com.example.Mapper;


import com.example.Pojo.User;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * @param
     */
    @Insert("insert into user(username,password,phone,create_time) values(#{username},#{password},#{phone},NOW())")
    void register(@Param("username")String username, String phone, String password);



}
