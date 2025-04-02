package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.service.dto.NhanVienDTO;
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
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    @Query("select a from Salary a where a.departmentCode = :departmentCode")
    Page<Salary> getSalaryByDepartment(@Param("departmentCode") String departmentCode, Pageable pageable);

    @Query(
        "select new com.mycompany.myapp.service.dto.NhanVienDTO (e.id, e.codeEmployee, e.name, e.serviceTypeName, e.region, e.rank, e.nhom,s.month, s.year, sd.kpis,sd.htc) " +
        "from Salary s join SalaryDetail sd on s.id = sd.salaryId join Employee e on e.id = sd.employeeId " +
        " where s.year = :nam and e.nhom = :nhom and e.id = :id"
    )
    List<NhanVienDTO> getNhanVienDTO(@Param("nam") Long nam, @Param("nhom") String nhom, @Param("id") Long id);
}
