package com.lms.services;

import com.lms.dto.request.EnrollmentRequestDto;
import com.lms.dto.response.EnrollmentResponseDto;
import com.lms.mappers.EnrollmentMapper;
import com.lms.models.Course;
import com.lms.models.Enrollment;
import com.lms.models.User;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.EnrollmentRepository;
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
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void testEnrollStudent_Success() {
        Long courseId = 1L;
        Long userId = 5L;

        EnrollmentRequestDto requestDto = new EnrollmentRequestDto();
        requestDto.setCourseId(courseId);

        User currentUser = new User();
        currentUser.setId(userId);

        Course course = new Course();
        course.setId(courseId);

        Enrollment savedEnrollment = new Enrollment();
        savedEnrollment.setId(10L);
        savedEnrollment.setStatus("ACTIVE");

        EnrollmentResponseDto responseDto = new EnrollmentResponseDto();
        responseDto.setId(10L);
        responseDto.setStatus("ACTIVE");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);
        when(enrollmentMapper.toResponseDto(savedEnrollment)).thenReturn(responseDto);

        EnrollmentResponseDto result = enrollmentService.enrollStudent(requestDto);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void testEnrollStudent_AlreadyEnrolled() {
        Long courseId = 1L;
        Long userId = 5L;
        EnrollmentRequestDto requestDto = new EnrollmentRequestDto();
        requestDto.setCourseId(courseId);

        User currentUser = new User();
        currentUser.setId(userId);
        Course course = new Course();
        course.setId(courseId);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId)).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            enrollmentService.enrollStudent(requestDto);
        });

        assertEquals("Student already enrolled in this course", exception.getMessage());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testGetMyEnrollments() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(1L);

        List<Enrollment> enrollments = Arrays.asList(new Enrollment(), new Enrollment());
        List<EnrollmentResponseDto> responseDtos = Arrays.asList(new EnrollmentResponseDto(), new EnrollmentResponseDto());

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(enrollmentRepository.findByStudentId(currentUser.getId())).thenReturn(enrollments);
        when(enrollmentMapper.toResponseDtoList(enrollments)).thenReturn(responseDtos);

        List<EnrollmentResponseDto> result = enrollmentService.getMyEnrollments();

        assertEquals(2, result.size());
    }

    @Test
    void testUpdateProgress_Normal() {
        Long enrollmentId = 1L;
        Integer newProgress = 50;

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setProgressPercentage(10);
        enrollment.setStatus("ACTIVE");

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        boolean result = enrollmentService.updateProgress(enrollmentId, newProgress);

        assertTrue(result);
        assertEquals(50, enrollment.getProgressPercentage());
        assertEquals("ACTIVE", enrollment.getStatus()); // Should still be active
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void testUpdateProgress_Completed() {
        Long enrollmentId = 1L;
        Integer newProgress = 100;

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setStatus("ACTIVE");

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        enrollmentService.updateProgress(enrollmentId, newProgress);

        assertEquals(100, enrollment.getProgressPercentage());
        assertEquals("COMPLETED", enrollment.getStatus()); // Verify status changed automatically
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void testDropEnrollment() {
        Long enrollmentId = 1L;
        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setStatus("ACTIVE");

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        boolean result = enrollmentService.dropEnrollment(enrollmentId);

        assertTrue(result);
        assertEquals("DROPPED", enrollment.getStatus()); // Verify status change
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void testGetEnrollmentById() {
        Long id = 1L;
        Enrollment enrollment = new Enrollment();
        EnrollmentResponseDto responseDto = new EnrollmentResponseDto();

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(enrollment));
        when(enrollmentMapper.toResponseDto(enrollment)).thenReturn(responseDto);

        EnrollmentResponseDto result = enrollmentService.getEnrollmentById(id);

        assertNotNull(result);
    }
}