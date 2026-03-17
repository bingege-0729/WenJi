package com.example.Pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("mall_item")
public class MallItem {
    @TableId(type = IdType.AUTO)
    private Integer itemId;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String imageUrl;
    private Integer status;
    private LocalDateTime createTime;
}
