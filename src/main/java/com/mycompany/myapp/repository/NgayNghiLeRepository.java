package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.NgayNghiLe;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface NgayNghiLeRepository extends JpaRepository<NgayNghiLe, Long> {
    @Query("select a from NgayNghiLe a where a.holidayDate = :holidayDate")
    List<NgayNghiLe> getNgayNghiLeByHolidayDate(@Param("holidayDate") LocalDate holidayDate);
}
