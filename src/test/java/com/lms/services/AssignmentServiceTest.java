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
class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private AssignmentMapper assignmentMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private AssignmentService assignmentService;

    @Test
    void testCreateAssignment_Success() {
        Long lessonId = 1L;
        AssignmentRequestDto requestDto = new AssignmentRequestDto();
        requestDto.setLessonId(lessonId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);

        Assignment assignmentEntity = new Assignment();
        Assignment savedAssignment = new Assignment();
        savedAssignment.setId(10L);

        AssignmentResponseDto responseDto = new AssignmentResponseDto();
        responseDto.setId(10L);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(assignmentMapper.toEntity(requestDto)).thenReturn(assignmentEntity);
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(savedAssignment);
        when(assignmentMapper.toResponseDto(savedAssignment)).thenReturn(responseDto);

        AssignmentResponseDto result = assignmentService.createAssignment(requestDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(assignmentRepository, times(1)).save(any(Assignment.class)); // Verify save was called
    }

    @Test
    void testGetAssignmentById_Success() {
        Long id = 1L;
        Assignment assignment = new Assignment();
        assignment.setId(id);

        AssignmentResponseDto responseDto = new AssignmentResponseDto();
        responseDto.setId(id);

        when(assignmentRepository.findById(id)).thenReturn(Optional.of(assignment));
        when(assignmentMapper.toResponseDto(assignment)).thenReturn(responseDto);

        AssignmentResponseDto result = assignmentService.getAssignmentById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void testGetAssignmentById_NotFound() {
        Long id = 99L;
        when(assignmentRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            assignmentService.getAssignmentById(id);
        });

        assertEquals("Assignment not found with id: " + id, exception.getMessage());
    }

    @Test
    void testGetAssignmentsByLesson() {
        Long lessonId = 1L;
        List<Assignment> assignments = Arrays.asList(new Assignment(), new Assignment());
        List<AssignmentResponseDto> responseDtos = Arrays.asList(new AssignmentResponseDto(), new AssignmentResponseDto());

        when(assignmentRepository.findByLessonId(lessonId)).thenReturn(assignments);
        when(assignmentMapper.toResponseDtoList(assignments)).thenReturn(responseDtos);


        List<AssignmentResponseDto> result = assignmentService.getAssignmentsByLesson(lessonId);

        assertEquals(2, result.size());
    }

    @Test
    void testSubmitAssignment_Success() {
        Long assignmentId = 1L;
        AssignmentSubmissionDto submissionDto = new AssignmentSubmissionDto();
        submissionDto.setSubmissionText("My Homework");

        Assignment assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setStatus("PENDING");

        User student = new User();
        student.setId(5L);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(userService.getCurrentUser()).thenReturn(student);
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);
        AssignmentResponseDto responseDto = new AssignmentResponseDto();
        responseDto.setStatus("SUBMITTED");
        when(assignmentMapper.toResponseDto(assignment)).thenReturn(responseDto);

        AssignmentResponseDto result = assignmentService.submitAssignment(assignmentId, submissionDto);

        assertEquals("SUBMITTED", result.getStatus());
        verify(assignmentRepository).save(assignment);
    }

    @Test
    void testGradeAssignment_Success() {
        Long assignmentId = 1L;
        AssignmentGradeDto gradeDto = new AssignmentGradeDto();
        gradeDto.setScore(95);
        gradeDto.setFeedback("Good job");

        Assignment assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setStatus("SUBMITTED"); // Must be submitted to grade

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

        AssignmentResponseDto responseDto = new AssignmentResponseDto();
        responseDto.setStatus("GRADED");
        when(assignmentMapper.toResponseDto(assignment)).thenReturn(responseDto);

        AssignmentResponseDto result = assignmentService.gradeAssignment(assignmentId, gradeDto);

        assertEquals("GRADED", result.getStatus());
        verify(assignmentRepository).save(assignment);
    }

    @Test
    void testDeleteAssignment_Success() {
        Long id = 1L;
        when(assignmentRepository.existsById(id)).thenReturn(true);

        boolean result = assignmentService.deleteAssignment(id);

        assertTrue(result);
        verify(assignmentRepository, times(1)).deleteById(id);
    }
}