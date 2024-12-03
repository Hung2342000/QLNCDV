package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Attendance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {}
