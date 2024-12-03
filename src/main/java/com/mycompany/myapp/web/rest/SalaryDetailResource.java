package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.domain.SalaryDetail;
import com.mycompany.myapp.repository.SalaryDetailRepository;
import com.mycompany.myapp.repository.SalaryRepository;
import com.mycompany.myapp.service.SalaryDetailService;
import com.mycompany.myapp.service.SalaryService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
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

/**
 * REST controller for managing {@link Salary}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SalaryDetailResource {

    private final Logger log = LoggerFactory.getLogger(SalaryDetailResource.class);

    private static final String ENTITY_NAME = "salary_detail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalaryRepository salaryRepository;
    private final SalaryDetailRepository salaryDetailRepository;
    private final SalaryDetailService salaryDetailService;

    public SalaryDetailResource(
        SalaryRepository salaryRepository,
        SalaryDetailRepository salaryDetailRepository,
        SalaryService salaryService,
        SalaryDetailService salaryDetailService
    ) {
        this.salaryRepository = salaryRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.salaryDetailService = salaryDetailService;
    }

    @PostMapping("/salary-detail")
    public ResponseEntity<SalaryDetail> createSalaryDetail(@Valid @RequestBody SalaryDetail salaryDetail) throws URISyntaxException {
        log.debug("REST request to save Salary : {}", salaryDetail);
        if (salaryDetail.getId() != null) {
            throw new BadRequestAlertException("A new Salary cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SalaryDetail result = salaryDetailRepository.save(salaryDetail);
        return ResponseEntity
            .created(new URI("/api/salary-detail/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /salary-detail/:id} : Updates an existing Salary.
     *
     * @param id the id of the Salary to save.
     * @param salaryDetail the Salary to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated Salary,
     * or with status {@code 400 (Bad Request)} if the Salary is not valid,
     * or with status {@code 500 (Internal Server Error)} if the Salary couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/salary-detail/{id}")
    public ResponseEntity<SalaryDetail> updateSalary(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalaryDetail salaryDetail
    ) throws URISyntaxException {
        log.debug("REST request to update Salary : {}, {}", id, salaryDetail);
        if (salaryDetail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salaryDetail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salaryDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalaryDetail result = salaryDetailService.updateSalaryDetail(salaryDetail);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salaryDetail.getId().toString()))
            .body(result);
    }

    @GetMapping("/salary-detail")
    public ResponseEntity<List<SalaryDetail>> getAllSalaryDetail(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        Long idSalary
    ) {
        log.debug("REST request to get a page of SalaryDetail");

        Page<SalaryDetail> page = salaryDetailRepository.getAllBySalaryId(pageable, idSalary);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /Salarys/:id} : get the "id" Salary.
     *
     * @param id the id of the Salary to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the Salary, or with status {@code 404 (Not Found)}.
     */
    //    @GetMapping("/salary-detail/{id}")
    //    public ResponseEntity<SalaryDetail> getSalaryDetail(@PathVariable Long id) {
    //        log.debug("REST request to get Salary : {}", id);
    //        Optional<SalaryDetail> salaryDetail = salaryDetailRepository.findById(id);
    //        return ResponseUtil.wrapOrNotFound(salaryDetail);
    //    }

    /**
     * {@code DELETE  /Salarys/:id} : delete the "id" Salary.
     *
     * @param id the id of the Salary to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/salary-detail/{id}")
    public ResponseEntity<Void> deleteSalaryDetail(@PathVariable Long id) {
        log.debug("REST request to delete SalaryDetail : {}", id);
        salaryDetailRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
