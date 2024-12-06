package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.domain.AttendanceDetail;
import com.mycompany.myapp.domain.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Attendance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttendanceDetailRepository extends JpaRepository<AttendanceDetail, Long> {
    @Query("select a from AttendanceDetail a where a.attendanceId =  :id ")
    List<AttendanceDetail> findAttendanceDetailByAttendanceId(@Param("id") Long id);

    @Query("select count(a) from AttendanceDetail a where a.attendanceId =  :id ")
    Long countAttendanceDetailByAttendanceId(@Param("id") Long id);

    @Query("select count(a) from AttendanceDetail a where a.attendanceId =  :id and a.countTime = 8")
    Long countSuccessAttendanceDetailByAttendanceId(@Param("id") Long id);

    @Query("select sum(a.countTime) from AttendanceDetail a where a.attendanceId =  :id and a.countTime <> 8")
    Double countTimeNotSuccessAttendanceDetailByAttendanceId(@Param("id") Long id);

    @Query("select a from AttendanceDetail a where a.attendanceId = :id")
    List<AttendanceDetail> selectAllByAttId(@Param("id") Long id);

    void deleteAllByAttendanceId(@Param("attendanceId") Long attendanceId);
}
