package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BaoCao;
import com.mycompany.myapp.domain.Luong;
import com.mycompany.myapp.domain.LuongDetail;
import com.mycompany.myapp.domain.SalaryDetail;
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
public interface LuongRepository extends JpaRepository<Luong, Long> {}
