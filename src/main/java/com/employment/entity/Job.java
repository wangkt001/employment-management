package com.employment.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String position;
    
    private String salaryRange;
    private String requirements;
    private String responsibilities;
    private String workingLocation;
    private String educationLevel;
    private String workExperience;
    private String industry;
    
    @Column(name = "publish_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate = new Date();
    
    @Column(name = "expire_date")
    @Temporal(TemporalType.DATE)
    private Date expireDate;
    
    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;
    
    private String tags;
}