package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.domain.SalaryDetail;
import com.mycompany.myapp.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class SalaryDetailService {

    private final Logger log = LoggerFactory.getLogger(SalaryDetailService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private SalaryRepository salaryRepository;
    private SalaryDetailRepository salaryDetailRepository;
    private AttendanceRepository attendanceRepository;

    public SalaryDetailService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        SalaryRepository salaryRepository,
        SalaryDetailRepository salaryDetailRepository,
        AttendanceRepository attendanceRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public SalaryDetail updateSalaryDetail(SalaryDetail salaryDetail) {
        BigDecimal amount = salaryDetail
            .getBasicSalary()
            .multiply(salaryDetail.getNumberWorking())
            .divide(salaryDetail.getNumberWorkInMonth(), 2, RoundingMode.HALF_UP);
        if (salaryDetail.getAllowance() != null) {
            amount = amount.add(salaryDetail.getAllowance());
        }
        if (salaryDetail.getIncentiveSalary() != null) {
            amount = amount.add(salaryDetail.getIncentiveSalary());
        }
        salaryDetail.setAmount(amount);
        this.salaryDetailRepository.save(salaryDetail);
        return salaryDetail;
    }
}
