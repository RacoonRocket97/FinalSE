package com.lms.mappers;

import com.lms.dto.response.EnrollmentResponseDto;
import com.lms.models.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression = "java(enrollment.getStudent().getFirstName() + \" \" + enrollment.getStudent().getLastName())")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    EnrollmentResponseDto toResponseDto(Enrollment enrollment);

    List<EnrollmentResponseDto> toResponseDtoList(List<Enrollment> enrollments);
}