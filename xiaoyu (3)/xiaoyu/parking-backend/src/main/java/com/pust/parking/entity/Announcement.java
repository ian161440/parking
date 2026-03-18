package com.pust.parking.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {

    private Long id;

    private String title;

    private String content;

    private Boolean isTop;

    private Long publisherId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
