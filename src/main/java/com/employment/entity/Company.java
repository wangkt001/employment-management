package com.employment.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String companyName;
    
    private String industry;
    private String scale;
    private String businessLicense;
    private String description;
    
    @Column(name = "is_verified", nullable = false, columnDefinition = "boolean default false")
    private boolean isVerified = false;
    
    @JsonProperty("isVerified")
    public boolean isVerified() {
        return isVerified;
    }
    
    @JsonProperty("isVerified")
    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}