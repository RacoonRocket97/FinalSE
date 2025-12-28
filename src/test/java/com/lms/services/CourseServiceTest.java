package com.lms.services;

import com.lms.dto.request.CourseRequestDto;
import com.lms.dto.response.CourseResponseDto;
import com.lms.mappers.CourseMapper;
import com.lms.models.Course;
import com.lms.models.User;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    @Test
    void testCreateCourse_Success() {
        // Arrange
        Long teacherId = 1L;
        CourseRequestDto requestDto = new CourseRequestDto();
        requestDto.setTitle("Java Basics");

        User teacher = new User();
        teacher.setId(teacherId);

        Course courseEntity = new Course();
        Course savedCourse = new Course();
        savedCourse.setId(10L);

        CourseResponseDto responseDto = new CourseResponseDto();
        responseDto.setId(10L);
        responseDto.setTitle("Java Basics");

        when(userRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseMapper.toEntity(requestDto)).thenReturn(courseEntity);
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);
        when(courseMapper.toResponseDto(savedCourse)).thenReturn(responseDto);

        CourseResponseDto result = courseService.createCourse(requestDto, teacherId);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(courseRepository).save(any(Course.class)); // Verify data persistence
    }

    @Test
    void testCreateCourse_TeacherNotFound() {
        Long teacherId = 99L;
        CourseRequestDto requestDto = new CourseRequestDto();
        when(userRepository.findById(teacherId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.createCourse(requestDto, teacherId);
        });

        assertEquals("Teacher not found with id: " + teacherId, exception.getMessage());
    }

    @Test
    void testGetAllCourses() {
        List<Course> courses = Arrays.asList(new Course(), new Course());
        List<CourseResponseDto> responseDtos = Arrays.asList(new CourseResponseDto(), new CourseResponseDto());

        when(courseRepository.findAll()).thenReturn(courses);
        when(courseMapper.toResponseDtoList(courses)).thenReturn(responseDtos);

        List<CourseResponseDto> result = courseService.getAllCourses();

        assertEquals(2, result.size());
        verify(courseRepository).findAll(); // Verify data retrieval
    }

    @Test
    void testGetCourseById_Success() {
        Long courseId = 1L;
        Course course = new Course();
        CourseResponseDto responseDto = new CourseResponseDto();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDto(course)).thenReturn(responseDto);

        CourseResponseDto result = courseService.getCourseById(courseId);

        assertNotNull(result);
    }

    @Test
    void testGetCoursesByTeacher() {
        Long teacherId = 5L;
        List<Course> courses = Arrays.asList(new Course());
        List<CourseResponseDto> responseDtos = Arrays.asList(new CourseResponseDto());

        when(courseRepository.findByTeacherId(teacherId)).thenReturn(courses);
        when(courseMapper.toResponseDtoList(courses)).thenReturn(responseDtos);

        List<CourseResponseDto> result = courseService.getCoursesByTeacher(teacherId);

        assertEquals(1, result.size());
        verify(courseRepository).findByTeacherId(teacherId);
    }

    @Test
    void testGetCoursesByCategory() {
        String category = "Programming";
        List<Course> courses = Arrays.asList(new Course(), new Course());
        List<CourseResponseDto> responseDtos = Arrays.asList(new CourseResponseDto(), new CourseResponseDto());

        when(courseRepository.findByCategory(category)).thenReturn(courses);
        when(courseMapper.toResponseDtoList(courses)).thenReturn(responseDtos);

        List<CourseResponseDto> result = courseService.getCoursesByCategory(category);

        assertEquals(2, result.size());
        verify(courseRepository).findByCategory(category);
    }

    @Test
    void testUpdateCourse_Success() {
        Long courseId = 1L;
        CourseRequestDto updateDto = new CourseRequestDto();
        updateDto.setTitle("Advanced Java"); // New title

        Course existingCourse = new Course();
        existingCourse.setId(courseId);
        existingCourse.setTitle("Old Java");

        CourseResponseDto responseDto = new CourseResponseDto();
        responseDto.setTitle("Advanced Java");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(existingCourse)).thenReturn(existingCourse);
        when(courseMapper.toResponseDto(existingCourse)).thenReturn(responseDto);

        CourseResponseDto result = courseService.updateCourse(courseId, updateDto);

        assertEquals("Advanced Java", result.getTitle());
        verify(courseRepository).save(existingCourse);
    }

    @Test
    void testDeleteCourse_Success() {
        Long id = 1L;
        when(courseRepository.existsById(id)).thenReturn(true);

        boolean result = courseService.deleteCourse(id);

        assertTrue(result);
        verify(courseRepository).deleteById(id);
    }

    @Test
    void testDeleteCourse_NotFound() {
        Long id = 99L;
        when(courseRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.deleteCourse(id);
        });

        assertEquals("Course not found with id: " + id, exception.getMessage());
    }
}