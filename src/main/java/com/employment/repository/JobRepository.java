package com.employment.repository;

import com.employment.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCompanyId(Long companyId);
    List<Job> findByIsActiveTrue();
    List<Job> findByIsActiveTrueAndIndustry(String industry);
    List<Job> findByTitleContainingOrPositionContaining(String title, String position);
    long countByIsActiveTrue();
}