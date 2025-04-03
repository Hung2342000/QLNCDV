package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BaoCao;
import com.mycompany.myapp.domain.LuongDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Tool entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LuongDetailRepository extends JpaRepository<LuongDetail, Long> {
    @Query("select a from LuongDetail a where a.luongId = :luongId AND a.nhom = 'NVBH' ")
    List<LuongDetail> getAllByLuongIdNVBH(@Param("luongId") Long luongId);

    @Query("select a from LuongDetail a where a.luongId = :luongId ")
    List<LuongDetail> getLuongDetailByLuongId(@Param("luongId") Long luongId);

    @Query("select a from LuongDetail a where a.luongId = :luongId and a.employeeCode = :code")
    List<LuongDetail> getLuongDetailByLuongIdAndEmployeeId(@Param("luongId") Long luongId, @Param("code") String code);

    @Query("select a from LuongDetail a where a.luongId = :luongId and a.phongBan = :phongBan")
    List<LuongDetail> getLuongDetailByLuongIdAndDepartMent(@Param("luongId") Long luongId, @Param("phongBan") String phongBan);

    void deleteAllByLuongId(@Param("id") Long id);
}
