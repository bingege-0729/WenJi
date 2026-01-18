package com.example.Service;

import com.example.DTO.LoginDTO;
import com.example.DTO.RegisterDTO;

public interface UserService {
    /* *
     * 用户注册/登录
     * @Param registerDTO
     * @Return
     */
    void register(RegisterDTO registerDTO);

}
