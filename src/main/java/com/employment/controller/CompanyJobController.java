package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.Job;
import com.employment.entity.User;
import com.employment.repository.CompanyRepository;
import com.employment.repository.JobRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CompanyJobController {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/company/jobs")
    public String jobs(Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        Company company = companyRepository.findByUserId(user.getId());
        model.addAttribute("jobs", jobRepository.findByCompanyId(company.getId()));
        return "company/jobs";
    }
    
    @GetMapping("/company/job/add")
    public String addJob(Model model) {
        model.addAttribute("job", new Job());
        return "company/job-add";
    }
    
    @PostMapping("/company/job/save")
    public String saveJob(@ModelAttribute Job job, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        Company company = companyRepository.findByUserId(user.getId());
        job.setCompany(company);
        job.setPublishDate(new Date());
        jobRepository.save(job);
        return "redirect:/company/jobs";
    }
    
    @GetMapping("/company/job/edit")
    public String editJob(@RequestParam Long id, Model model, Authentication authentication) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/company/jobs";
        }
        User user = userRepository.findByUsername(authentication.getName());
        Company company = companyRepository.findByUserId(user.getId());
        if (!job.getCompany().getId().equals(company.getId())) {
            return "redirect:/company/jobs";
        }
        model.addAttribute("job", job);
        return "company/job-edit";
    }
    
    @GetMapping("/company/job/delete")
    public String deleteJob(@RequestParam Long id, Authentication authentication) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            User user = userRepository.findByUsername(authentication.getName());
            Company company = companyRepository.findByUserId(user.getId());
            if (job.getCompany().getId().equals(company.getId())) {
                jobRepository.delete(job);
            }
        }
        return "redirect:/company/jobs";
    }
}