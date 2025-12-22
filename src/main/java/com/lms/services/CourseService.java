package com.lms.services;

import com.lms.dto.request.CourseRequestDto;
import com.lms.dto.response.CourseResponseDto;
import com.lms.mappers.CourseMapper;
import com.lms.models.Course;
import com.lms.models.User;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    public CourseResponseDto createCourse(CourseRequestDto dto, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        Course course = courseMapper.toEntity(dto);
        course.setTeacher(teacher);

        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponseDto(savedCourse);
    }

    public List<CourseResponseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toResponseDtoList(courses);
    }

    public CourseResponseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return courseMapper.toResponseDto(course);
    }

    public List<CourseResponseDto> getCoursesByTeacher(Long teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        return courseMapper.toResponseDtoList(courses);
    }

    public List<CourseResponseDto> getCoursesByCategory(String category) {
        List<Course> courses = courseRepository.findByCategory(category);
        return courseMapper.toResponseDtoList(courses);
    }

    public CourseResponseDto updateCourse(Long id, CourseRequestDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCategory(dto.getCategory());
        course.setDurationHours(dto.getDurationHours());
        course.setStartDate(dto.getStartDate());
        course.setEndDate(dto.getEndDate());

        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponseDto(updatedCourse);
    }

    public boolean deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
        return true;
    }
}