package com.lms.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CourseResponseDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Integer durationHours;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long teacherId;
    private String teacherName;
    private Integer totalLessons;
}