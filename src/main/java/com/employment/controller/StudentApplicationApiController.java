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
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 申请岗位
    @PostMapping("/job/{jobId}")
    public Application applyForJob(@PathVariable Long jobId, @RequestParam Long userId) {
        System.out.println("===========================================");
        System.out.println("接收到申请岗位请求，jobId: " + jobId + ", userId: " + userId);
        System.out.println("===========================================");
        try {
            // 1. 校验用户ID
            System.out.println("1. 校验用户ID");
            if (userId == null) {
                System.out.println("用户ID不能为空");
                throw new RuntimeException("用户ID不能为空");
            }

            // 2. 查找岗位
            System.out.println("2. 查找岗位，jobId: " + jobId);
            Job job = null;
            try {
                System.out.println("尝试通过ID查找岗位: " + jobId);
                // 先检查岗位是否存在
                boolean jobExists = jobRepository.existsById(jobId);
                System.out.println("岗位是否存在: " + jobExists);

                if (!jobExists) {
                    System.out.println("岗位不存在，jobId: " + jobId);
                    throw new RuntimeException("岗位不存在，jobId: " + jobId);
                }

                job = jobRepository.findById(jobId).get();
                System.out.println("找到岗位，jobId: " + job.getId() + ", title: " + job.getTitle());
                if (job.getCompany() != null) {
                    System.out.println("岗位的companyId: " + job.getCompany().getId());
                } else {
                    System.out.println("岗位没有关联公司");
                }
            } catch (Exception e) {
                System.out.println("查找岗位失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("查找岗位失败: " + e.getMessage());
            }

            // 3. 获取用户
            System.out.println("3. 获取用户，userId: " + userId);
            User user = null;
            try {
                user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    System.out.println("用户不存在，userId: " + userId);
                    throw new RuntimeException("用户不存在，userId: " + userId);
                } else {
                    System.out.println("用户已存在，userId: " + user.getId() + ", username: " + user.getUsername());
                }
            } catch (Exception e) {
                System.out.println("获取用户失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("获取用户失败: " + e.getMessage());
            }

            // 3. 获取或创建学生记录
            System.out.println("3. 获取或创建学生记录");
            Student student = null;
            try {
                student = studentRepository.findByUser(user);
                if (student == null) {
                    System.out.println("学生记录不存在，创建新学生记录");
                    student = new Student();
                    student.setUser(user);
                    student.setStudentId("STU" + System.currentTimeMillis());
                    student.setMajor("未设置");
                    // 先保存用户，确保用户ID已经生成
                    if (user.getId() == null) {
                        System.out.println("用户ID为null，重新保存用户");
                        user = userRepository.save(user);
                        System.out.println("重新保存用户成功，userId: " + user.getId());
                        student.setUser(user);
                    }
                    student = studentRepository.save(student);
                    System.out.println("创建学生记录成功，studentId: " + student.getId());
                } else {
                    System.out.println("学生记录已存在，studentId: " + student.getId());
                }
            } catch (Exception e) {
                System.out.println("获取或创建学生记录失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("获取或创建学生记录失败: " + e.getMessage());
            }

            // 4. 检查是否已经申请过该岗位
            System.out.println("4. 检查是否已经申请过该岗位");
            try {
                List<Application> existingApplications = applicationRepository.findByStudent(student);
                System.out.println("当前学生的申请记录数量: " + existingApplications.size());
                boolean alreadyApplied = existingApplications.stream()
                        .anyMatch(app -> {
                            if (app.getJob() == null) {
                                System.out.println("申请记录中的job为null");
                                return false;
                            }
                            boolean match = app.getJob().getId().equals(jobId);
                            if (match) {
                                System.out.println("已经申请过该岗位，jobId: " + jobId);
                            }
                            return match;
                        });
                if (alreadyApplied) {
                    throw new RuntimeException("已经申请过该岗位");
                }
            } catch (Exception e) {
                System.out.println("检查申请记录失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("检查申请记录失败: " + e.getMessage());
            }

            // 5. 创建申请
            System.out.println("5. 创建申请");
            Application application = new Application();
            application.setStudent(student); // 使用Student作为学生
            application.setJob(job);
            application.setStatus("pending");
            application.setApplyDate(new Date());
            System.out.println("申请对象创建成功，studentId: " + student.getId() + ", jobId: " + job.getId());

            // 6. 保存申请
            System.out.println("6. 保存申请");
            System.out.println("准备保存申请，studentId: " + student.getId() + ", jobId: " + job.getId());
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
            // 打印完整的堆栈跟踪
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : e.getStackTrace()) {
                sb.append(element.toString()).append("\n");
            }
            System.out.println("堆栈跟踪: " + sb.toString());
            // 返回一个空的申请对象，避免服务崩溃
            Application errorApplication = new Application();
            errorApplication.setStatus("ERROR");
            errorApplication.setApplyDate(new Date());
            return errorApplication;
        }
    }

    // 获取学生的所有申请
    @GetMapping("/student")
    public List<Application> getStudentApplications(@RequestParam Long userId) {
        try {
            // 1. 校验用户ID
            System.out.println("1. 校验用户ID");
            if (userId == null) {
                System.out.println("用户ID不能为空");
                throw new RuntimeException("用户ID不能为空");
            }

            // 2. 获取用户
            System.out.println("2. 获取用户，userId: " + userId);
            User user = null;
            user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                System.out.println("用户不存在，userId: " + userId);
                throw new RuntimeException("用户不存在，userId: " + userId);
            }
            System.out.println("用户已存在，userId: " + user.getId() + ", username: " + user.getUsername());

            // 2. 获取学生对象
            Student student = studentRepository.findByUser(user);
            if (student == null) {
                System.out.println("学生记录不存在，创建新学生记录");
                student = new Student();
                student.setUser(user);
                student.setStudentId("STU" + System.currentTimeMillis());
                student.setMajor("未设置");
                // 先保存用户，确保用户ID已经生成
                if (user.getId() == null) {
                    System.out.println("用户ID为null，重新保存用户");
                    user = userRepository.save(user);
                    System.out.println("重新保存用户成功，userId: " + user.getId());
                    student.setUser(user);
                }
                student = studentRepository.save(student);
                System.out.println("创建学生记录成功，studentId: " + student.getId());
            } else {
                System.out.println("学生记录已存在，studentId: " + student.getId());
            }

            // 3. 获取学生的所有申请
            List<Application> applications = applicationRepository.findByStudent(student);
            System.out.println("获取到 " + applications.size() + " 个申请记录");

            // 4. 确保 job 和 company 对象被加载
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
