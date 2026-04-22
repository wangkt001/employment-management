package com.employment.controller;

import com.employment.entity.Student;
import com.employment.entity.User;
import com.employment.entity.WorkExperience;
import com.employment.repository.StudentRepository;
import com.employment.repository.UserRepository;
import com.employment.repository.WorkExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentResumeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

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
        List<WorkExperience> workExperiences = student != null
                ? workExperienceRepository.findByStudentOrderByStartDateDesc(student)
                : new ArrayList<>();
        return ResponseEntity.ok(buildResumeData(user, student, workExperiences));
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

        if (resumeData.containsKey("workExperiences")) {
            saveWorkExperiences(student, (List<Map<String, Object>>) resumeData.get("workExperiences"));
        }

        List<WorkExperience> workExperiences = workExperienceRepository.findByStudentOrderByStartDateDesc(student);
        return ResponseEntity.ok(buildResumeData(user, student, workExperiences));
    }

    @DeleteMapping("/{userId}/resume/work-experiences/{experienceId}")
    public ResponseEntity<?> deleteWorkExperience(@PathVariable Long userId, @PathVariable Long experienceId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return buildError("用户不存在", HttpStatus.NOT_FOUND);
        }

        Student student = studentRepository.findByUserId(userId);
        if (student == null) {
            return buildError("学生记录不存在", HttpStatus.NOT_FOUND);
        }

        WorkExperience experience = workExperienceRepository.findById(experienceId).orElse(null);
        if (experience == null || !experience.getStudent().getId().equals(student.getId())) {
            return buildError("工作经历不存在", HttpStatus.NOT_FOUND);
        }

        workExperienceRepository.delete(experience);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }

    private void saveWorkExperiences(Student student, List<Map<String, Object>> experiences) {
        List<WorkExperience> existingExperiences = workExperienceRepository.findByStudentOrderByStartDateDesc(student);
        List<Long> existingIds = existingExperiences.stream().map(WorkExperience::getId).collect(Collectors.toList());

        for (Map<String, Object> expData : experiences) {
            Long id = expData.get("id") != null ? Long.valueOf(expData.get("id").toString()) : null;
            String companyName = normalize(expData.get("companyName"));
            String position = normalize(expData.get("position"));
            String startDateStr = normalize(expData.get("startDate"));

            if (!StringUtils.hasText(companyName) || !StringUtils.hasText(position)
                    || !StringUtils.hasText(startDateStr)) {
                continue;
            }

            WorkExperience experience;
            if (id != null && existingIds.contains(id)) {
                experience = workExperienceRepository.findById(id).orElse(null);
                if (experience == null)
                    continue;
            } else {
                experience = new WorkExperience();
                experience.setStudent(student);
            }

            experience.setCompanyName(companyName);
            experience.setPosition(position);
            experience.setDepartment(normalize(expData.get("department")));
            experience.setStartDate(LocalDate.parse(startDateStr));

            String endDateStr = normalize(expData.get("endDate"));
            if (StringUtils.hasText(endDateStr)) {
                experience.setEndDate(LocalDate.parse(endDateStr));
            } else {
                experience.setEndDate(null);
            }

            experience.setCurrentJob(Boolean.TRUE.equals(expData.get("currentJob")));
            experience.setDescription(normalize(expData.get("description")));
            experience.setAchievements(normalize(expData.get("achievements")));

            workExperienceRepository.save(experience);
        }

        List<Long> submittedIds = experiences.stream()
                .filter(e -> e.get("id") != null)
                .map(e -> Long.valueOf(e.get("id").toString()))
                .collect(Collectors.toList());

        List<Long> toDelete = existingIds.stream()
                .filter(id -> !submittedIds.contains(id))
                .collect(Collectors.toList());

        if (!toDelete.isEmpty()) {
            workExperienceRepository.deleteAllById(toDelete);
        }
    }

    private ResponseEntity<Map<String, Object>> buildError(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private Map<String, Object> buildResumeData(User user, Student student, List<WorkExperience> workExperiences) {
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

        List<Map<String, Object>> workExpList = new ArrayList<>();
        if (workExperiences != null) {
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
        }
        response.put("workExperiences", workExpList);
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
