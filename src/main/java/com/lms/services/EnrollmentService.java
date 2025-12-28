package com.lms.services;

import com.lms.dto.request.EnrollmentRequestDto;
import com.lms.dto.response.EnrollmentResponseDto;
import com.lms.mappers.EnrollmentMapper;
import com.lms.models.Course;
import com.lms.models.Enrollment;
import com.lms.models.User;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final UserService userService;

    public EnrollmentResponseDto enrollStudent(EnrollmentRequestDto dto) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + dto.getCourseId()));

        if (enrollmentRepository.existsByStudentIdAndCourseId(currentUser.getId(), course.getId())) {
            throw new RuntimeException("Student already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(currentUser);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus("ACTIVE");
        enrollment.setProgressPercentage(0);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponseDto(savedEnrollment);
    }

    public List<EnrollmentResponseDto> getMyEnrollments() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(currentUser.getId());
        return enrollmentMapper.toResponseDtoList(enrollments);
    }

    public List<EnrollmentResponseDto> getEnrollmentsByCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollmentMapper.toResponseDtoList(enrollments);
    }

    public EnrollmentResponseDto getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));
        return enrollmentMapper.toResponseDto(enrollment);
    }

    public boolean updateProgress(Long enrollmentId, Integer progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));

        enrollment.setProgressPercentage(progress);

        if (progress >= 100) {
            enrollment.setStatus("COMPLETED");
        }

        enrollmentRepository.save(enrollment);
        return true;
    }

    public boolean dropEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));

        enrollment.setStatus("DROPPED");
        enrollmentRepository.save(enrollment);
        return true;
    }
}