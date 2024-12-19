package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Attendance;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Attendance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    //    @Query("select a.numberWorking from Attendance a where a.employeeId =  :id and a.month = :month and a.year = :year")
    //    BigDecimal getNumberWorkingByEmployeeId(@Param("id") Long id, @Param("month") Long month, @Param("year") Long year);

}
