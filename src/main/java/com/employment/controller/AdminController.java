package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.Job;
import com.employment.entity.User;
import com.employment.repository.ApplicationRepository;
import com.employment.repository.CompanyRepository;
import com.employment.repository.JobRepository;
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

        return existingUser;
    }

    // 删除用户
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
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
        company.setVerified(false);

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

    // 企业请求DTO
    public static class CompanyRequest {
        private String companyName;
        private String industry;
        private String scale;
        private String businessLicense;
        private String description;
        private boolean verified;

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
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }
    }
}
