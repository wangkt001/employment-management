package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.User;
import com.employment.repository.CompanyRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/current")
    public Object getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            User user = userRepository.findByUsername(authentication.getName());
            if (user != null) {
                return user;
            }
        }
        // 返回一个空对象，而不是null，确保JSON解析成功
        return new java.util.HashMap<>();
    }

    @GetMapping("/{userId}")
    public Object getUserById(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                // 构建用户信息，包含公司ID（如果是公司用户）
                java.util.HashMap<String, Object> userInfo = new java.util.HashMap<>();
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
                    try {
                        Company company = companyRepository.findByUserId(user.getId());
                        if (company != null) {
                            userInfo.put("companyId", company.getId());
                        }
                    } catch (Exception e) {
                        System.out.println("获取公司信息失败: " + e.getMessage());
                    }
                }

                return userInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回一个空对象，而不是null，确保JSON解析成功
        return new java.util.HashMap<>();
    }
}