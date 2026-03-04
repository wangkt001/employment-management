package com.employment.config;

import com.employment.entity.Company;
import com.employment.entity.User;
import com.employment.repository.CompanyRepository;
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
    private CompanyRepository companyRepository;

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
        
        // 初始化公司用户
        /*if (userRepository.findByUsername("company") == null) {
            // 创建公司用户
            User companyUser = new User();
            companyUser.setUsername("company");
            companyUser.setPassword(passwordEncoder.encode("123456"));
            companyUser.setRole("COMPANY");
            companyUser.setName("测试企业");
            companyUser.setEmail("company@example.com");
            companyUser.setPhone("13700137000");
            companyUser.setAddress("广州市天河区");
            companyUser.setActive(true);
            userRepository.save(companyUser);
            System.out.println("初始化company用户成功");
            
            // 创建对应的公司信息
            Company company = new Company();
            company.setUser(companyUser);
            company.setCompanyName("测试科技有限公司");
            company.setIndustry("互联网");
            company.setScale("50-100人");
            company.setBusinessLicense("123456789012345");
            company.setDescription("一家专注于技术创新的科技公司");
            company.setVerified(true);
            companyRepository.save(company);
            System.out.println("初始化公司信息成功，companyId: " + company.getId());
        }*/
    }
}
