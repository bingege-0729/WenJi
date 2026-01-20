package com.example.Pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class OpeningHours {
    private Long hourId;
    private String siteId;
    private Integer dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isOpen;
    private LocalDate specialDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
