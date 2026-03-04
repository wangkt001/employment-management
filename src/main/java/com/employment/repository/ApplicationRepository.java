package com.employment.repository;

import com.employment.entity.Application;
import com.employment.entity.Job;
import com.employment.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    long countByStatus(String status);

    long countByJobId(Long jobId);

    List<Application> findByStudent(Student student);
    
    List<Application> findByJob(Job job);
    
    void deleteByJobId(Long jobId);
}