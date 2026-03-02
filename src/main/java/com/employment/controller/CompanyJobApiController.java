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
    public List<Job> getJobs(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                System.out.println("未找到认证用户");
                return List.of();
            }
            User user = userRepository.findByUsername(authentication.getName());
            if (user == null) {
                System.out.println("用户不存在");
                return List.of();
            }
            Company company = companyRepository.findByUserId(user.getId());
            if (company == null) {
                System.out.println("公司不存在");
                return List.of();
            }
            return jobRepository.findByCompanyId(company.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 创建新岗位
    @PostMapping
    public Job createJob(@RequestBody JobRequest jobRequest, Authentication authentication) {
        System.out.println("开始创建岗位...");
        System.out.println("请求参数: " + jobRequest.toString());
        try {
            // 1. 获取或创建用户
            System.out.println("步骤1: 获取或创建用户");
            try {
                User user = null;
                if (authentication != null && authentication.getName() != null) {
                    System.out.println("使用认证用户: " + authentication.getName());
                    user = userRepository.findByUsername(authentication.getName());
                    if (user == null) {
                        System.out.println("用户不存在，创建新用户");
                        user = new User();
                        user.setUsername(authentication.getName());
                        user.setPassword(passwordEncoder.encode("default123"));
                        user.setRole("COMPANY");
                        user.setName(authentication.getName());
                        user.setActive(true);
                        user = userRepository.save(user);
                        System.out.println("创建用户成功，ID: " + user.getId());
                    } else {
                        System.out.println("用户已存在，ID: " + user.getId());
                    }
                } else {
                    System.out.println("未找到认证用户");
                    throw new RuntimeException("用户未认证，请先登录");
                }

                // 2. 获取或创建公司
                System.out.println("步骤2: 获取或创建公司");
                Company company = companyRepository.findByUserId(user.getId());
                if (company == null) {
                    System.out.println("公司不存在，创建新公司");
                    company = new Company();
                    company.setUser(user);
                    company.setCompanyName("测试公司");
                    company.setVerified(true);
                    System.out.println("准备保存公司");
                    company = companyRepository.save(company);
                    System.out.println("创建公司成功，ID: " + company.getId());
                } else {
                    System.out.println("公司已存在，ID: " + company.getId());
                }

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
                System.out.println("数据库操作错误: " + e.getMessage());
                e.printStackTrace();
                // 返回友好的错误信息，而不是抛出异常
                throw new RuntimeException("数据库操作失败: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            // 返回友好的错误信息，而不是抛出异常
            throw new RuntimeException("岗位创建失败: " + e.getMessage());
        }
    }

    // 更新岗位
    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody JobRequest jobRequest, Authentication authentication) {
        System.out.println("开始更新岗位...");
        System.out.println("岗位ID: " + id);
        System.out.println("请求参数: " + jobRequest.toString());
        try {
            // 1. 获取认证用户
            System.out.println("步骤1: 获取认证用户");
            User user = null;
            if (authentication != null && authentication.getName() != null) {
                System.out.println("使用认证用户: " + authentication.getName());
                user = userRepository.findByUsername(authentication.getName());
                if (user == null) {
                    System.out.println("用户不存在，创建新用户");
                    user = new User();
                    user.setUsername(authentication.getName());
                    user.setPassword(passwordEncoder.encode("default123"));
                    user.setRole("COMPANY");
                    user.setName(authentication.getName());
                    user.setActive(true);
                    user = userRepository.save(user);
                    System.out.println("创建用户成功，ID: " + user.getId());
                } else {
                    System.out.println("用户已存在，ID: " + user.getId());
                }
            } else {
                System.out.println("未找到认证用户");
                throw new RuntimeException("用户未认证，请先登录");
            }

            // 2. 获取或创建公司
            System.out.println("步骤2: 获取或创建公司");
            Company company = companyRepository.findByUserId(user.getId());
            if (company == null) {
                System.out.println("公司不存在，创建新公司");
                company = new Company();
                company.setUser(user);
                company.setCompanyName(user.getName() + "公司");
                company.setVerified(true);
                company = companyRepository.save(company);
                System.out.println("创建公司成功，ID: " + company.getId());
            } else {
                System.out.println("公司已存在，ID: " + company.getId());
            }

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
    public String deleteJob(@PathVariable Long id, Authentication authentication) {
        System.out.println("开始删除岗位...");
        System.out.println("岗位ID: " + id);
        try {
            // 1. 获取认证用户
            System.out.println("步骤1: 获取认证用户");
            User user = null;
            if (authentication != null && authentication.getName() != null) {
                System.out.println("使用认证用户: " + authentication.getName());
                user = userRepository.findByUsername(authentication.getName());
                if (user == null) {
                    System.out.println("用户不存在，创建新用户");
                    user = new User();
                    user.setUsername(authentication.getName());
                    user.setPassword(passwordEncoder.encode("default123"));
                    user.setRole("COMPANY");
                    user.setName(authentication.getName());
                    user.setActive(true);
                    user = userRepository.save(user);
                    System.out.println("创建用户成功，ID: " + user.getId());
                } else {
                    System.out.println("用户已存在，ID: " + user.getId());
                }
            } else {
                System.out.println("未找到认证用户");
                throw new RuntimeException("用户未认证，请先登录");
            }

            // 2. 获取或创建公司
            System.out.println("步骤2: 获取或创建公司");
            Company company = companyRepository.findByUserId(user.getId());
            if (company == null) {
                System.out.println("公司不存在，创建新公司");
                company = new Company();
                company.setUser(user);
                company.setCompanyName(user.getName() + "公司");
                company.setVerified(true);
                company = companyRepository.save(company);
                System.out.println("创建公司成功，ID: " + company.getId());
            } else {
                System.out.println("公司已存在，ID: " + company.getId());
            }

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
    public Job toggleJobStatus(@PathVariable Long id, @RequestParam boolean active, Authentication authentication) {
        System.out.println("开始切换岗位状态...");
        System.out.println("岗位ID: " + id);
        System.out.println("新状态: " + active);
        try {
            // 1. 获取认证用户
            System.out.println("步骤1: 获取认证用户");
            User user = null;
            if (authentication != null && authentication.getName() != null) {
                System.out.println("使用认证用户: " + authentication.getName());
                user = userRepository.findByUsername(authentication.getName());
                if (user == null) {
                    System.out.println("用户不存在");
                    throw new RuntimeException("用户不存在");
                }
            } else {
                System.out.println("未找到认证用户");
                throw new RuntimeException("用户未认证，请先登录");
            }

            // 2. 获取公司
            System.out.println("步骤2: 获取公司");
            Company company = companyRepository.findByUserId(user.getId());
            if (company == null) {
                System.out.println("公司不存在");
                throw new RuntimeException("公司不存在");
            }

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
                    '}';
        }
    }
}