package com.lms.controllers;

import com.lms.dto.request.LessonRequestDto;
import com.lms.dto.response.LessonResponseDto;
import com.lms.services.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LessonResponseDto>> getLessonsByCourse(@PathVariable Long courseId) {
        List<LessonResponseDto> lessons = lessonService.getLessonsByCourse(courseId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LessonResponseDto> getLessonById(@PathVariable Long id) {
        LessonResponseDto lessonDto = lessonService.getLessonById(id);
        return ResponseEntity.ok(lessonDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<LessonResponseDto> createLesson(@Valid @RequestBody LessonRequestDto dto) {
        LessonResponseDto lessonDto = lessonService.createLesson(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<LessonResponseDto> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequestDto dto) {
        LessonResponseDto lessonDto = lessonService.updateLesson(id, dto);
        return ResponseEntity.ok(lessonDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<String> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok("Lesson deleted successfully");
    }
}