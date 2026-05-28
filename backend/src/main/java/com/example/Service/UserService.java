package com.example.Service;

import com.example.Common.Result;
import com.example.po.DTO.LoginFormDTO;
import com.example.po.DTO.UserUpdateDTO;
import com.example.po.VO.UserInfoVO;

public interface UserService {
    /* *
     * 用户登录
     * @Param loginDTO
     * @Return
     */
    Result<?> login(LoginFormDTO loginDTO);

    /* *
     * 获取用户信息
     * @Param userId
     * @Return
     */
    Result<UserInfoVO> getUserProfile(Long userId, String  token);

    /* *
     * 修改用户信息
     * @Param userId
     * @Return
     */
    void updateById(Long userId, UserUpdateDTO userUpdateDTO);

    /* *
     * 删除用户
     * @Param userId
     * @Return
     */
    void removeUserById(Long userId);

    /**
     * 用户登出
     * @return 登出结果
     */
    Result<Void> logout(String token);

    /* *
     * 根据用户ID获取用户信息
     * @Param userId
     * @Return
     */
    UserInfoVO getByUserId(Long userId);
}
