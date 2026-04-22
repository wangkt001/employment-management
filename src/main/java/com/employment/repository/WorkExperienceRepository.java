package com.employment.repository;

import com.employment.entity.Student;
import com.employment.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findByStudentOrderByStartDateDesc(Student student);

    List<WorkExperience> findByStudentIdOrderByStartDateDesc(Long studentId);
}
