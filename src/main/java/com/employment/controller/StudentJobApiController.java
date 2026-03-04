package com.employment.controller;

import com.employment.entity.Job;
import com.employment.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
public class StudentJobApiController {

    @Autowired
    private JobRepository jobRepository;

    // 学生端分页查询岗位
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public Page<Job> getJobsByPage(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "salary", required = false) String salary,
            @RequestParam(value = "experience", required = false) String experience,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            System.out.println("学生端分页查询岗位");
            System.out.println("title: " + title);
            System.out.println("industry: " + industry);
            System.out.println("salary: " + salary);
            System.out.println("experience: " + experience);
            System.out.println("page: " + page);
            System.out.println("size: " + size);

            // 构建排序
            Sort sort = Sort.by(Sort.Direction.DESC, "publishDate");
            // 构建分页请求
            Pageable pageable = PageRequest.of(page, size, sort);

            // 根据条件查询
            Page<Job> jobPage;

            // 根据条件查询，使用安全的查询方法
            if (title != null) {
                jobPage = jobRepository.findActiveJobsWithCompanyByTitleContaining(title, pageable);
            } else if (industry != null) {
                jobPage = jobRepository.findActiveJobsWithCompanyByIndustry(industry, pageable);
            } else if (salary != null) {
                jobPage = jobRepository.findActiveJobsWithCompanyBySalaryRange(salary, pageable);
            } else if (experience != null) {
                jobPage = jobRepository.findActiveJobsWithCompanyByWorkExperience(experience, pageable);
            } else {
                jobPage = jobRepository.findActiveJobsWithCompany(pageable);
            }

            // 获取过滤后的岗位列表
            List<Job> filteredJobs = jobPage.getContent();

            // 创建新的Page对象返回
            jobPage = new PageImpl<>(filteredJobs, pageable, jobPage.getTotalElements());

            System.out.println("查询结果: " + jobPage.getTotalElements() + " 条记录");
            return jobPage;
        } catch (Exception e) {
            e.printStackTrace();
            // 打印错误日志
            System.out.println("查询岗位失败: " + e.getMessage());
            throw new RuntimeException("查询岗位失败: " + e.getMessage());
        }
    }
}
