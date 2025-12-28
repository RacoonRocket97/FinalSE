package com.lms.mappers;

import com.lms.dto.request.CourseRequestDto;
import com.lms.dto.response.CourseResponseDto;
import com.lms.models.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    Course toEntity(CourseRequestDto dto);

    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", expression = "java(getTeacherFullName(course))")
    @Mapping(target = "totalLessons", expression = "java(getLessonsCount(course))")
    CourseResponseDto toResponseDto(Course course);

    List<CourseResponseDto> toResponseDtoList(List<Course> courses);

    default String getTeacherFullName(Course course) {
        return course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName();
    }

    default Integer getLessonsCount(Course course) {
        return course.getLessons() != null ? course.getLessons().size() : 0;
    }
}