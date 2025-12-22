package com.lms.controllers;

import com.lms.dto.request.EnrollmentRequestDto;
import com.lms.dto.response.EnrollmentResponseDto;
import com.lms.services.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<EnrollmentResponseDto> enrollInCourse(@Valid @RequestBody EnrollmentRequestDto dto) {
        EnrollmentResponseDto enrollmentDto = enrollmentService.enrollStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentDto);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<EnrollmentResponseDto>> getMyEnrollments() {
        List<EnrollmentResponseDto> enrollments = enrollmentService.getMyEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<List<EnrollmentResponseDto>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        List<EnrollmentResponseDto> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnrollmentResponseDto> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponseDto enrollmentDto = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollmentDto);
    }

    @PutMapping("/{id}/progress")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<String> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer progress) {
        enrollmentService.updateProgress(id, progress);
        return ResponseEntity.ok("Progress updated successfully");
    }

    @PutMapping("/{id}/drop")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<String> dropEnrollment(@PathVariable Long id) {
        enrollmentService.dropEnrollment(id);
        return ResponseEntity.ok("Enrollment dropped successfully");
    }
}