package com.lms.controllers;

import com.lms.dto.request.AssignmentGradeDto;
import com.lms.dto.request.AssignmentRequestDto;
import com.lms.dto.request.AssignmentSubmissionDto;
import com.lms.dto.response.AssignmentResponseDto;
import com.lms.services.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<AssignmentResponseDto> createAssignment(@Valid @RequestBody AssignmentRequestDto dto) {
        AssignmentResponseDto assignmentDto = assignmentService.createAssignment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentDto);
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AssignmentResponseDto>> getAssignmentsByLesson(@PathVariable Long lessonId) {
        List<AssignmentResponseDto> assignments = assignmentService.getAssignmentsByLesson(lessonId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<AssignmentResponseDto>> getMyAssignments() {
        List<AssignmentResponseDto> assignments = assignmentService.getMyAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/my/pending")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<AssignmentResponseDto>> getMyPendingAssignments() {
        List<AssignmentResponseDto> assignments = assignmentService.getMyPendingAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AssignmentResponseDto> getAssignmentById(@PathVariable Long id) {
        AssignmentResponseDto assignmentDto = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(assignmentDto);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<AssignmentResponseDto> submitAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentSubmissionDto dto) {
        AssignmentResponseDto assignmentDto = assignmentService.submitAssignment(id, dto);
        return ResponseEntity.ok(assignmentDto);
    }

    @PutMapping("/{id}/grade")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<AssignmentResponseDto> gradeAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentGradeDto dto) {
        AssignmentResponseDto assignmentDto = assignmentService.gradeAssignment(id, dto);
        return ResponseEntity.ok(assignmentDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok("Assignment deleted successfully");
    }
}