package com.lms.controllers;

import com.lms.dto.request.CourseRequestDto;
import com.lms.dto.response.CourseResponseDto;
import com.lms.models.User;
import com.lms.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        List<CourseResponseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long id) {
        CourseResponseDto courseDto = courseService.getCourseById(id);
        return ResponseEntity.ok(courseDto);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseResponseDto>> getCoursesByTeacher(@PathVariable Long teacherId) {
        List<CourseResponseDto> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CourseResponseDto>> getCoursesByCategory(@PathVariable String category) {
        List<CourseResponseDto> courses = courseService.getCoursesByCategory(category);
        return ResponseEntity.ok(courses);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<CourseResponseDto> createCourse(
            @Valid @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User user) {
        Long teacherId = dto.getTeacherId() != null ? dto.getTeacherId() : user.getId();
        CourseResponseDto courseDto = courseService.createCourse(dto, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDto dto) {
        CourseResponseDto courseDto = courseService.updateCourse(id, dto);
        return ResponseEntity.ok(courseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }
}