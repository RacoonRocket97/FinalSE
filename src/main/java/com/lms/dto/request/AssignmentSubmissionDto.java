package com.lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentSubmissionDto {

    @NotBlank(message = "Submission text is required")
    private String submissionText;
}