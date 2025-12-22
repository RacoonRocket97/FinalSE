package com.lms.repositories;

import com.lms.models.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByLessonId(Long lessonId);
    List<Assignment> findByStudentId(Long studentId);
    List<Assignment> findByStudentIdAndStatus(Long studentId, String status);
}