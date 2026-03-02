package com.employment.repository;

import com.employment.entity.Student;
import com.employment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUser(User user);
}