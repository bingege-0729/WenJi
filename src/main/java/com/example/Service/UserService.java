package com.example.Service;

import com.example.Common.Result;
import com.example.DTO.UserLoginDTO;
import com.example.DTO.UserRegistDTO;
import com.example.DTO.UserUpdateDTO;
import com.example.VO.UserInfoVO;
import com.example.VO.UserLoginVO;

public interface UserService {
    /* *
     * 用户注册
     * @Param registerDTO
     * @Return
     */
    void register(UserRegistDTO userRegistDTO);

    /* *
     * 用户登录
     * @Param loginDTO
     * @Return
     */
    Result<UserLoginVO> login(UserLoginDTO userLoginDTO);

    /* *
     * 获取用户信息
     * @Param userId
     * @Return
     */
    UserInfoVO getByUserId(Integer userId);

    /* *
     * 修改用户信息
     * @Param userId
     * @Return
     */
    void updateById(Integer userId, UserUpdateDTO userUpdateDTO);

    /* *
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param rePassword 确认新密码
     */
    void updatePassword(Integer userId,String oldPassword,String newPassword, String rePassword);

}
