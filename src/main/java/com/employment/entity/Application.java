package com.employment.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = true)
    private Job job;

    @Column(name = "apply_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date applyDate = new Date();

    @Column(nullable = false)
    private String status; // PENDING, REVIEWED, INTERVIEW, OFFER, REJECTED, ERROR

    private String resumeUrl;
    private String coverLetter;
    private String companyFeedback;
}