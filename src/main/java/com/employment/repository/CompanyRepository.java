package com.employment.repository;

import com.employment.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByUserId(Long userId);
    Page<Company> findByCompanyNameContaining(String companyName, Pageable pageable);
    Page<Company> findByIsVerifiedTrue(Pageable pageable);
    Page<Company> findByIsVerifiedFalse(Pageable pageable);
}