package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.repository.DTO.LuongDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class LuongService {

    private final Logger log = LoggerFactory.getLogger(LuongService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private AttendanceRepository attendanceRepository;
    private AttendanceDetailRepository attendanceDetailRepository;
    private DepartmentRepository departmentRepository;
    private SalaryRepository salaryRepository;
    private LuongRepository luongRepository;
    private LuongDetailRepository luongDetailRepository;
    private final NgayNghiLeRepository ngayNghiLeRepository;

    public LuongService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository,
        DepartmentRepository departmentRepository,
        SalaryRepository salaryRepository,
        LuongRepository luongRepository,
        LuongDetailRepository luongDetailRepository,
        NgayNghiLeRepository ngayNghiLeRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
        this.departmentRepository = departmentRepository;
        this.salaryRepository = salaryRepository;
        this.luongRepository = luongRepository;
        this.luongDetailRepository = luongDetailRepository;
        this.ngayNghiLeRepository = ngayNghiLeRepository;
    }

    public Page<Luong> pageLuong(Pageable pageable) {
        Page<Luong> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();

        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            page = this.luongRepository.getAllByLuongByDot(pageable, "1");
        } else if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority))
        ) {
            page = this.luongRepository.getAllByLuongByDot(pageable, "1");
        } else if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            page = this.luongRepository.getAllByLuongByDot(pageable, "1");
        }

        return page;
    }

    public Page<Luong> pageLuongDot2(Pageable pageable) {
        Page<Luong> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();

        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            page = this.luongRepository.getAllByLuongByDot(pageable, "2");
        } else if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority))
        ) {
            page = this.luongRepository.getAllByLuongByDot(pageable, "2");
        } else if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            page = this.luongRepository.getAllByLuongByDot(pageable, "2");
        }
        return page;
    }

    public void importLuong(LuongDTO luongDTO) {
        List<LuongDetail> luongDetailList = luongDTO.getLuongDetails();
        Luong luong = new Luong();
        luong.setYear(luongDTO.getYear());
        luong.setMonth(luongDTO.getMonth());
        luong.setCreateDate(LocalDate.now());
        luong.setName(luongDTO.getNameSalary());
        luong.setDot(luongDTO.getDot());
        luong = luongRepository.save(luong);
        for (LuongDetail detail : luongDetailList) {
            if (detail.getEmployeeCode() != null) {
                String department = employeeRepository.nameDepartmentByCode(detail.getEmployeeCode());
                if (department != null) {
                    detail.setPhongBan(department);
                } else detail.setPhongBan("");
            }
            detail.setLuongId(luong.getId());
            if (detail.getKpi() != null) {
                detail.setKpi(new BigDecimal(detail.getKpi()).setScale(2, RoundingMode.HALF_UP).toString());
            }
        }
        luongDetailRepository.saveAll(luongDetailList);
    }

    public void deleteLuong(Long id) {
        List<LuongDetail> luongDetails = new ArrayList<>();
        luongDetails = this.luongDetailRepository.getLuongDetailByLuongId(id);
        if (luongDetails.size() > 0) {
            this.luongDetailRepository.deleteAllByLuongId(id);
        }
        this.luongRepository.deleteById(id);
    }
}
