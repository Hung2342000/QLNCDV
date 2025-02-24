package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.domain.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Attendance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    @Query("select a from Salary a where a.departmentCode = :departmentCode")
    Page<Salary> getSalaryByDepartment(@Param("departmentCode") String departmentCode, Pageable pageable);
}
