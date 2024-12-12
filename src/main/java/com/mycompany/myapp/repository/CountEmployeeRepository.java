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
}
