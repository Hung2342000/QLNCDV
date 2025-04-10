package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.repository.AttendanceRepository;
import com.mycompany.myapp.service.AttendanceService;
import com.mycompany.myapp.service.PdfService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Attendance}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AttendanceResource {

    private final Logger log = LoggerFactory.getLogger(AttendanceResource.class);

    private static final String ENTITY_NAME = "attendance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttendanceRepository attendanceRepository;
    private AttendanceService attendanceService;
    private PdfService pdfService;

    public AttendanceResource(AttendanceRepository attendanceRepository, AttendanceService attendanceService, PdfService pdfService) {
        this.attendanceRepository = attendanceRepository;
        this.attendanceService = attendanceService;
        this.pdfService = pdfService;
    }

    /**
     * {@code POST  /attendances} : Create a new attendance.
     *
     * @param attendance the attendance to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attendance, or with status {@code 400 (Bad Request)} if the attendance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/attendances")
    public ResponseEntity<Attendance> createAttendance(@Valid @RequestBody Attendance attendance) throws URISyntaxException {
        log.debug("REST request to save Attendance : {}", attendance);
        if (attendance.getId() != null) {
            throw new BadRequestAlertException("A new attendance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Attendance result = attendanceService.createAttendance(attendance);
        return ResponseEntity
            .created(new URI("/api/attendances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /attendances/:id} : Updates an existing attendance.
     *
     * @param id the id of the attendance to save.
     * @param attendance the attendance to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attendance,
     * or with status {@code 400 (Bad Request)} if the attendance is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attendance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/attendances/{id}")
    public ResponseEntity<Attendance> updateAttendance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Attendance attendance
    ) throws URISyntaxException {
        log.debug("REST request to update Attendance : {}, {}", id, attendance);
        if (attendance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attendance.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attendanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Attendance result = attendanceRepository.save(attendance);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attendance.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /attendances} : get all the attendances.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attendances in body.
     */
    @GetMapping("/attendances")
    public ResponseEntity<List<Attendance>> getAllAttendances(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Attendances");
        Page<Attendance> page = attendanceService.getAllByDepartment(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /attendances/:id} : get the "id" attendance.
     *
     * @param id the id of the attendance to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attendance, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/attendances/{id}")
    public ResponseEntity<Attendance> getAttendance(@PathVariable Long id) {
        log.debug("REST request to get Attendance : {}", id);
        Optional<Attendance> attendance = attendanceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(attendance);
    }

    /**
     * {@code DELETE  /attendances/:id} : delete the "id" attendance.
     *
     * @param id the id of the attendance to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/attendances/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        log.debug("REST request to delete Attendance : {}", id);
        attendanceService.deleteAttendance(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/attendances/all")
    public ResponseEntity<List<Attendance>> getAllAttendanceAll() {
        List<Attendance> attendanceList = attendanceService.getAllByDepartmentAll();
        return ResponseEntity.ok().body(attendanceList);
    }

    @GetMapping("/attendances/export")
    public ResponseEntity<byte[]> exportSalary(Long attendanceId) throws IOException {
        byte[] excelBytes = attendanceService.exportAttendance(attendanceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "bangchamcong.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/attendances/export/pdf")
    public ResponseEntity<ByteArrayResource> exportChamCongPdf(Long attendanceId) {
        try {
            ByteArrayResource resource = pdfService.exportChamCongPdf(attendanceId);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bang-cham-cong.pdf");

            return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
