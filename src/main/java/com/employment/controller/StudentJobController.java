package com.employment.controller;

import com.employment.entity.Job;
import com.employment.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StudentJobController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/student/jobs")
    public String jobs(Model model, @RequestParam(required = false) String keyword) {
        List<Job> jobs;
        if (keyword != null && !keyword.isEmpty()) {
            jobs = jobRepository.findByTitleContainingOrPositionContaining(keyword, keyword);
            model.addAttribute("keyword", keyword);
        } else {
            jobs = jobRepository.findByActiveTrue();
        }
        model.addAttribute("jobs", jobs);
        return "student/jobs";
    }

    @GetMapping("/student/job/detail")
    public String jobDetail(@RequestParam Long id, Model model) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null) {
            return "redirect:/student/jobs";
        }
        model.addAttribute("job", job);
        return "student/job-detail";
    }
}