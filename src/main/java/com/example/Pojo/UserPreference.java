package com.example.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreference {
    private Long preferenceId;       // 偏好ID
    private Integer userId;          // 关联User.userId
    private String preferenceType;   // interest_theme/language/pace/accessibility
    private String preferenceValue;  // 偏好值(JSON)
    private Integer weight;          // 权重
    private LocalDateTime updateTime; // 更新时间
}