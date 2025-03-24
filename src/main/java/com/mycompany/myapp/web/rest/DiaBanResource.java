package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.DiaBan;
import com.mycompany.myapp.repository.DiaBanRepository;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Transactional
public class DiaBanResource {

    private final DiaBanRepository diaBanRepository;

    public DiaBanResource(DiaBanRepository diaBanRepository) {
        this.diaBanRepository = diaBanRepository;
    }

    @GetMapping("/diaban/all")
    public ResponseEntity<List<DiaBan>> getAllDeparment() throws URISyntaxException {
        List<DiaBan> list = diaBanRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
}
