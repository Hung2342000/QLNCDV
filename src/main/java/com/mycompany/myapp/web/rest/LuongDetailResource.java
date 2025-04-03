package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.LuongDetail;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.repository.LuongDetailRepository;
import com.mycompany.myapp.repository.LuongRepository;
import com.mycompany.myapp.service.LuongDetailService;
import com.mycompany.myapp.service.LuongService;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Salary}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LuongDetailResource {

    private final Logger log = LoggerFactory.getLogger(LuongDetailResource.class);

    private static final String ENTITY_NAME = "salary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private LuongService luongService;
    private LuongDetailService luongDetailService;
    private LuongRepository luongRepository;
    private LuongDetailRepository luongDetailRepository;

    public LuongDetailResource(
        LuongService luongService,
        LuongDetailService luongDetailService,
        LuongRepository luongRepository,
        LuongDetailRepository luongDetailRepository
    ) {
        this.luongService = luongService;
        this.luongDetailService = luongDetailService;
        this.luongRepository = luongRepository;
        this.luongDetailRepository = luongDetailRepository;
    }

    @GetMapping("/luọng-detail/all/{id}")
    public ResponseEntity<List<LuongDetail>> getAllLuongDetail(@PathVariable Long id) throws IOException {
        List<LuongDetail> luongDetailList = luongDetailService.listLuongDetail(id);
        return ResponseEntity.ok().body(luongDetailList);
    }
}
