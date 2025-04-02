package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.BaoCao;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.repository.SalaryRepository;
import com.mycompany.myapp.service.BaoCaoService;
import com.mycompany.myapp.service.SalaryService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Salary}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BaoCaoResource {

    private final Logger log = LoggerFactory.getLogger(BaoCaoResource.class);

    private static final String ENTITY_NAME = "salary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalaryRepository salaryRepository;
    private BaoCaoService baoCaoService;

    public BaoCaoResource(SalaryRepository salaryRepository, BaoCaoService baoCaoService) {
        this.salaryRepository = salaryRepository;
        this.baoCaoService = baoCaoService;
    }

    /**
     * {@code GET  /Salarys} : get all the Salarys.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Salarys in body.
     */
    @GetMapping("/baocao")
    public ResponseEntity<List<BaoCao>> getAllBaocao(String searchNam, String searchNhom) {
        log.debug("REST request to get a page of Salarys");
        List<BaoCao> baoCaoList = baoCaoService.pageBaoCao(searchNam, searchNhom);
        return ResponseEntity.ok().body(baoCaoList);
    }
}
