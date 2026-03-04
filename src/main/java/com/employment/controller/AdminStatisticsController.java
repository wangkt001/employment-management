package com.employment.controller;

import com.employment.entity.Application;
import com.employment.entity.Job;
import com.employment.entity.User;
import com.employment.repository.ApplicationRepository;
import com.employment.repository.JobRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminStatisticsController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @GetMapping("/admin/statistics")
    public String statistics(Model model) {
        // 统计用户数量
        long totalUsers = userRepository.count();
        long studentCount = userRepository.countByRole("STUDENT");
        long companyCount = userRepository.countByRole("COMPANY");
        
        // 统计岗位数量
        long totalJobs = jobRepository.count();
        long activeJobs = jobRepository.countByIsActiveTrue();
        
        // 统计投递数量
        long totalApplications = applicationRepository.count();
        
        // 按状态统计投递
        Map<String, Long> applicationStatusStats = new HashMap<>();
        applicationStatusStats.put("PENDING", applicationRepository.countByStatus("PENDING"));
        applicationStatusStats.put("REVIEWED", applicationRepository.countByStatus("REVIEWED"));
        applicationStatusStats.put("INTERVIEW", applicationRepository.countByStatus("INTERVIEW"));
        applicationStatusStats.put("OFFER", applicationRepository.countByStatus("OFFER"));
        applicationStatusStats.put("REJECTED", applicationRepository.countByStatus("REJECTED"));
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("companyCount", companyCount);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("applicationStatusStats", applicationStatusStats);
        
        return "admin/statistics";
    }
    
    @GetMapping("/admin/user-list")
    public String users(Model model) {
        // 获取所有用户
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }
}