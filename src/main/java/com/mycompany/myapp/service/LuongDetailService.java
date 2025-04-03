package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.LuongDetail;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class LuongDetailService {

    private final Logger log = LoggerFactory.getLogger(LuongDetailService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private LuongDetailRepository luongDetailRepository;
    private AttendanceRepository attendanceRepository;
    private AttendanceDetailRepository attendanceDetailRepository;
    private DepartmentRepository departmentRepository;
    private SalaryRepository salaryRepository;
    private final NgayNghiLeRepository ngayNghiLeRepository;

    public LuongDetailService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        LuongDetailRepository luongDetailRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository,
        DepartmentRepository departmentRepository,
        SalaryRepository salaryRepository,
        NgayNghiLeRepository ngayNghiLeRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.luongDetailRepository = luongDetailRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
        this.departmentRepository = departmentRepository;
        this.salaryRepository = salaryRepository;
        this.ngayNghiLeRepository = ngayNghiLeRepository;
    }

    public List<LuongDetail> listLuongDetail(Long id) {
        List<LuongDetail> listLuong = new ArrayList<>();

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
            listLuong = this.luongDetailRepository.getLuongDetailByLuongId(id);
        } else if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority))
        ) {
            listLuong = this.luongDetailRepository.getLuongDetailByLuongIdAndEmployeeId(id, user.getCode());
        } else if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            listLuong = this.luongDetailRepository.getLuongDetailByLuongIdAndDepartMent(id, user.getDepartmentName());
        }

        return listLuong;
    }
}
