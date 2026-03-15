package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.Job;
import com.employment.entity.Student;
import com.employment.entity.User;
import com.employment.repository.ApplicationRepository;
import com.employment.repository.CompanyRepository;
import com.employment.repository.JobRepository;
import com.employment.repository.StudentRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 获取所有用户
    @GetMapping("/users")
    public List<Map<String, Object>> getUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> userList = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("name", user.getName());
            userMap.put("role", user.getRole());
            userMap.put("email", user.getEmail());
            userMap.put("phone", user.getPhone());
            userMap.put("active", user.isActive());

            // 如果是企业角色，查询关联的企业ID
            if ("COMPANY".equals(user.getRole())) {
                Company company = companyRepository.findByUserId(user.getId());
                if (company != null) {
                    userMap.put("companyId", company.getId());
                }
            }
            // 如果是学生角色，查询关联的学生信息
            else if ("STUDENT".equals(user.getRole())) {
                Student student = studentRepository.findByUserId(user.getId());
                if (student != null) {
                    userMap.put("studentId", student.getStudentId());
                    userMap.put("major", student.getMajor());
                    userMap.put("education", student.getEducation());
                    userMap.put("school", student.getSchool());
                    userMap.put("selfIntroduction", student.getSelfIntroduction());
                }
            }

            userList.add(userMap);
        }

        return userList;
    }

    // 添加用户
    @PostMapping("/users")
    public User addUser(@RequestBody Map<String, Object> userData) {
        // 检查用户名是否已存在
        String username = (String) userData.get("username");
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setName((String) userData.get("name"));
        user.setRole((String) userData.get("role"));
        user.setEmail((String) userData.get("email"));
        user.setPhone((String) userData.get("phone"));
        user.setPassword(passwordEncoder.encode((String) userData.get("password")));
        user.setActive(true);

        // 保存用户
        user = userRepository.save(user);

        // 如果是企业角色且提供了companyId，则关联企业
        if ("COMPANY".equals(user.getRole()) && userData.containsKey("companyId")
                && userData.get("companyId") != null) {
            Long companyId = ((Number) userData.get("companyId")).longValue();
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("企业不存在"));
            company.setUser(user);
            companyRepository.save(company);
        }
        // 如果是学生角色，则创建学生信息
        else if ("STUDENT".equals(user.getRole())) {
            Student student = new Student();
            student.setUser(user);
            student.setStudentId((String) userData.get("studentId"));
            student.setMajor((String) userData.get("major"));
            student.setEducation((String) userData.get("education"));
            student.setSchool((String) userData.get("school"));
            student.setSelfIntroduction((String) userData.get("selfIntroduction"));
            studentRepository.save(student);
        }

        return user;
    }

    // 更新用户
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新用户信息
        existingUser.setUsername((String) userData.get("username"));
        existingUser.setName((String) userData.get("name"));
        existingUser.setRole((String) userData.get("role"));
        existingUser.setEmail((String) userData.get("email"));
        existingUser.setPhone((String) userData.get("phone"));

        // 如果密码不为空，则更新密码
        if (userData.containsKey("password") && userData.get("password") != null
                && !((String) userData.get("password")).isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode((String) userData.get("password")));
        }

        // 保存用户
        existingUser = userRepository.save(existingUser);

        // 如果是企业角色且提供了companyId，则关联企业
        if ("COMPANY".equals(existingUser.getRole()) && userData.containsKey("companyId")
                && userData.get("companyId") != null) {
            Long companyId = ((Number) userData.get("companyId")).longValue();
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("企业不存在"));
            company.setUser(existingUser);
            companyRepository.save(company);
        }
        // 如果是学生角色，则更新学生信息
        else if ("STUDENT".equals(existingUser.getRole())) {
            Student student = studentRepository.findByUserId(existingUser.getId());
            if (student != null) {
                student.setStudentId((String) userData.get("studentId"));
                student.setMajor((String) userData.get("major"));
                student.setEducation((String) userData.get("education"));
                student.setSchool((String) userData.get("school"));
                student.setSelfIntroduction((String) userData.get("selfIntroduction"));
                studentRepository.save(student);
            }
        }

        return existingUser;
    }

    // 删除用户
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 如果是学生角色，先删除关联的学生信息和申请记录
        if ("STUDENT".equals(user.getRole())) {
            Student student = studentRepository.findByUserId(id);
            if (student != null) {
                // 删除该学生的所有申请记录
                applicationRepository.deleteByStudentId(student.getId());
                // 删除学生记录
                studentRepository.delete(student);
            }
        }
        // 如果是企业角色，先删除关联的企业信息、职位和申请记录
        else if ("COMPANY".equals(user.getRole())) {
            Company company = companyRepository.findByUserId(id);
            if (company != null) {
                // 获取该企业的所有职位
                List<Job> jobs = jobRepository.findByCompanyId(company.getId());
                // 删除每个职位的所有申请
                for (Job job : jobs) {
                    applicationRepository.deleteByJobId(job.getId());
                }
                // 删除企业的所有职位
                jobRepository.deleteByCompanyId(company.getId());
                // 删除企业记录
                companyRepository.delete(company);
            }
        }
        
        // 最后删除用户
        userRepository.deleteById(id);
    }

    // 切换用户状态
    @PatchMapping("/users/{id}/status")
    public User toggleUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setActive(active);
        return userRepository.save(user);
    }

    // 获取所有企业（支持分页）
    @GetMapping("/companies")
    public Map<String, Object> getCompanies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        // 构建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        // 构建查询
        Page<Company> companyPage;
        if (keyword != null && !keyword.isEmpty()) {
            // 按企业名称搜索
            companyPage = companyRepository.findByCompanyNameContaining(keyword, pageable);
        } else if ("verified".equals(status)) {
            // 只查询已认证企业
            companyPage = companyRepository.findByIsVerifiedTrue(pageable);
        } else if ("unverified".equals(status)) {
            // 只查询未认证企业
            companyPage = companyRepository.findByIsVerifiedFalse(pageable);
        } else {
            // 查询所有企业
            companyPage = companyRepository.findAll(pageable);
        }

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("companies", companyPage.getContent());
        response.put("total", companyPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", companyPage.getTotalPages());

        return response;
    }

    // 添加企业
    @PostMapping("/companies")
    public Company addCompany(@RequestBody CompanyRequest companyRequest) {
        // 创建企业用户
        User user = new User();
        user.setUsername("company_" + System.currentTimeMillis());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole("COMPANY");
        user.setName(companyRequest.getCompanyName());
        user.setActive(true);
        user = userRepository.save(user);

        // 创建企业
        Company company = new Company();
        company.setUser(user);
        company.setCompanyName(companyRequest.getCompanyName());
        company.setIndustry(companyRequest.getIndustry());
        company.setScale(companyRequest.getScale());
        company.setBusinessLicense(companyRequest.getBusinessLicense());
        company.setDescription(companyRequest.getDescription());
        company.setVerified(companyRequest.isVerified());

        return companyRepository.save(company);
    }

    // 更新企业
    @PutMapping("/companies/{id}")
    public Company updateCompany(@PathVariable Long id, @RequestBody CompanyRequest companyRequest) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("企业不存在"));

        // 更新企业信息
        existingCompany.setCompanyName(companyRequest.getCompanyName());
        existingCompany.setIndustry(companyRequest.getIndustry());
        existingCompany.setScale(companyRequest.getScale());
        existingCompany.setBusinessLicense(companyRequest.getBusinessLicense());
        existingCompany.setDescription(companyRequest.getDescription());
        existingCompany.setVerified(companyRequest.isVerified());

        return companyRepository.save(existingCompany);
    }

    // 删除企业
    @DeleteMapping("/companies/{id}")
    public void deleteCompany(@PathVariable Long id) {
        // 先获取企业的所有职位
        List<Job> jobs = jobRepository.findByCompanyId(id);
        // 删除每个职位的所有申请
        for (Job job : jobs) {
            applicationRepository.deleteByJobId(job.getId());
        }
        // 删除企业的所有职位
        jobRepository.deleteByCompanyId(id);
        // 再删除企业
        companyRepository.deleteById(id);
    }

    // 切换企业认证状态
    @PatchMapping("/companies/{id}/verify")
    public Company toggleCompanyVerification(@PathVariable Long id, @RequestParam boolean verified) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("企业不存在"));
        company.setVerified(verified);
        return companyRepository.save(company);
    }

    // 获取统计数据
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总用户数
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        // 就业岗位数（只统计已上架的）
        long totalJobs = jobRepository.countByActiveTrue();
        stats.put("totalJobs", totalJobs);

        // 投递记录数
        long totalApplications = applicationRepository.count();
        stats.put("totalApplications", totalApplications);

        // 企业数
        long totalCompanies = companyRepository.count();
        stats.put("totalCompanies", totalCompanies);

        // 学生用户数
        long studentCount = userRepository.countByRole("STUDENT");
        stats.put("studentCount", studentCount);

        // 企业用户数
        long companyCount = userRepository.countByRole("COMPANY");
        stats.put("companyCount", companyCount);

        // 岗位发布率（已上架岗位数 / 总岗位数）
        long totalJobsAll = jobRepository.count();
        double jobPostingRate = totalJobsAll > 0 ? (double) totalJobs / totalJobsAll * 100 : 0;
        stats.put("jobPostingRate", Math.round(jobPostingRate));

        // 简历投递率（投递记录数 / 学生用户数）
        double applicationRate = studentCount > 0 ? (double) totalApplications / studentCount * 100 : 0;
        stats.put("applicationRate", Math.round(applicationRate));

        return stats;
    }

    // 企业请求DTO
    public static class CompanyRequest {
        private String companyName;
        private String industry;
        private String scale;
        private String businessLicense;
        private String description;
        private boolean isVerified;

        // Getters and Setters
        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getScale() {
            return scale;
        }

        public void setScale(String scale) {
            this.scale = scale;
        }

        public String getBusinessLicense() {
            return businessLicense;
        }

        public void setBusinessLicense(String businessLicense) {
            this.businessLicense = businessLicense;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isVerified() {
            return isVerified;
        }

        public void setVerified(boolean isVerified) {
            this.isVerified = isVerified;
        }
    }
}
