package com.employment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "work_experience")
public class WorkExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String position;

    private String department;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean currentJob = false;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String achievements;
}
