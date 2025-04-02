package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BaoCao;
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
public interface BaoCaoRepository extends JpaRepository<BaoCao, Long> {}
