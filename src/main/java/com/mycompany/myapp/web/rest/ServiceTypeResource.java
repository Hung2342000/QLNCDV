package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ServiceType;
import com.mycompany.myapp.repository.ServiceTypeRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link ServiceType}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ServiceTypeResource {

    private ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeResource(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @GetMapping("/serviceType/all")
    public ResponseEntity<List<ServiceType>> getAllServiceType() {
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        return ResponseEntity.ok().body(serviceTypes);
    }

    @GetMapping("/serviceType/all/custom")
    public ResponseEntity<List<ServiceType>> getAllCustomServiceType() {
        List<ServiceType> serviceTypes = serviceTypeRepository.findAllCustom();
        return ResponseEntity.ok().body(serviceTypes);
    }
}
