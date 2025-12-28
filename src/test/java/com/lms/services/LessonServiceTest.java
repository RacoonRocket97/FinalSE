package com.lms.services;

import com.lms.dto.request.LessonRequestDto;
import com.lms.dto.response.LessonResponseDto;
import com.lms.mappers.LessonMapper;
import com.lms.models.Course;
import com.lms.models.Lesson;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.LessonRepository;
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
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonMapper lessonMapper;

    @InjectMocks
    private LessonService lessonService;

    @Test
    void testCreateLesson_Success() {
        Long courseId = 1L;
        LessonRequestDto requestDto = new LessonRequestDto();
        requestDto.setCourseId(courseId);
        requestDto.setTitle("Intro to Spring");

        Course course = new Course();
        course.setId(courseId);

        Lesson lesson = new Lesson();
        Lesson savedLesson = new Lesson();
        savedLesson.setId(10L);

        LessonResponseDto responseDto = new LessonResponseDto();
        responseDto.setId(10L);
        responseDto.setTitle("Intro to Spring");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonMapper.toEntity(requestDto)).thenReturn(lesson);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(savedLesson);
        when(lessonMapper.toResponseDto(savedLesson)).thenReturn(responseDto);

        LessonResponseDto result = lessonService.createLesson(requestDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void testCreateLesson_CourseNotFound() {
        Long courseId = 99L;
        LessonRequestDto requestDto = new LessonRequestDto();
        requestDto.setCourseId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.createLesson(requestDto);
        });

        assertEquals("Course not found with id: " + courseId, exception.getMessage());
    }

    @Test
    void testGetLessonsByCourse() {
        Long courseId = 1L;
        List<Lesson> lessons = Arrays.asList(new Lesson(), new Lesson());
        List<LessonResponseDto> responseDtos = Arrays.asList(new LessonResponseDto(), new LessonResponseDto());

        when(lessonRepository.findByCourseIdOrderByOrderNumber(courseId)).thenReturn(lessons);
        when(lessonMapper.toResponseDtoList(lessons)).thenReturn(responseDtos);

        List<LessonResponseDto> result = lessonService.getLessonsByCourse(courseId);

        assertEquals(2, result.size());
        verify(lessonRepository).findByCourseIdOrderByOrderNumber(courseId);
    }

    @Test
    void testGetLessonById_Success() {
        Long id = 1L;
        Lesson lesson = new Lesson();
        LessonResponseDto responseDto = new LessonResponseDto();

        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));
        when(lessonMapper.toResponseDto(lesson)).thenReturn(responseDto);

        LessonResponseDto result = lessonService.getLessonById(id);

        assertNotNull(result);
    }

    @Test
    void testGetLessonById_NotFound() {
        Long id = 99L;
        when(lessonRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lessonService.getLessonById(id));
    }

    @Test
    void testUpdateLesson_Success() {
        Long id = 1L;
        LessonRequestDto updateDto = new LessonRequestDto();
        updateDto.setTitle("Updated Title");
        updateDto.setContent("New Content");

        Lesson existingLesson = new Lesson();
        existingLesson.setId(id);
        existingLesson.setTitle("Old Title");

        LessonResponseDto responseDto = new LessonResponseDto();
        responseDto.setTitle("Updated Title");

        when(lessonRepository.findById(id)).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(existingLesson)).thenReturn(existingLesson);
        when(lessonMapper.toResponseDto(existingLesson)).thenReturn(responseDto);

        LessonResponseDto result = lessonService.updateLesson(id, updateDto);

        assertEquals("Updated Title", result.getTitle());
        verify(lessonRepository).save(existingLesson);
    }

    @Test
    void testDeleteLesson_Success() {
        Long id = 1L;
        when(lessonRepository.existsById(id)).thenReturn(true);

        boolean result = lessonService.deleteLesson(id);

        assertTrue(result);
        verify(lessonRepository).deleteById(id);
    }

    @Test
    void testDeleteLesson_NotFound() {
        Long id = 99L;
        when(lessonRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.deleteLesson(id);
        });

        assertEquals("Lesson not found with id: " + id, exception.getMessage());
    }
}