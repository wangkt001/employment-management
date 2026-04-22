package com.employment.controller;

import com.employment.entity.Application;
import com.employment.entity.Company;
import com.employment.entity.Job;
import com.employment.entity.Student;
import com.employment.entity.User;
import com.employment.entity.WorkExperience;
import com.employment.repository.ApplicationRepository;
import com.employment.repository.CompanyRepository;
import com.employment.repository.JobRepository;
import com.employment.repository.StudentRepository;
import com.employment.repository.WorkExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/company/applications")
public class CompanyApplicationApiController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    // 获取公司的所有申请
    @GetMapping
    public List<Application> getCompanyApplications(@RequestParam Long companyId) {
        System.out.println("===========================================");
        System.out.println("接收到获取公司申请请求，companyId: " + companyId);
        System.out.println("===========================================");
        try {
            // 1. 校验公司ID
            if (companyId == null) {
                System.out.println("公司ID不能为空");
                throw new RuntimeException("公司ID不能为空");
            }

            // 2. 获取公司
            Company company = companyRepository.findById(companyId).orElse(null);
            if (company == null) {
                System.out.println("公司不存在，companyId: " + companyId);
                throw new RuntimeException("公司不存在，companyId: " + companyId);
            }
            System.out.println("公司已存在，companyId: " + company.getId() + ", companyName: " + company.getCompanyName());

            // 3. 获取公司的所有岗位
            List<Job> jobs = jobRepository.findByCompanyId(company.getId());
            System.out.println("公司的岗位数量: " + jobs.size());

            // 4. 获取这些岗位的所有申请
            List<Application> applications = jobs.stream()
                    .flatMap(job -> applicationRepository.findByJob(job).stream())
                    .collect(Collectors.toList());
            System.out.println("获取到 " + applications.size() + " 个申请记录");

            // 5. 确保相关对象被加载
            for (Application application : applications) {
                if (application.getJob() != null) {
                    System.out.println("申请 ID: " + application.getId() + ", 岗位 ID: " + application.getJob().getId()
                            + ", 岗位名称: " + application.getJob().getTitle());
                }
                if (application.getStudent() != null) {
                    System.out.println("申请人: " + application.getStudent().getUser().getName());
                }
            }

            return applications;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 批准申请
    @PutMapping("/{applicationId}/approve")
    public Application approveApplication(@PathVariable Long applicationId) {
        try {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            if (application == null) {
                throw new RuntimeException("申请不存在");
            }
            application.setStatus("approved");
            return applicationRepository.save(application);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 拒绝申请
    @PutMapping("/{applicationId}/reject")
    public Application rejectApplication(@PathVariable Long applicationId) {
        try {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            if (application == null) {
                throw new RuntimeException("申请不存在");
            }
            application.setStatus("rejected");
            return applicationRepository.save(application);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 安排面试
    @PutMapping("/{applicationId}/schedule-interview")
    public Map<String, Object> scheduleInterview(@PathVariable Long applicationId, @RequestBody Map<String, Object> interviewData) {
        Map<String, Object> result = new HashMap<>();
        try {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            if (application == null) {
                result.put("success", false);
                result.put("message", "申请不存在");
                return result;
            }

            application.setStatus("INTERVIEW");
            application.setInterviewResult("PENDING");

            if (interviewData.get("interviewTime") != null) {
                String timeStr = interviewData.get("interviewTime").toString();
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                    application.setInterviewTime(sdf.parse(timeStr));
                } catch (Exception e) {
                    result.put("success", false);
                    result.put("message", "面试时间格式错误，请使用 yyyy-MM-dd HH:mm 格式");
                    return result;
                }
            }
            if (interviewData.get("interviewLocation") != null) {
                application.setInterviewLocation(interviewData.get("interviewLocation").toString().trim());
            }
            if (interviewData.get("interviewFeedback") != null) {
                application.setInterviewFeedback(interviewData.get("interviewFeedback").toString().trim());
            }

            applicationRepository.save(application);
            result.put("success", true);
            result.put("message", "面试安排成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "安排面试失败: " + e.getMessage());
            return result;
        }
    }

    // 更新面试结果
    @PutMapping("/{applicationId}/interview-result")
    public Map<String, Object> updateInterviewResult(@PathVariable Long applicationId, @RequestBody Map<String, Object> resultData) {
        Map<String, Object> result = new HashMap<>();
        try {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            if (application == null) {
                result.put("success", false);
                result.put("message", "申请不存在");
                return result;
            }

            String interviewResult = resultData.get("interviewResult").toString().trim();
            if (!"PASS".equals(interviewResult) && !"FAIL".equals(interviewResult)) {
                result.put("success", false);
                result.put("message", "面试结果必须为 PASS 或 FAIL");
                return result;
            }

            application.setInterviewResult(interviewResult);
            if ("PASS".equals(interviewResult)) {
                application.setStatus("OFFER");
            } else {
                application.setStatus("REJECTED");
            }

            if (resultData.get("interviewFeedback") != null) {
                application.setInterviewFeedback(resultData.get("interviewFeedback").toString().trim());
            }

            applicationRepository.save(application);
            result.put("success", true);
            result.put("message", "面试结果已更新");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "更新面试结果失败: " + e.getMessage());
            return result;
        }
    }

    // 获取申请对应的学生简历详情
    @GetMapping("/{applicationId}/student-resume")
    public Map<String, Object> getStudentResume(@PathVariable Long applicationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            if (application == null || application.getStudent() == null) {
                result.put("success", false);
                result.put("message", "申请或学生记录不存在");
                return result;
            }

            Student student = application.getStudent();
            User user = student.getUser();

            result.put("success", true);
            result.put("name", user.getName());
            result.put("email", user.getEmail());
            result.put("phone", user.getPhone());
            result.put("address", user.getAddress());
            result.put("studentId", student.getStudentId());
            result.put("major", student.getMajor());
            result.put("department", student.getDepartment());
            result.put("grade", student.getGrade());
            result.put("education", student.getEducation());
            result.put("school", student.getSchool());
            result.put("expectedSalary", student.getExpectedSalary());
            result.put("careerDirection", student.getCareerDirection());
            result.put("selfIntroduction", student.getSelfIntroduction());

            result.put("status", application.getStatus());
            result.put("interviewTime", application.getInterviewTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(application.getInterviewTime()) : "");
            result.put("interviewLocation", application.getInterviewLocation() != null ? application.getInterviewLocation() : "");
            result.put("interviewResult", application.getInterviewResult() != null ? application.getInterviewResult() : "");
            result.put("interviewFeedback", application.getInterviewFeedback() != null ? application.getInterviewFeedback() : "");

            List<WorkExperience> workExperiences = workExperienceRepository.findByStudentOrderByStartDateDesc(student);
            List<Map<String, Object>> workExpList = new ArrayList<>();
            for (WorkExperience exp : workExperiences) {
                Map<String, Object> expMap = new HashMap<>();
                expMap.put("id", exp.getId());
                expMap.put("companyName", exp.getCompanyName());
                expMap.put("position", exp.getPosition());
                expMap.put("department", exp.getDepartment());
                expMap.put("startDate", exp.getStartDate() != null ? exp.getStartDate().toString() : "");
                expMap.put("endDate", exp.getEndDate() != null ? exp.getEndDate().toString() : "");
                expMap.put("currentJob", exp.getCurrentJob());
                expMap.put("description", exp.getDescription());
                expMap.put("achievements", exp.getAchievements());
                workExpList.add(expMap);
            }
            result.put("workExperiences", workExpList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取简历详情失败: " + e.getMessage());
            return result;
        }
    }
}
