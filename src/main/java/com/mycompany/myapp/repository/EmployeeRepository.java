package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Ticket;
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
        "select a from Employee a where LOWER(a.codeEmployee) LIKE %:searchCode% AND LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment%"
    )
    Page<Employee> listAllEmployees(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        Pageable pageable
    );
}
