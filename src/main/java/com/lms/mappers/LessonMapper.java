package com.lms.mappers;

import com.lms.dto.request.LessonRequestDto;
import com.lms.dto.response.LessonResponseDto;
import com.lms.models.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "publishedAt", expression = "java(java.time.LocalDateTime.now())")
    Lesson toEntity(LessonRequestDto dto);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    LessonResponseDto toResponseDto(Lesson lesson);

    List<LessonResponseDto> toResponseDtoList(List<Lesson> lessons);
}