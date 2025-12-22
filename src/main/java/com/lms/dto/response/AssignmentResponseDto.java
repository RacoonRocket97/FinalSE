package com.lms.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AssignmentResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer maxScore;
    private Long lessonId;
    private String lessonTitle;
    private Long studentId;
    private String studentName;
    private String submissionText;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
    private String status;
}