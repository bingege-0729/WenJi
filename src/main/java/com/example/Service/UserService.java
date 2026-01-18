package com.example.Service;

import com.example.Common.Result;
import com.example.DTO.UserLoginDTO;
import com.example.DTO.UserRegistDTO;
import com.example.VO.UserLoginVO;

public interface UserService {
    /* *
     * 用户注册/登录
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
}
