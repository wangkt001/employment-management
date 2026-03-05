package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.Student;
import com.employment.entity.User;
import com.employment.repository.CompanyRepository;
import com.employment.repository.StudentRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用户登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        System.out.println("登录请求: username=" + username + ", password=" + password);
        
        if (username == null || password == null) {
            response.put("success", false);
            response.put("message", "用户名和密码不能为空");
            System.out.println("登录失败: 用户名和密码不能为空");
            return response;
        }
        
        // 根据用户名查找用户
        User user = userRepository.findByUsername(username);
        System.out.println("查找用户结果: " + (user != null ? "找到用户: " + user.getUsername() : "未找到用户"));
        
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            System.out.println("登录失败: 用户不存在");
            return response;
        }
        
        // 暂时允许禁用的用户登录，以便测试
        if (!user.isActive()) {
            System.out.println("警告: 用户已被禁用，但暂时允许登录");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "密码错误");
            System.out.println("登录失败: 密码错误");
            return response;
        }
        
        // 登录成功，返回用户信息
        response.put("success", true);
        response.put("message", "登录成功");
        System.out.println("登录成功: " + user.getUsername());
        
        // 构建用户信息，不包含密码
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("name", user.getName());
        userInfo.put("role", user.getRole());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("address", user.getAddress());
        userInfo.put("isActive", user.isActive());
        
        // 如果是公司用户，添加公司ID
        if ("COMPANY".equals(user.getRole())) {
            // 从 Company 表中获取与用户关联的公司
            try {
                Company company = companyRepository.findByUserId(user.getId());
                if (company != null) {
                    userInfo.put("companyId", company.getId());
                }
            } catch (Exception e) {
                System.out.println("获取公司信息失败: " + e.getMessage());
            }
        }
        
        response.put("user", userInfo);
        
        return response;
    }
    
    // 用户注册接口
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> registerData) {
        Map<String, Object> response = new HashMap<>();
        
        String username = (String) registerData.get("username");
        String password = (String) registerData.get("password");
        String name = (String) registerData.get("name");
        String role = (String) registerData.get("role");
        String email = (String) registerData.get("email");
        String phone = (String) registerData.get("phone");
        
        System.out.println("注册请求: username=" + username + ", role=" + role);
        
        if (username == null || password == null || name == null || role == null || email == null || phone == null) {
            response.put("success", false);
            response.put("message", "请填写所有必填字段");
            System.out.println("注册失败: 缺少必填字段");
            return response;
        }
        
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username) != null) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            System.out.println("注册失败: 用户名已存在");
            return response;
        }
        
        // 验证角色
        if (!"STUDENT".equals(role) && !"COMPANY".equals(role)) {
            response.put("success", false);
            response.put("message", "角色只能是学生或企业");
            System.out.println("注册失败: 无效角色");
            return response;
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setEmail(email);
        user.setPhone(phone);
        user.setActive(true);
        
        try {
            user = userRepository.save(user);
            System.out.println("注册成功: " + user.getUsername());
            
            // 处理学生特有字段
            if ("STUDENT".equals(role)) {
                String studentId = (String) registerData.get("studentId");
                String major = (String) registerData.get("major");
                String education = (String) registerData.get("education");
                String school = (String) registerData.get("school");
                String selfIntroduction = (String) registerData.get("selfIntroduction");
                
                if (studentId != null && major != null) {
                    Student student = new Student();
                    student.setUser(user);
                    student.setStudentId(studentId);
                    student.setMajor(major);
                    student.setEducation(education);
                    student.setSchool(school);
                    student.setSelfIntroduction(selfIntroduction);
                    studentRepository.save(student);
                    System.out.println("学生信息保存成功");
                }
            }
            
            // 处理企业特有字段
            if ("COMPANY".equals(role)) {
                Object companyIdObj = registerData.get("companyId");
                if (companyIdObj != null) {
                    try {
                        Long companyId = Long.parseLong(companyIdObj.toString());
                        Company company = companyRepository.findById(companyId).orElse(null);
                        if (company != null) {
                            company.setUser(user);
                            companyRepository.save(company);
                            System.out.println("企业关联成功");
                        }
                    } catch (Exception e) {
                        System.out.println("企业ID解析失败: " + e.getMessage());
                    }
                }
            }
            
            response.put("success", true);
            response.put("message", "注册成功");
            
            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("isActive", user.isActive());
            
            // 如果是公司用户，添加公司ID
            if ("COMPANY".equals(user.getRole())) {
                try {
                    Company company = companyRepository.findByUserId(user.getId());
                    if (company != null) {
                        userInfo.put("companyId", company.getId());
                    }
                } catch (Exception e) {
                    System.out.println("获取公司信息失败: " + e.getMessage());
                }
            }
            
            response.put("user", userInfo);
        } catch (Exception e) {
            System.out.println("注册失败: " + e.getMessage());
            response.put("success", false);
            response.put("message", "注册失败: " + e.getMessage());
        }
        
        return response;
    }
    
    // 检查用户名是否存在接口
    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User existingUser = userRepository.findByUsername(username);
            if (existingUser != null) {
                response.put("exists", true);
                response.put("message", "用户名已存在");
            } else {
                response.put("exists", false);
                response.put("message", "用户名可用");
            }
            response.put("success", true);
        } catch (Exception e) {
            System.out.println("检查用户名失败: " + e.getMessage());
            response.put("success", false);
            response.put("message", "检查用户名失败");
        }
        
        return response;
    }
}
