package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Luong;
import com.mycompany.myapp.domain.LuongDetail;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.domain.SalaryDetail;
import com.mycompany.myapp.repository.DTO.LuongDTO;
import com.mycompany.myapp.repository.LuongRepository;
import com.mycompany.myapp.service.LuongService;
import java.net.URISyntaxException;
import java.util.List;
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
public class LuongResource {

    private final Logger log = LoggerFactory.getLogger(LuongResource.class);

    private static final String ENTITY_NAME = "salary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private LuongService luongService;
    private LuongRepository luongRepository;

    public LuongResource(LuongService luongService, LuongRepository luongRepository) {
        this.luongService = luongService;
        this.luongRepository = luongRepository;
    }

    /**
     * {@code GET  /Salarys} : get all the Salarys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Salarys in body.
     */
    @GetMapping("/luong")
    public ResponseEntity<List<Luong>> getAllLuongs(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Salarys");
        Page<Luong> page = luongService.pageLuong(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/luong/all/import")
    public void importAllLuongs(@Valid @RequestBody LuongDTO luongDto) throws URISyntaxException {
        luongService.importLuong(luongDto);
    }

    @GetMapping("/luong/{id}")
    public ResponseEntity<Luong> getLuong(@PathVariable Long id) {
        Optional<Luong> luong = luongRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(luong);
    }

    @DeleteMapping("/luong/{id}")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        log.debug("REST request to delete Salary : {}", id);
        luongService.deleteLuong(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
