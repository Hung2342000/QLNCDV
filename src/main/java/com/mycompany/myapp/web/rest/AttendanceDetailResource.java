package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.domain.AttendanceDetail;
import com.mycompany.myapp.repository.AttendanceDetailRepository;
import com.mycompany.myapp.repository.AttendanceRepository;
import com.mycompany.myapp.service.AttendanceDetailService;
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
 * REST controller for managing {@link Attendance}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AttendanceDetailResource {

    private final Logger log = LoggerFactory.getLogger(AttendanceDetailResource.class);

    private static final String ENTITY_NAME = "attendanceDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttendanceRepository attendanceRepository;
    private AttendanceDetailRepository attendanceDetailRepository;
    private AttendanceDetailService attendanceDetailService;

    public AttendanceDetailResource(
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository,
        AttendanceDetailService attendanceDetailService
    ) {
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
        this.attendanceDetailService = attendanceDetailService;
    }

    @PostMapping("/attendanceDetail")
    public ResponseEntity<AttendanceDetail> createAttendance(@Valid @RequestBody AttendanceDetail attendanceDetail)
        throws URISyntaxException {
        log.debug("REST request to save Attendance : {}", attendanceDetail);
        if (attendanceDetail.getId() != null) {
            throw new BadRequestAlertException("A new attendance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AttendanceDetail result = attendanceDetailService.createAttendanceDetail(attendanceDetail);
        return ResponseEntity
            .created(new URI("/api/attendanceDetail/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/attendanceDetail/{id}")
    public ResponseEntity<AttendanceDetail> updateAttendance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AttendanceDetail attendanceDetail
    ) throws URISyntaxException {
        log.debug("REST request to update Attendance : {}, {}", id, attendanceDetail);
        if (attendanceDetail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attendanceDetail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attendanceDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AttendanceDetail result = attendanceDetailService.createAttendanceDetail(attendanceDetail);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attendanceDetail.getId().toString()))
            .body(result);
    }

    @GetMapping("/attendanceDetail")
    public ResponseEntity<List<AttendanceDetail>> getAllAttendanceDetail(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Attendances");
        Page<AttendanceDetail> page = attendanceDetailRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/attendanceDetail/{id}")
    public ResponseEntity<List<AttendanceDetail>> getAllAttendanceDetailAll(@PathVariable Long id) {
        List<AttendanceDetail> attendancDetailList = attendanceDetailRepository.findAttendanceDetailByAttendanceId(id);
        return ResponseEntity.ok().body(attendancDetailList);
    }

    //    @GetMapping("/attendanceDetail/{id}")
    //    public ResponseEntity<AttendanceDetail> getAttendance(@PathVariable Long id) {
    //        log.debug("REST request to get Attendance : {}", id);
    //        Optional<AttendanceDetail> attendance = attendanceDetailRepository.findById(id);
    //        return ResponseUtil.wrapOrNotFound(attendance);
    //    }

    @DeleteMapping("/attendanceDetail/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        log.debug("REST request to delete Attendance : {}", id);
        attendanceDetailService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/attendanceDetail/all")
    public void createAttendanceDetailAll(@Valid @RequestBody List<AttendanceDetail> attendanceDetail) throws URISyntaxException {
        attendanceDetailService.createAttendanceDetailAll(attendanceDetail);
        //AttendanceDetail result = attendanceDetailService.createAttendanceDetail(attendanceDetail);
    }
}
