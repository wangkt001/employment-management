package com.employment.controller;

import com.employment.entity.Company;
import com.employment.entity.User;
import com.employment.repository.CompanyRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
public class CompanyApiController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/info")
    public ResponseEntity<?> getCompanyInfo(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(result);
            }

            Company company = companyRepository.findByUserId(userId);
            if (company == null) {
                result.put("success", false);
                result.put("message", "企业不存在");
                result.put("hasCompany", false);
                return ResponseEntity.ok(result);
            }

            result.put("success", true);
            result.put("hasCompany", true);
            result.put("id", company.getId());
            result.put("companyName", company.getCompanyName());
            result.put("industry", company.getIndustry());
            result.put("scale", company.getScale());
            result.put("nature", company.getNature());
            result.put("businessLicense", company.getBusinessLicense());
            result.put("description", company.getDescription());
            result.put("contactPerson", company.getContactPerson());
            result.put("contactPhone", company.getContactPhone());
            result.put("contactEmail", company.getContactEmail());
            result.put("address", company.getAddress());
            result.put("website", company.getWebsite());
            result.put("isVerified", company.isVerified());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取企业信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCompany(@RequestBody Map<String, Object> companyData, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(result);
            }

            Company existingCompany = companyRepository.findByUserId(userId);
            if (existingCompany != null) {
                result.put("success", false);
                result.put("message", "您已经有企业记录了，请勿重复创建");
                return ResponseEntity.badRequest().body(result);
            }

            Company company = new Company();
            company.setUser(user);
            company.setCompanyName(
                    companyData.get("companyName") != null ? companyData.get("companyName").toString().trim() : "");
            company.setIndustry(
                    companyData.get("industry") != null ? companyData.get("industry").toString().trim() : "");
            company.setScale(companyData.get("scale") != null ? companyData.get("scale").toString().trim() : "");
            company.setNature(companyData.get("nature") != null ? companyData.get("nature").toString().trim() : "");
            company.setBusinessLicense(
                    companyData.get("businessLicense") != null ? companyData.get("businessLicense").toString().trim()
                            : "");
            company.setDescription(
                    companyData.get("description") != null ? companyData.get("description").toString().trim() : "");
            company.setContactPerson(
                    companyData.get("contactPerson") != null ? companyData.get("contactPerson").toString().trim() : "");
            company.setContactPhone(
                    companyData.get("contactPhone") != null ? companyData.get("contactPhone").toString().trim() : "");
            company.setContactEmail(
                    companyData.get("contactEmail") != null ? companyData.get("contactEmail").toString().trim() : "");
            company.setAddress(
                    companyData.get("address") != null ? companyData.get("address").toString().trim() : "");
            company.setWebsite(
                    companyData.get("website") != null ? companyData.get("website").toString().trim() : "");
            company.setVerified(false);

            if (company.getCompanyName().isEmpty()) {
                result.put("success", false);
                result.put("message", "企业名称不能为空");
                return ResponseEntity.badRequest().body(result);
            }

            Company savedCompany = companyRepository.save(company);
            result.put("success", true);
            result.put("message", "企业创建成功");
            result.put("id", savedCompany.getId());
            result.put("companyName", savedCompany.getCompanyName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "创建企业失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCompany(@RequestBody Map<String, Object> companyData, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(result);
            }

            Company company = companyRepository.findByUserId(userId);
            if (company == null) {
                result.put("success", false);
                result.put("message", "企业不存在，请先创建企业");
                return ResponseEntity.badRequest().body(result);
            }

            if (companyData.get("companyName") != null) {
                company.setCompanyName(companyData.get("companyName").toString().trim());
            }
            if (companyData.get("industry") != null) {
                company.setIndustry(companyData.get("industry").toString().trim());
            }
            if (companyData.get("scale") != null) {
                company.setScale(companyData.get("scale").toString().trim());
            }
            if (companyData.get("nature") != null) {
                company.setNature(companyData.get("nature").toString().trim());
            }
            if (companyData.get("businessLicense") != null) {
                company.setBusinessLicense(companyData.get("businessLicense").toString().trim());
            }
            if (companyData.get("description") != null) {
                company.setDescription(companyData.get("description").toString().trim());
            }
            if (companyData.get("contactPerson") != null) {
                company.setContactPerson(companyData.get("contactPerson").toString().trim());
            }
            if (companyData.get("contactPhone") != null) {
                company.setContactPhone(companyData.get("contactPhone").toString().trim());
            }
            if (companyData.get("contactEmail") != null) {
                company.setContactEmail(companyData.get("contactEmail").toString().trim());
            }
            if (companyData.get("address") != null) {
                company.setAddress(companyData.get("address").toString().trim());
            }
            if (companyData.get("website") != null) {
                company.setWebsite(companyData.get("website").toString().trim());
            }

            if (company.getCompanyName().isEmpty()) {
                result.put("success", false);
                result.put("message", "企业名称不能为空");
                return ResponseEntity.badRequest().body(result);
            }

            Company updatedCompany = companyRepository.save(company);
            result.put("success", true);
            result.put("message", "企业信息更新成功");
            result.put("id", updatedCompany.getId());
            result.put("companyName", updatedCompany.getCompanyName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "更新企业失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
