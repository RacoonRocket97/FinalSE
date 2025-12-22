package com.lms.services;

import com.lms.dto.request.AssignmentGradeDto;
import com.lms.dto.request.AssignmentRequestDto;
import com.lms.dto.request.AssignmentSubmissionDto;
import com.lms.dto.response.AssignmentResponseDto;
import com.lms.mappers.AssignmentMapper;
import com.lms.models.Assignment;
import com.lms.models.Lesson;
import com.lms.models.User;
import com.lms.repositories.AssignmentRepository;
import com.lms.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final AssignmentMapper assignmentMapper;
    private final UserService userService;

    public AssignmentResponseDto createAssignment(AssignmentRequestDto dto) {
        Lesson lesson = lessonRepository.findById(dto.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + dto.getLessonId()));

        Assignment assignment = assignmentMapper.toEntity(dto);
        assignment.setLesson(lesson);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponseDto(savedAssignment);
    }

    public List<AssignmentResponseDto> getAssignmentsByLesson(Long lessonId) {
        List<Assignment> assignments = assignmentRepository.findByLessonId(lessonId);
        return assignmentMapper.toResponseDtoList(assignments);
    }

    public List<AssignmentResponseDto> getMyAssignments() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        List<Assignment> assignments = assignmentRepository.findByStudentId(currentUser.getId());
        return assignmentMapper.toResponseDtoList(assignments);
    }

    public List<AssignmentResponseDto> getMyPendingAssignments() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        List<Assignment> assignments = assignmentRepository.findByStudentIdAndStatus(currentUser.getId(), "PENDING");
        return assignmentMapper.toResponseDtoList(assignments);
    }

    public AssignmentResponseDto getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));
        return assignmentMapper.toResponseDto(assignment);
    }

    public AssignmentResponseDto submitAssignment(Long id, AssignmentSubmissionDto dto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        assignment.setStudent(currentUser);
        assignment.setSubmissionText(dto.getSubmissionText());
        assignment.setSubmittedAt(LocalDateTime.now());
        assignment.setStatus("SUBMITTED");

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponseDto(updatedAssignment);
    }

    public AssignmentResponseDto gradeAssignment(Long id, AssignmentGradeDto dto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));

        if (!"SUBMITTED".equals(assignment.getStatus())) {
            throw new RuntimeException("Assignment has not been submitted yet");
        }

        assignment.setScore(dto.getScore());
        assignment.setFeedback(dto.getFeedback());
        assignment.setStatus("GRADED");

        Assignment gradedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponseDto(gradedAssignment);
    }

    public boolean deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
        assignmentRepository.deleteById(id);
        return true;
    }
}