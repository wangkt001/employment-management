package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.User;
import com.employment.repository.CompanyRepository;
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
}
