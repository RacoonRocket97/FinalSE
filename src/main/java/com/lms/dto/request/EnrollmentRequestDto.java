package com.lms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentRequestDto {

    @NotNull(message = "Course ID is required")
    private Long courseId;
}