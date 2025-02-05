package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
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
    private ServiceTypeRepository serviceTypeRepository;

    public EmployeeService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        CountEmployeeRepository countEmployeeRepository,
        DepartmentRepository departmentRepository,
        ServiceTypeRepository serviceTypeRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.countEmployeeRepository = countEmployeeRepository;
        this.departmentRepository = departmentRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public Page<Employee> getAllEmployees(
        Pageable pageable,
        String searchCode,
        String searchName,
        String searchDepartment,
        String searchNhom
    ) {
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
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            page =
                employeeRepository.listAllEmployees(
                    searchCode.toLowerCase(),
                    searchName.toLowerCase(),
                    searchDepartment,
                    searchNhom,
                    pageable
                );
        } else if (
            authentication != null && !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            page =
                employeeRepository.listAllEmployeesDepartment(
                    searchCode.toLowerCase(),
                    searchName.toLowerCase(),
                    user.getDepartment(),
                    searchNhom,
                    pageable
                );
        }
        return page;
    }

    public Employee saveEmployee(Employee employee) {
        ServiceType serviceType = new ServiceType();
        if (employee.getServiceType() != null) {
            serviceType = serviceTypeRepository.findById(employee.getServiceType()).get();
        }
        if (serviceType != null) {
            if (serviceType.getMucChiTraToiThieu() != null) {
                employee.setMucChiTraToiThieu(serviceType.getMucChiTraToiThieu());
            }
            if (serviceType.getBasicSalary() != null) {
                employee.setBasicSalary(serviceType.getBasicSalary());
            }
            if (serviceType.getRank() != null) {
                employee.setRank(serviceType.getRank());
            }
            if (serviceType.getRegion() != null) {
                employee.setRegion(serviceType.getRegion());
            }
            if (serviceType.getNhom() != null) {
                employee.setNhom(serviceType.getNhom());
            }
        }
        employeeRepository.save(employee);
        return employee;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
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
        for (CountEmployee countEmployee : employeeList) {
            if (countEmployee.getCode().equals(user.getDepartmentName())) {
                countEmployee.setDepartment(true);
                break;
            }
        }
        return employeeList;
    }

    public List<Employee> importEmployee(List<Employee> employeeList) {
        List<Employee> employees = new ArrayList<>();
        if (employeeList.size() > 0) {
            for (Employee employee : employeeList) {
                ServiceType serviceType = new ServiceType();
                Department department = new Department();
                if (employee.getDepartment() != null) {
                    department = departmentRepository.findDepartmentByName(employee.getDepartment().toLowerCase());
                }
                if (department != null && department.getId() != null) {
                    employee.setDepartment(department.getCode());
                } else {
                    employee.setDepartment(" ");
                }
                if (
                    employee.getRank() != null &&
                    employee.getRank() != "" &&
                    employee.getServiceTypeName() != null &&
                    employee.getRegion() != null
                ) {
                    serviceType =
                        serviceTypeRepository.findServiceTypeByServiceNameAndRegionRank(
                            employee.getServiceTypeName().toLowerCase(),
                            employee.getRegion(),
                            employee.getRank()
                        );
                } else if (employee.getServiceTypeName() != null && employee.getRegion() != null) {
                    serviceType =
                        serviceTypeRepository.findServiceTypeByServiceNameAndRegion(
                            employee.getServiceTypeName().toLowerCase(),
                            employee.getRegion()
                        );
                }
                if (serviceType != null && serviceType.getId() != null) {
                    employee.setNhom(serviceType.getNhom());
                    employee.setRegion(serviceType.getRegion());
                    employee.setServiceType(serviceType.getId());
                    employee.setRank(serviceType.getRank());
                    employee.setBasicSalary(serviceType.getBasicSalary());
                    employee.setMucChiTraToiThieu(serviceType.getMucChiTraToiThieu());
                } else {
                    // check null khi tim kiem
                    employee.setNhom(" ");
                }
                Employee employeeCheck = new Employee();
                employeeCheck = employeeRepository.getByCode(employee.getCodeEmployee());
                if (employeeCheck == null) {
                    employees.add(employee);
                }
            }
        }
        employeeRepository.saveAll(employees);
        return employees;
    }
}
