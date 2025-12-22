package com.lms.services;

import com.lms.dto.request.LessonRequestDto;
import com.lms.dto.response.LessonResponseDto;
import com.lms.mappers.LessonMapper;
import com.lms.models.Course;
import com.lms.models.Lesson;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;

    public LessonResponseDto createLesson(LessonRequestDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + dto.getCourseId()));

        Lesson lesson = lessonMapper.toEntity(dto);
        lesson.setCourse(course);

        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonMapper.toResponseDto(savedLesson);
    }

    public List<LessonResponseDto> getLessonsByCourse(Long courseId) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderNumber(courseId);
        return lessonMapper.toResponseDtoList(lessons);
    }

    public LessonResponseDto getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        return lessonMapper.toResponseDto(lesson);
    }

    public LessonResponseDto updateLesson(Long id, LessonRequestDto dto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

        lesson.setTitle(dto.getTitle());
        lesson.setContent(dto.getContent());
        lesson.setOrderNumber(dto.getOrderNumber());
        lesson.setDurationMinutes(dto.getDurationMinutes());

        Lesson updatedLesson = lessonRepository.save(lesson);
        return lessonMapper.toResponseDto(updatedLesson);
    }

    public boolean deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new RuntimeException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
        return true;
    }
}