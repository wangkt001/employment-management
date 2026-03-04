package com.employment.repository;

import com.employment.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCompanyId(Long companyId);
    void deleteByCompanyId(Long companyId);
    List<Job> findByActiveTrue();
    List<Job> findByActiveTrueAndIndustry(String industry);
    List<Job> findByTitleContainingOrPositionContaining(String title, String position);
    long countByActiveTrue();
    
    // 分页查询接口
    Page<Job> findByCompanyIdAndTitleContainingAndWorkingLocationContainingAndActive(
            Long companyId, String title, String location, boolean active, Pageable pageable);
    
    // 当某些条件为null时的查询
    Page<Job> findByCompanyIdAndTitleContainingAndWorkingLocationContaining(
            Long companyId, String title, String location, Pageable pageable);
    
    Page<Job> findByCompanyIdAndTitleContainingAndActive(
            Long companyId, String title, boolean active, Pageable pageable);
    
    Page<Job> findByCompanyIdAndWorkingLocationContainingAndActive(
            Long companyId, String location, boolean active, Pageable pageable);
    
    Page<Job> findByCompanyIdAndTitleContaining(
            Long companyId, String title, Pageable pageable);
    
    Page<Job> findByCompanyIdAndWorkingLocationContaining(
            Long companyId, String location, Pageable pageable);
    
    Page<Job> findByCompanyIdAndActive(
            Long companyId, boolean active, Pageable pageable);
    
    Page<Job> findByCompanyId(
            Long companyId, Pageable pageable);
}