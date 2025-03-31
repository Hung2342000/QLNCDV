package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.domain.NgayNghiLe;
import com.mycompany.myapp.repository.NgayNghiLeRepository;
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

@RestController
@RequestMapping("/api")
@Transactional
public class NgayNghiLeResource {

    private final Logger log = LoggerFactory.getLogger(AttendanceResource.class);

    private static final String ENTITY_NAME = "ngayNghiLe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NgayNghiLeRepository ngayNghiLeRepository;

    public NgayNghiLeResource(NgayNghiLeRepository ngayNghiLeRepository) {
        this.ngayNghiLeRepository = ngayNghiLeRepository;
    }

    @PostMapping("/ngay-nghi-le")
    public ResponseEntity<NgayNghiLe> createNgayNghiLe(@Valid @RequestBody NgayNghiLe ngayNghiLe) throws URISyntaxException {
        NgayNghiLe result = ngayNghiLeRepository.save(ngayNghiLe);
        return ResponseEntity
            .created(new URI("/api/ngay-nghi-le/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/ngay-nghi-le/{id}")
    public ResponseEntity<NgayNghiLe> updateNgayNghiLe(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NgayNghiLe ngayNghiLe
    ) throws URISyntaxException {
        if (ngayNghiLe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ngayNghiLe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ngayNghiLeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NgayNghiLe result = ngayNghiLeRepository.save(ngayNghiLe);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ngayNghiLe.getId().toString()))
            .body(result);
    }

    @GetMapping("/ngay-nghi-le")
    public ResponseEntity<List<NgayNghiLe>> getAllNgayNghile(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Page<NgayNghiLe> page = ngayNghiLeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/ngay-nghi-le/{id}")
    public ResponseEntity<NgayNghiLe> getNgayNghiLe(@PathVariable Long id) {
        Optional<NgayNghiLe> ngayNghiLe = ngayNghiLeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ngayNghiLe);
    }

    @DeleteMapping("/ngay-nghi-le/{id}")
    public ResponseEntity<Void> deleteNgayNghiLe(@PathVariable Long id) {
        ngayNghiLeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
