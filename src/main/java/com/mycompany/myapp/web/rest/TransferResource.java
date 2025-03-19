package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Transfer;
import com.mycompany.myapp.repository.CountEmployeeRepository;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.service.EmployeeService;
import com.mycompany.myapp.service.TransferService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link Employee}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TransferResource {

    private final Logger log = LoggerFactory.getLogger(TransferResource.class);

    private static final String ENTITY_NAME = "transfer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final TransferService transferService;

    public TransferResource(
        EmployeeRepository employeeRepository,
        DepartmentRepository departmentRepository,
        CountEmployeeRepository countEmployeeRepository,
        EmployeeService employeeService,
        TransferService transferService
    ) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transfer> createEmployee(@Valid @RequestBody Transfer transfer) throws URISyntaxException {
        log.debug("REST request to save Transfer : {}", transfer);
        if (transfer.getId() != null) {
            throw new BadRequestAlertException("A new employee cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Transfer result = transferService.saveTransfer(transfer);
        return ResponseEntity
            .created(new URI("/api/transfer/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
    //    @PutMapping("/employees/{id}")
    //    public ResponseEntity<Employee> updateEmployee(
    //        @PathVariable(value = "id", required = false) final Long id,
    //        @Valid @RequestBody Employee employee
    //    ) throws URISyntaxException {
    //        log.debug("REST request to update Employee : {}, {}", id, employee);
    //        if (employee.getId() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(id, employee.getId())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!employeeRepository.existsById(id)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //        Employee result = employeeService.saveEmployee(employee);
    //        return ResponseEntity
    //            .ok()
    //            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employee.getId().toString()))
    //            .body(result);
    //    }
}
