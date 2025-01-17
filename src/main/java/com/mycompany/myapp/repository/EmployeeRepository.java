package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CountEmployee;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Ticket;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query(
        "select a from Employee a where LOWER(a.codeEmployee) LIKE %:searchCode% AND LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% and a.nhom LIKE %:searchNhom%"
    )
    Page<Employee> listAllEmployees(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        @Param("searchNhom") String searchNhom,
        Pageable pageable
    );

    @Query(
        "select a from Employee a where LOWER(a.codeEmployee) LIKE %:searchCode% AND LOWER(a.name) LIKE %:searchName% and a.department = :department and a.nhom LIKE %:searchNhom%"
    )
    Page<Employee> listAllEmployeesDepartment(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("department") String department,
        @Param("searchNhom") String searchNhom,
        Pageable pageable
    );

    @Query("select a from Employee a where a.department = :department")
    List<Employee> listAllEmployeesDepartmentNoPage(@Param("department") String department);

    @Query("select a from Employee a where a.codeEmployee = :codeEmployee")
    Employee getByCode(@Param("codeEmployee") String codeEmployee);

    @Query("SELECT new CountEmployee( d.code, count(d.code)) FROM Employee e JOIN Department d on e.department = d.code group by d.code")
    List<CountEmployee> listAllEmployeesDepartmentNoPage();
}
