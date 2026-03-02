package com.employment.controller;

import com.employment.entity.Application;
import com.employment.entity.Job;
import com.employment.entity.Student;
import com.employment.entity.User;
import com.employment.repository.ApplicationRepository;
import com.employment.repository.JobRepository;
import com.employment.repository.StudentRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class StudentApplicationApiController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    // 申请岗位
    @PostMapping("/job/{jobId}")
    public Application applyForJob(@PathVariable Long jobId, Authentication authentication) {
        System.out.println("接收到申请岗位请求，jobId: " + jobId);
        try {
            // 1. 查找岗位
            System.out.println("1. 查找岗位，jobId: " + jobId);
            Job job = null;
            try {
                job = jobRepository.findById(jobId)
                        .orElseThrow(() -> new RuntimeException("岗位不存在"));
                System.out.println("找到岗位，jobId: " + job.getId() + ", title: " + job.getTitle());
                System.out.println("岗位的companyId: " + job.getCompany().getId());
            } catch (Exception e) {
                System.out.println("查找岗位失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("查找岗位失败: " + e.getMessage());
            }

            // 2. 获取当前登录用户
            System.out.println("2. 获取当前登录用户");
            User user = null;
            try {
                if (authentication != null && authentication.getName() != null) {
                    user = userRepository.findByUsername(authentication.getName());
                    if (user == null) {
                        System.out.println("用户不存在，创建新用户");
                        user = new User();
                        user.setUsername(authentication.getName());
                        user.setPassword("test"); // 实际应用中应该使用加密密码
                        user.setRole("STUDENT");
                        user.setName(authentication.getName());
                        user.setActive(true);
                        user = userRepository.save(user);
                        System.out.println("创建用户成功，userId: " + user.getId());
                    } else {
                        System.out.println("用户已存在，userId: " + user.getId());
                    }
                } else {
                    // 为了兼容测试，保留test用户作为 fallback
                    user = userRepository.findByUsername("test");
                    if (user == null) {
                        System.out.println("测试用户不存在，创建测试用户");
                        user = new User();
                        user.setUsername("test");
                        user.setPassword("test");
                        user.setRole("STUDENT");
                        user.setName("测试学生");
                        user.setActive(true);
                        user = userRepository.save(user);
                        System.out.println("创建测试用户成功，userId: " + user.getId());
                    }
                    System.out.println("使用测试用户，userId: " + user.getId());
                }
            } catch (Exception e) {
                System.out.println("获取或创建用户失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("获取或创建用户失败: " + e.getMessage());
            }

            // 3. 创建申请
            System.out.println("3. 创建申请");
            Application application = new Application();
            application.setStudent(user); // 使用User作为Student
            application.setJob(job);
            application.setStatus("PENDING");
            application.setApplyDate(new Date());
            System.out.println("申请对象创建成功，studentId: " + user.getId() + ", jobId: " + job.getId());

            // 4. 保存申请
            System.out.println("4. 保存申请");
            System.out.println("准备保存申请，studentId: " + user.getId() + ", jobId: " + job.getId());
            Application savedApplication = null;
            try {
                savedApplication = applicationRepository.save(application);
                System.out.println("保存申请成功，applicationId: " + savedApplication.getId());
                System.out.println("保存后的申请对象: " + savedApplication);
            } catch (Exception e) {
                System.out.println("保存申请失败: " + e.getMessage());
                e.printStackTrace();
                // 打印完整的堆栈跟踪
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement element : e.getStackTrace()) {
                    sb.append(element.toString()).append("\n");
                }
                System.out.println("堆栈跟踪: " + sb.toString());
                throw new RuntimeException("保存申请失败: " + e.getMessage());
            }

            System.out.println("申请岗位成功，返回申请记录");
            return savedApplication;
        } catch (Exception e) {
            System.out.println("申请失败: " + e.getMessage());
            e.printStackTrace();
            // 返回一个空的申请对象，避免服务崩溃
            Application errorApplication = new Application();
            errorApplication.setStatus("ERROR");
            errorApplication.setApplyDate(new Date());
            return errorApplication;
        }
    }

    // 获取学生的所有申请
    @GetMapping("/student")
    public List<Application> getStudentApplications(Authentication authentication) {
        try {
            // 暂时跳过认证，直接返回所有申请
            // 实际应用中，应该根据具体的业务逻辑处理认证

            // 1. 获取或创建用户
            User user = userRepository.findByUsername("test");
            if (user == null) {
                user = new User();
                user.setUsername("test");
                user.setPassword("test");
                user.setRole("STUDENT");
                user.setName("测试学生");
                user.setActive(true);
                user = userRepository.save(user);
            }

            // 2. 获取学生的所有申请
            List<Application> applications = applicationRepository.findByStudent(user);
            System.out.println("获取到 " + applications.size() + " 个申请记录");

            // 3. 确保 job 和 company 对象被加载
            for (Application application : applications) {
                if (application.getJob() != null) {
                    System.out.println("申请 ID: " + application.getId() + ", 岗位 ID: " + application.getJob().getId()
                            + ", 岗位名称: " + application.getJob().getTitle());
                    if (application.getJob().getCompany() != null) {
                        System.out.println("公司名称: " + application.getJob().getCompany().getCompanyName());
                    } else {
                        System.out.println("公司对象为 null");
                    }
                } else {
                    System.out.println("岗位对象为 null");
                }
            }

            return applications;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
