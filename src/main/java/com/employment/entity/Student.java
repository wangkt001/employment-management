package com.employment.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String studentId;
    
    @Column(nullable = false)
    private String major;
    
    private String department;
    private String grade;
    private String resumeUrl;
    private String expectedSalary;
    private String careerDirection;
}