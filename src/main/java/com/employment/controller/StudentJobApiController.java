package com.employment.controller;

import com.employment.entity.Job;
import com.employment.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class StudentJobApiController {

    @Autowired
    private JobRepository jobRepository;

    // 获取所有活跃的岗位，支持筛选
    @GetMapping
    public List<Job> getJobs(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String salary,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String keyword
    ) {
        try {
            // 获取所有活跃的岗位
            List<Job> allJobs = jobRepository.findByIsActiveTrue();
            
            // 根据筛选条件过滤
            return allJobs.stream()
                    .filter(job -> {
                        // 行业筛选
                        if (industry != null && !industry.isEmpty() && !industry.equals(job.getIndustry())) {
                            return false;
                        }
                        // 薪资范围筛选
                        if (salary != null && !salary.isEmpty()) {
                            String salaryRange = job.getSalaryRange();
                            switch (salary) {
                                case "0-10":
                                    if (!salaryRange.contains("10K以下")) return false;
                                    break;
                                case "10-20":
                                    if (!salaryRange.contains("10K-20K")) return false;
                                    break;
                                case "20-30":
                                    if (!salaryRange.contains("20K-30K")) return false;
                                    break;
                                case "30+":
                                    if (!salaryRange.contains("30K以上")) return false;
                                    break;
                            }
                        }
                        // 工作经验筛选
                        if (experience != null && !experience.isEmpty()) {
                            String workExperience = job.getWorkExperience();
                            switch (experience) {
                                case "0":
                                    if (!workExperience.contains("应届毕业生")) return false;
                                    break;
                                case "1-3":
                                    if (!workExperience.contains("1-3年")) return false;
                                    break;
                                case "3-5":
                                    if (!workExperience.contains("3-5年")) return false;
                                    break;
                                case "5+":
                                    if (!workExperience.contains("5年以上")) return false;
                                    break;
                            }
                        }
                        // 关键词搜索
                        if (keyword != null && !keyword.isEmpty()) {
                            if (!job.getTitle().contains(keyword) && 
                                (job.getCompany() == null || !job.getCompany().getCompanyName().contains(keyword)) &&
                                (job.getResponsibilities() == null || !job.getResponsibilities().contains(keyword))) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
