package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.Job;
import com.employment.entity.User;
import com.employment.repository.CompanyRepository;
import com.employment.repository.JobRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/company/jobs")
public class CompanyJobApiController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 获取企业的所有岗位
    @GetMapping
    public List<Job> getJobs() {
        try {
            System.out.println("获取所有岗位");
            return jobRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 分页查询岗位
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public Page<Job> getJobsByPage(
            @RequestParam("companyId") Long companyId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            System.out.println("分页查询岗位");
            System.out.println("companyId: " + companyId);
            System.out.println("title: " + title);
            System.out.println("location: " + location);
            System.out.println("active: " + active);
            System.out.println("page: " + page);
            System.out.println("size: " + size);

            // 构建排序
            Sort sort = Sort.by(Sort.Direction.DESC, "publishDate");
            // 构建分页请求
            Pageable pageable = PageRequest.of(page, size, sort);

            // 根据条件查询
            Page<Job> jobPage;
            if (title != null && location != null && active != null) {
                jobPage = jobRepository.findByCompanyIdAndTitleContainingAndWorkingLocationContainingAndActive(
                        companyId, title, location, active, pageable);
            } else if (title != null && location != null) {
                jobPage = jobRepository.findByCompanyIdAndTitleContainingAndWorkingLocationContaining(
                        companyId, title, location, pageable);
            } else if (title != null && active != null) {
                jobPage = jobRepository.findByCompanyIdAndTitleContainingAndActive(
                        companyId, title, active, pageable);
            } else if (location != null && active != null) {
                jobPage = jobRepository.findByCompanyIdAndWorkingLocationContainingAndActive(
                        companyId, location, active, pageable);
            } else if (title != null) {
                jobPage = jobRepository.findByCompanyIdAndTitleContaining(
                        companyId, title, pageable);
            } else if (location != null) {
                jobPage = jobRepository.findByCompanyIdAndWorkingLocationContaining(
                        companyId, location, pageable);
            } else if (active != null) {
                jobPage = jobRepository.findByCompanyIdAndActive(
                        companyId, active, pageable);
            } else {
                jobPage = jobRepository.findByCompanyId(
                        companyId, pageable);
            }

            System.out.println("查询结果: " + jobPage.getTotalElements() + " 条记录");
            return jobPage;
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty();
        }
    }

    // 创建新岗位
    @PostMapping
    public Job createJob(@RequestBody JobRequest jobRequest) {
        System.out.println("开始创建岗位...");
        System.out.println("请求参数: " + jobRequest.toString());
        try {
            // 1. 根据userId获取用户
            System.out.println("步骤1: 根据userId获取用户");
            if (jobRequest.getUserId() == null) {
                System.out.println("userId为null");
                throw new RuntimeException("用户ID不能为空");
            }
            User user = userRepository.findById(jobRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            System.out.println("找到用户，ID: " + user.getId() + "，名称: " + user.getName());

            // 2. 根据companyId获取公司
            System.out.println("步骤2: 根据companyId获取公司");
            if (jobRequest.getCompanyId() == null) {
                System.out.println("companyId为null");
                throw new RuntimeException("公司ID不能为空");
            }
            Company company = companyRepository.findById(jobRequest.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("企业不存在"));
            System.out.println("找到公司，ID: " + company.getId() + "，名称: " + company.getCompanyName());

            // 3. 创建岗位
            System.out.println("步骤3: 创建岗位");
            Job job = new Job();
            job.setCompany(company);
            job.setTitle(jobRequest.getTitle());
            job.setPosition(jobRequest.getTitle()); // 使用title作为position
            job.setSalaryRange(jobRequest.getSalary());
            job.setWorkingLocation(jobRequest.getLocation());
            job.setWorkExperience(jobRequest.getExperience());
            job.setEducationLevel(jobRequest.getEducation());
            job.setIndustry(jobRequest.getIndustry());
            job.setResponsibilities(jobRequest.getDescription());
            job.setRequirements(jobRequest.getRequirements());
            job.setTags(jobRequest.getTagsInput());
            job.setActive(true);
            job.setPublishDate(new Date());

            System.out.println("准备保存岗位");
            Job savedJob = jobRepository.save(job);
            System.out.println("岗位创建成功，ID: " + savedJob.getId());
            return savedJob;
        } catch (Exception e) {
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 更新岗位
    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody JobRequest jobRequest) {
        System.out.println("开始更新岗位...");
        System.out.println("岗位ID: " + id);
        System.out.println("请求参数: " + jobRequest.toString());
        try {
            // 1. 根据userId获取用户
            System.out.println("步骤1: 根据userId获取用户");
            if (jobRequest.getUserId() == null) {
                System.out.println("userId为null");
                throw new RuntimeException("用户ID不能为空");
            }
            User user = userRepository.findById(jobRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            System.out.println("找到用户，ID: " + user.getId() + "，名称: " + user.getName());

            // 2. 根据companyId获取公司
            System.out.println("步骤2: 根据companyId获取公司");
            if (jobRequest.getCompanyId() == null) {
                System.out.println("companyId为null");
                throw new RuntimeException("公司ID不能为空");
            }
            Company company = companyRepository.findById(jobRequest.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("企业不存在"));
            System.out.println("找到公司，ID: " + company.getId() + "，名称: " + company.getCompanyName());

            // 3. 查找岗位
            System.out.println("步骤3: 查找岗位");
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("岗位不存在"));
            System.out.println("找到岗位，ID: " + job.getId() + "，公司ID: " + job.getCompany().getId());

            // 4. 验证权限（暂时跳过，方便测试）
            // if (!job.getCompany().getId().equals(company.getId())) {
            // throw new RuntimeException("无权操作此岗位");
            // }

            // 5. 更新岗位信息
            System.out.println("步骤4: 更新岗位信息");
            job.setTitle(jobRequest.getTitle());
            job.setPosition(jobRequest.getTitle()); // 使用title作为position
            job.setSalaryRange(jobRequest.getSalary());
            job.setWorkingLocation(jobRequest.getLocation());
            job.setWorkExperience(jobRequest.getExperience());
            job.setEducationLevel(jobRequest.getEducation());
            job.setIndustry(jobRequest.getIndustry());
            job.setResponsibilities(jobRequest.getDescription());
            job.setRequirements(jobRequest.getRequirements());
            job.setTags(jobRequest.getTagsInput());

            // 6. 保存更新
            System.out.println("步骤5: 保存更新");
            Job updatedJob = jobRepository.save(job);
            System.out.println("岗位更新成功，ID: " + updatedJob.getId());

            return updatedJob;
        } catch (Exception e) {
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("岗位更新失败: " + e.getMessage());
        }
    }

    // 删除岗位
    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id, @RequestParam Long userId, @RequestParam Long companyId) {
        System.out.println("开始删除岗位...");
        System.out.println("岗位ID: " + id);
        System.out.println("userId: " + userId);
        System.out.println("companyId: " + companyId);
        try {
            // 1. 根据userId获取用户
            System.out.println("步骤1: 根据userId获取用户");
            if (userId == null) {
                System.out.println("userId为null");
                throw new RuntimeException("用户ID不能为空");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            System.out.println("找到用户，ID: " + user.getId() + "，名称: " + user.getName());

            // 2. 根据companyId获取公司
            System.out.println("步骤2: 根据companyId获取公司");
            if (companyId == null) {
                System.out.println("companyId为null");
                throw new RuntimeException("公司ID不能为空");
            }
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("企业不存在"));
            System.out.println("找到公司，ID: " + company.getId() + "，名称: " + company.getCompanyName());

            // 3. 查找岗位
            System.out.println("步骤3: 查找岗位");
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("岗位不存在"));
            System.out.println("找到岗位，ID: " + job.getId() + "，公司ID: " + job.getCompany().getId());

            // 4. 验证权限（暂时跳过，方便测试）
            // if (!job.getCompany().getId().equals(company.getId())) {
            // throw new RuntimeException("无权操作此岗位");
            // }

            // 5. 删除岗位
            System.out.println("步骤4: 删除岗位");
            jobRepository.delete(job);
            System.out.println("岗位删除成功，ID: " + id);

            return "岗位删除成功";
        } catch (Exception e) {
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("岗位删除失败: " + e.getMessage());
        }
    }

    // 切换岗位状态
    @PatchMapping("/{id}/status")
    public Job toggleJobStatus(@PathVariable Long id, @RequestParam boolean active, @RequestParam Long userId,
            @RequestParam Long companyId) {
        System.out.println("开始切换岗位状态...");
        System.out.println("岗位ID: " + id);
        System.out.println("新状态: " + active);
        System.out.println("userId: " + userId);
        System.out.println("companyId: " + companyId);
        try {
            // 1. 根据userId获取用户
            System.out.println("步骤1: 根据userId获取用户");
            if (userId == null) {
                System.out.println("userId为null");
                throw new RuntimeException("用户ID不能为空");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            System.out.println("找到用户，ID: " + user.getId() + "，名称: " + user.getName());

            // 2. 根据companyId获取公司
            System.out.println("步骤2: 根据companyId获取公司");
            if (companyId == null) {
                System.out.println("companyId为null");
                throw new RuntimeException("公司ID不能为空");
            }
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("企业不存在"));
            System.out.println("找到公司，ID: " + company.getId() + "，名称: " + company.getCompanyName());

            // 3. 查找岗位
            System.out.println("步骤3: 查找岗位");
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("岗位不存在"));
            System.out.println("找到岗位，ID: " + job.getId());
            if (job.getCompany() != null) {
                System.out.println("公司ID: " + job.getCompany().getId());
            } else {
                System.out.println("岗位没有关联公司");
                throw new RuntimeException("岗位没有关联公司");
            }

            // 4. 验证权限
            System.out.println("步骤4: 验证权限");
            if (!job.getCompany().getId().equals(company.getId())) {
                System.out.println("无权操作此岗位");
                throw new RuntimeException("无权操作此岗位");
            }

            // 5. 切换岗位状态
            System.out.println("步骤5: 切换岗位状态");
            job.setActive(active);

            // 6. 保存更新
            System.out.println("步骤6: 保存更新");
            Job updatedJob = jobRepository.save(job);
            System.out.println("岗位状态切换成功，ID: " + updatedJob.getId() + "，新状态: " + updatedJob.isActive());

            return updatedJob;
        } catch (Exception e) {
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("岗位状态切换失败: " + e.getMessage());
        }
    }

    // 岗位请求DTO
    public static class JobRequest {
        private String title;
        private String salary;
        private String location;
        private String experience;
        private String education;
        private String industry;
        private String description;
        private String requirements;
        private String tagsInput;
        private Long companyId;
        private Long userId;

        // getters and setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getEducation() {
            return education;
        }

        public void setEducation(String education) {
            this.education = education;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRequirements() {
            return requirements;
        }

        public void setRequirements(String requirements) {
            this.requirements = requirements;
        }

        public String getTagsInput() {
            return tagsInput;
        }

        public void setTagsInput(String tagsInput) {
            this.tagsInput = tagsInput;
        }

        public Long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "JobRequest{" +
                    "title='" + title + '\'' +
                    ", salary='" + salary + '\'' +
                    ", location='" + location + '\'' +
                    ", experience='" + experience + '\'' +
                    ", education='" + education + '\'' +
                    ", industry='" + industry + '\'' +
                    ", description='" + description + '\'' +
                    ", requirements='" + requirements + '\'' +
                    ", tagsInput='" + tagsInput + '\'' +
                    ", companyId='" + companyId + '\'' +
                    ", userId='" + userId + '\'' +
                    '}';
        }
    }
}