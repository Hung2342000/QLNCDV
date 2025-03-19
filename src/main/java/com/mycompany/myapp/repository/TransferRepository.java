package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Transfer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Transfer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("select a from Transfer a where a.employeeId =  :employeeId and  a.closeDate = TO_DATE(:closeDate, 'YYYY-MM-DD')")
    Transfer transferByCloseDateAndEmployeeId(@Param("employeeId") Long employeeId, @Param("closeDate") String closeDate);

    @Query("select a from Transfer a where a.employeeId = :employeeId ")
    List<Transfer> transferByEmployeeId(@Param("employeeId") Long employeeId);
}
