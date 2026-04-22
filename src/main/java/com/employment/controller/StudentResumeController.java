package com.employment.controller;

import com.employment.entity.Student;
import com.employment.entity.User;
import com.employment.repository.StudentRepository;
import com.employment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentResumeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/{userId}/resume")
    public ResponseEntity<?> getResume(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return buildError("用户不存在", HttpStatus.NOT_FOUND);
        }
        if (!"STUDENT".equals(user.getRole())) {
            return buildError("当前用户不是学生", HttpStatus.BAD_REQUEST);
        }

        Student student = studentRepository.findByUserId(userId);
        return ResponseEntity.ok(buildResumeData(user, student));
    }

    @PutMapping("/{userId}/resume")
    public ResponseEntity<?> updateResume(@PathVariable Long userId, @RequestBody Map<String, Object> resumeData) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return buildError("用户不存在", HttpStatus.NOT_FOUND);
        }
        if (!"STUDENT".equals(user.getRole())) {
            return buildError("当前用户不是学生", HttpStatus.BAD_REQUEST);
        }

        String studentId = normalize(resumeData.get("studentId"));
        String major = normalize(resumeData.get("major"));
        if (!StringUtils.hasText(studentId) || !StringUtils.hasText(major)) {
            return buildError("学号和专业不能为空", HttpStatus.BAD_REQUEST);
        }

        user.setName(normalize(resumeData.get("name")));
        user.setEmail(normalize(resumeData.get("email")));
        user.setPhone(normalize(resumeData.get("phone")));
        user.setAddress(normalize(resumeData.get("address")));
        userRepository.save(user);

        Student student = studentRepository.findByUserId(userId);
        if (student == null) {
            student = new Student();
            student.setUser(user);
        }

        student.setStudentId(studentId);
        student.setMajor(major);
        student.setDepartment(normalize(resumeData.get("department")));
        student.setGrade(normalize(resumeData.get("grade")));
        student.setEducation(normalize(resumeData.get("education")));
        student.setSchool(normalize(resumeData.get("school")));
        student.setExpectedSalary(normalize(resumeData.get("expectedSalary")));
        student.setCareerDirection(normalize(resumeData.get("careerDirection")));
        student.setSelfIntroduction(normalize(resumeData.get("selfIntroduction")));
        student.setResumeUrl(normalize(resumeData.get("resumeUrl")));
        studentRepository.save(student);

        return ResponseEntity.ok(buildResumeData(user, student));
    }

    private ResponseEntity<Map<String, Object>> buildError(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private Map<String, Object> buildResumeData(User user, Student student) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("address", user.getAddress());

        response.put("studentId", student != null ? student.getStudentId() : "");
        response.put("major", student != null ? student.getMajor() : "");
        response.put("department", student != null ? student.getDepartment() : "");
        response.put("grade", student != null ? student.getGrade() : "");
        response.put("education", student != null ? student.getEducation() : "");
        response.put("school", student != null ? student.getSchool() : "");
        response.put("expectedSalary", student != null ? student.getExpectedSalary() : "");
        response.put("careerDirection", student != null ? student.getCareerDirection() : "");
        response.put("selfIntroduction", student != null ? student.getSelfIntroduction() : "");
        response.put("resumeUrl", student != null ? student.getResumeUrl() : "");
        return response;
    }

    private String normalize(Object value) {
        if (value == null) {
            return null;
        }

        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }
}
