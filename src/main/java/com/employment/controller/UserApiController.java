package com.employment.controller;

import com.employment.entity.User;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserRepository userRepository;

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
}