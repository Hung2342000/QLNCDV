package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.ServiceType;
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
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    @Query("select a from ServiceType a where a.id <= 10")
    List<ServiceType> findAllCustom();

    @Query("select a from ServiceType a where a.id = :id")
    ServiceType findbyId(@Param("id") Long id);

    @Query("select a from ServiceType a where lower(a.serviceName) = :serviceName and a.region = :region")
    ServiceType findServiceTypeByServiceNameAndRegion(@Param("serviceName") String serviceName, @Param("region") String region);

    @Query(
        "select a from ServiceType a where lower(a.serviceName) = :serviceName and lower(a.region) = :region and lower(a.rank) LIKE %:rank%"
    )
    ServiceType findServiceTypeByServiceNameAndRegionRank(
        @Param("serviceName") String serviceName,
        @Param("region") String region,
        @Param("rank") String rank
    );
}
