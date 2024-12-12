package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.CountEmployee;
import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.CountEmployeeRepository;
import com.mycompany.myapp.repository.DepartmentRepository;
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
public class EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private CountEmployeeRepository countEmployeeRepository;
    private DepartmentRepository departmentRepository;

    public EmployeeService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        CountEmployeeRepository countEmployeeRepository,
        DepartmentRepository departmentRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.countEmployeeRepository = countEmployeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public Page<Employee> getAllEmployees(Pageable pageable, String searchCode, String searchName, String searchDepartment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        Page<Employee> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            page = employeeRepository.listAllEmployees(searchCode.toLowerCase(), searchName.toLowerCase(), searchDepartment, pageable);
        } else if (
            authentication != null && !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            page =
                employeeRepository.listAllEmployeesDepartment(
                    searchCode.toLowerCase(),
                    searchName.toLowerCase(),
                    user.getDepartment(),
                    pageable
                );
        }
        return page;
    }

    public List<Employee> getAllEmployeeNoPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        List<Employee> employeeList = new ArrayList<>();
        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            employeeList = employeeRepository.findAll();
        } else if (
            authentication != null && !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            employeeList = employeeRepository.listAllEmployeesDepartmentNoPage(user.getDepartment());
        }
        return employeeList;
    }

    public List<CountEmployee> getAllCountEmployee() {
        List<CountEmployee> employeeList = countEmployeeRepository.listAllCountEmployee();
        List<Department> listDepartment = new ArrayList<>();
        listDepartment = this.departmentRepository.findAll();
        if (employeeList.size() > 0) {
            for (Department dp : listDepartment) {
                if (!employeeList.stream().anyMatch(em -> em.getCode().equals(dp.getName()))) {
                    CountEmployee countEmployee = new CountEmployee();
                    countEmployee.setCountEmployee((long) 0);
                    countEmployee.setCode(dp.getName());
                    employeeList.add(countEmployee);
                }
            }
        }
        return employeeList;
    }
}
