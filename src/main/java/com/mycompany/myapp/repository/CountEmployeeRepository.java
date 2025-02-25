package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CountEmployee;
import com.mycompany.myapp.domain.Employee;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CountEmployeeRepository extends JpaRepository<CountEmployee, Long> {
    @Query(
        "SELECT  new CountEmployee ( d.name, count(*)) FROM Employee e JOIN Department d on e.department = d.code group by d.code, d.name  ORDER BY count(d.code) desc "
    )
    List<CountEmployee> listAllCountEmployee();

    @Query(
        "SELECT  new CountEmployee ( e.nhom, count(*)) FROM Employee e where e.startDate <= TO_DATE(:startDate, 'YYYY-MM-DD') and ( e.closeDate <= TO_DATE(:endDate, 'YYYY-MM-DD') or e.closeDate is NULL) and e.department like %:department%  group by e.nhom  "
    )
    List<CountEmployee> listAllCountEmployeeByNhomDate(
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("department") String department
    );

    @Query(
        "SELECT  new CountEmployee ( e.nhom, count(*)) FROM Employee e where (e.startDate <= TO_DATE(:startDate, 'YYYY-MM-DD') or  e.startDate is null)  and e.department like %:department%  group by e.nhom  "
    )
    List<CountEmployee> listAllCountEmployeeByNhomStartDate(@Param("startDate") String startDate, @Param("department") String department);

    @Query("SELECT  new CountEmployee ( e.nhom, count(*)) FROM Employee e where e.department like %:department% group by e.nhom  ")
    List<CountEmployee> listAllCountEmployeeByNhom(@Param("department") String department);

    @Query(
        "SELECT  new CountEmployee ( e.status, count(*)) FROM Employee e where e.startDate <= TO_DATE(:startDate, 'YYYY-MM-DD') and ( e.closeDate <= TO_DATE(:endDate, 'YYYY-MM-DD') or e.closeDate is NULL) and e.department like %:department% group by e.status"
    )
    List<CountEmployee> listAllCountEmployeeByStatusDate(
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("department") String department
    );

    @Query(
        "SELECT  new CountEmployee ( e.status, count(*)) FROM Employee e where (e.startDate <= TO_DATE(:startDate, 'YYYY-MM-DD') or  e.startDate is null) and e.department like %:department%  group by e.status"
    )
    List<CountEmployee> listAllCountEmployeeByStatusStartDate(@Param("startDate") String startDate, @Param("department") String department);

    @Query("SELECT  new CountEmployee ( e.status, count(*)) FROM Employee e where e.department like %:department% group by e.status")
    List<CountEmployee> listAllCountEmployeeByStatus(@Param("department") String department);

    @Query("SELECT  new CountEmployee ( e.nhom, count(*)) FROM Employee e where e.department = :department group by e.nhom  ")
    List<CountEmployee> listAllCountEmployeeByNhomAnDDepartment(@Param("department") String department);
}
