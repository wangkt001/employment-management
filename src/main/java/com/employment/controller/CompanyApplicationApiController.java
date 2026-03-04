package com.employment.controller;

import com.employment.entity.Application;
import com.employment.entity.Company;
import com.employment.entity.Job;
import com.employment.entity.Student;
import com.employment.repository.ApplicationRepository;
import com.employment.repository.CompanyRepository;
import com.employment.repository.JobRepository;
import com.employment.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/company/applications")
public class CompanyApplicationApiController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private StudentRepository studentRepository;

    // 获取公司的所有申请
    @GetMapping
    public List<Application> getCompanyApplications(@RequestParam Long companyId) {
        System.out.println("===========================================");
        System.out.println("接收到获取公司申请请求，companyId: " + companyId);
        System.out.println("===========================================");
        try {
            // 1. 校验公司ID
            if (companyId == null) {
                System.out.println("公司ID不能为空");
                throw new RuntimeException("公司ID不能为空");
            }

            // 2. 获取公司
            Company company = companyRepository.findById(companyId).orElse(null);
            if (company == null) {
                System.out.println("公司不存在，companyId: " + companyId);
                throw new RuntimeException("公司不存在，companyId: " + companyId);
            }
            System.out.println("公司已存在，companyId: " + company.getId() + ", companyName: " + company.getCompanyName());

            // 3. 获取公司的所有岗位
            List<Job> jobs = jobRepository.findByCompanyId(company.getId());
            System.out.println("公司的岗位数量: " + jobs.size());

            // 4. 获取这些岗位的所有申请
            List<Application> applications = jobs.stream()
                    .flatMap(job -> applicationRepository.findByJob(job).stream())
                    .collect(Collectors.toList());
            System.out.println("获取到 " + applications.size() + " 个申请记录");

            // 5. 确保相关对象被加载
            for (Application application : applications) {
                if (application.getJob() != null) {
                    System.out.println("申请 ID: " + application.getId() + ", 岗位 ID: " + application.getJob().getId()
                            + ", 岗位名称: " + application.getJob().getTitle());
                }
                if (application.getStudent() != null) {
                    System.out.println("申请人: " + application.getStudent().getUser().getName());
                }
            }

            return applications;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 批准申请
    @PutMapping("/{applicationId}/approve")
    public Application approveApplication(@PathVariable Long applicationId) {
        try {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            if (application == null) {
                throw new RuntimeException("申请不存在");
            }
            application.setStatus("approved");
            return applicationRepository.save(application);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 拒绝申请
    @PutMapping("/{applicationId}/reject")
    public Application rejectApplication(@PathVariable Long applicationId) {
        try {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            if (application == null) {
                throw new RuntimeException("申请不存在");
            }
            application.setStatus("rejected");
            return applicationRepository.save(application);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
