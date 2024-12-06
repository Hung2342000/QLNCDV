package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.AttendanceDetailRepository;
import com.mycompany.myapp.repository.AttendanceRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class AttendanceService {

    private final Logger log = LoggerFactory.getLogger(AttendanceService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private AttendanceRepository attendanceRepository;
    private AttendanceDetailRepository attendanceDetailRepository;

    public AttendanceService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
    }

    public Attendance createAttendance(Attendance attendance) {
        attendance.setCount((long) 0);
        attendance.setCountNot((long) 0);
        if (attendance.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(attendance.getEmployeeId()).get();
            attendance.setDepartment(employee.getDepartment());
        }
        Attendance attendanceCreate = this.attendanceRepository.save(attendance);
        return attendanceCreate;
    }

    public Page<Attendance> getAllByDepartment(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        Page<Attendance> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            page = this.attendanceRepository.findAll(pageable);
        } else if (
            authentication != null && !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            page = this.attendanceRepository.getAllAttendanceByDepartment(user.getDepartment(), pageable);
        }
        return page;
    }

    public void deleteAttendance(Long id) {
        List<AttendanceDetail> list = new ArrayList<>();
        list = this.attendanceDetailRepository.selectAllByAttId(id);
        if (list != null && list.size() > 0) {
            this.attendanceDetailRepository.deleteAllByAttendanceId(id);
        }
        this.attendanceRepository.deleteById(id);
    }
}
