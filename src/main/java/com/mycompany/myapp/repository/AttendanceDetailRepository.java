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
    @Query("select a from AttendanceDetail a where a.attendanceId = :id")
    List<AttendanceDetail> selectAllByAttId(@Param("id") Long id);

    @Query("select a from AttendanceDetail a where a.attendanceId = :id and a.nhom <> :nhom")
    List<AttendanceDetail> selectAllByAttIdAndKhacNhom(@Param("id") Long id, @Param("nhom") String nhom);

    @Query("select a from AttendanceDetail a where a.attendanceId = :id and a.nhom = :nhom")
    List<AttendanceDetail> selectAllByAttIdAndNhom(@Param("id") Long id, @Param("nhom") String nhom);

    @Query(
        "select a from AttendanceDetail a where a.attendanceId = :id and LOWER(a.employeeCode) LIKE %:searchCode% AND LOWER(a.employeeName) LIKE %:searchName% and a.department LIKE %:searchDepartment%"
    )
    List<AttendanceDetail> selectAllByAttIdSearch(
        @Param("id") Long id,
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment
    );

    @Query("select a from AttendanceDetail a where a.attendanceId = :id and a.employeeId = :employeeId")
    AttendanceDetail selectAllByAttIdAndEm(@Param("id") Long id, @Param("employeeId") Long employeeId);

    void deleteAllByAttendanceId(@Param("attendanceId") Long attendanceId);
}
