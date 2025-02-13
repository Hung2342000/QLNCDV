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

    void deleteAllBySalaryId(@Param("idSalary") Long idSalary);
    List<SalaryDetail> getSalaryDetailBySalaryId(@Param("idSalary") Long idSalary);

    @Query("select a from SalaryDetail a where a.salaryId = :idSalary AND a.nhom = 'GDV' ORDER BY a.dichVu asc, a.tenDonVi asc")
    List<SalaryDetail> getAllBySalaryIdGDV(@Param("idSalary") Long idSalary);

    @Query("select a from SalaryDetail a where a.salaryId = :idSalary AND a.nhom = 'HTVP' ORDER BY a.dichVu asc, a.tenDonVi asc")
    List<SalaryDetail> getAllBySalaryIdHtvp(@Param("idSalary") Long idSalary);

    @Query("select a from SalaryDetail a where a.salaryId = :idSalary AND a.nhom = 'AM' ORDER BY a.dichVu asc, a.tenDonVi asc")
    List<SalaryDetail> getAllBySalaryIdAM(@Param("idSalary") Long idSalary);

    @Query("select a from SalaryDetail a where a.salaryId = :idSalary AND a.nhom = 'KAM' ORDER BY a.dichVu asc, a.tenDonVi asc")
    List<SalaryDetail> getAllBySalaryIdKAM(@Param("idSalary") Long idSalary);

    @Query("select a from SalaryDetail a where a.salaryId = :idSalary AND a.nhom = 'NVBH' ORDER BY a.dichVu asc, a.tenDonVi asc")
    List<SalaryDetail> getAllBySalaryIdNVBH(@Param("idSalary") Long idSalary);

    @Query(
        "SELECT sd FROM SalaryDetail sd JOIN Employee e on sd.employeeId = e.id  where sd.salaryId = :idSalary and e.codeEmployee = :employeeCode AND ROWNUM = 1"
    )
    SalaryDetail getSalaryDetailBySalaryIdEndEmployeeCode(@Param("idSalary") Long idSalary, @Param("employeeCode") String employeeCode);
}
