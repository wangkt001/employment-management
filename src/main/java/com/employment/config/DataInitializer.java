package com.employment.config;

import com.employment.entity.User;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 初始化admin用户
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setName("系统管理员");
            admin.setEmail("admin@example.com");
            admin.setPhone("13800138000");
            admin.setAddress("北京市朝阳区");
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("初始化admin用户成功");
        }
        
        // 初始化测试用户ceshi
        if (userRepository.findByUsername("ceshi") == null) {
            User ceshi = new User();
            ceshi.setUsername("ceshi");
            ceshi.setPassword(passwordEncoder.encode("12345"));
            ceshi.setRole("STUDENT");
            ceshi.setName("测试用户");
            ceshi.setEmail("ceshi@example.com");
            ceshi.setPhone("13900139000");
            ceshi.setAddress("上海市浦东新区");
            ceshi.setActive(true);
            userRepository.save(ceshi);
            System.out.println("初始化ceshi用户成功");
        }
    }
}
