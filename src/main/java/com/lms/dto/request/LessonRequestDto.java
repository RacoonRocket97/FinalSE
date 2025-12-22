package com.lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;

    @NotNull(message = "Order number is required")
    @Positive(message = "Order number must be positive")
    private Integer orderNumber;

    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @NotNull(message = "Course ID is required")
    private Long courseId;
}