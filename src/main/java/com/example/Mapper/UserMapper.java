package com.example.Mapper;


import com.example.Pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    @Select("select * from user where phone=#{phone}")
    User selectByPhone(String phone);




    /**
     * 注册
     * @param
     */
    @Insert("insert into user(nickname,password,phone,create_time) values(#{nickname},#{password},#{phone},NOW())")
    void register(String phone,String nickname, String password);
}
