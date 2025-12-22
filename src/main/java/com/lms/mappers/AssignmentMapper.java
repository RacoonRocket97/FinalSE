package com.lms.mappers;

import com.lms.dto.request.AssignmentRequestDto;
import com.lms.dto.response.AssignmentResponseDto;
import com.lms.models.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "submissionText", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Assignment toEntity(AssignmentRequestDto dto);

    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "lessonTitle", source = "lesson.title")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression = "java(assignment.getStudent() != null ? assignment.getStudent().getFirstName() + \" \" + assignment.getStudent().getLastName() : null)")
    AssignmentResponseDto toResponseDto(Assignment assignment);

    List<AssignmentResponseDto> toResponseDtoList(List<Assignment> assignments);
}