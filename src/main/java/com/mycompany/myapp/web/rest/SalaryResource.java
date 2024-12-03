package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.repository.SalaryRepository;
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
public class SalaryResource {

    private final Logger log = LoggerFactory.getLogger(SalaryResource.class);

    private static final String ENTITY_NAME = "salary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalaryRepository salaryRepository;
    private SalaryService salaryService;

    public SalaryResource(SalaryRepository salaryRepository, SalaryService salaryService) {
        this.salaryRepository = salaryRepository;
        this.salaryService = salaryService;
    }

    @PostMapping("/salary")
    public ResponseEntity<Salary> createSalary(@Valid @RequestBody Salary salary) throws URISyntaxException {
        log.debug("REST request to save Salary : {}", salary);
        if (salary.getId() != null) {
            throw new BadRequestAlertException("A new Salary cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Salary result = salaryService.createSalary(salary);
        return ResponseEntity
            .created(new URI("/api/salary/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /Salarys/:id} : Updates an existing Salary.
     *
     * @param id the id of the Salary to save.
     * @param salary the Salary to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated Salary,
     * or with status {@code 400 (Bad Request)} if the Salary is not valid,
     * or with status {@code 500 (Internal Server Error)} if the Salary couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/salary/{id}")
    public ResponseEntity<Salary> updateSalary(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Salary salary
    ) throws URISyntaxException {
        log.debug("REST request to update Salary : {}, {}", id, salary);
        if (salary.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salary.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salaryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Salary result = salaryRepository.save(salary);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salary.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /Salarys} : get all the Salarys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Salarys in body.
     */
    @GetMapping("/salary")
    public ResponseEntity<List<Salary>> getAllSalarys(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Salarys");
        Page<Salary> page = salaryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /Salarys/:id} : get the "id" Salary.
     *
     * @param id the id of the Salary to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the Salary, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/salary/{id}")
    public ResponseEntity<Salary> getSalary(@PathVariable Long id) {
        log.debug("REST request to get Salary : {}", id);
        Optional<Salary> salary = salaryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(salary);
    }

    /**
     * {@code DELETE  /Salarys/:id} : delete the "id" Salary.
     *
     * @param id the id of the Salary to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/salary/{id}")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        log.debug("REST request to delete Salary : {}", id);
        salaryRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
