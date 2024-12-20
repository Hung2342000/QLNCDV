package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Attendance;
import java.math.BigDecimal;
import java.util.List;
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
    @Query("select a from Attendance a where a.month = :month and a.year = :year")
    Attendance getAttendanceByMonthEndYear(@Param("month") Long month, @Param("year") Long year);

    @Query("select a from Attendance a where a.departmentCode = :departmentCode")
    List<Attendance> getAttendanceByDepartment(@Param("departmentCode") String departmentCode);

    @Query("select a from Attendance a where a.departmentCode = :departmentCode")
    Page<Attendance> getAttendanceByDepartment(@Param("departmentCode") String departmentCode, Pageable pageable);
}
