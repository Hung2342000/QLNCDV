package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.domain.SalaryDetail;
import java.math.BigDecimal;
import java.util.List;
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
public interface SalaryDetailRepository extends JpaRepository<SalaryDetail, Long> {
    @Query("select a from SalaryDetail a where a.salaryId = :idSalary ")
    Page<SalaryDetail> getAllBySalaryId(Pageable pageable, @Param("idSalary") Long idSalary);
}
