package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.DiaBan;
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
public interface DiaBanRepository extends JpaRepository<DiaBan, Long> {
    @Query("select a from DiaBan a where a.name like %:name% ")
    DiaBan findDiaBanByName(@Param("name") String name);
}
