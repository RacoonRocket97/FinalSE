package com.lms.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LessonResponseDto {
    private Long id;
    private String title;
    private String content;
    private Integer orderNumber;
    private Integer durationMinutes;
    private LocalDateTime publishedAt;
    private Long courseId;
    private String courseTitle;
}