package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.domain.SalaryDetail;
import com.mycompany.myapp.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import liquibase.pro.packaged.B;
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
public class SalaryService {

    private final Logger log = LoggerFactory.getLogger(SalaryService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private SalaryRepository salaryRepository;
    private SalaryDetailRepository salaryDetailRepository;
    private AttendanceRepository attendanceRepository;

    public SalaryService(
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

    public Salary createSalary(Salary salary) {
        Salary salaryCreate = this.salaryRepository.save(salary);
        for (Employee employee : salary.getEmployees()) {
            Employee employeeSelect = employeeRepository.findById(employee.getId()).get();
            SalaryDetail salaryDetail = new SalaryDetail();
            salaryDetail.setEmployeeId(employeeSelect.getId());
            salaryDetail.setSalaryId(salaryCreate.getId());
            //            BigDecimal numberWorking = attendanceRepository.getNumberWorkingByEmployeeId(
            //                employeeSelect.getId(),
            //                salary.getMonth(),
            //                salary.getYear()
            //            );
            //            if (numberWorking != null && employeeSelect.getBasicSalary() != null) {
            //                salaryDetail.setBasicSalary(employeeSelect.getBasicSalary());
            //                salaryDetail.setNumberWorking(numberWorking);
            //                salaryDetail.setNumberWorkInMonth(new BigDecimal(salary.getNumberWork()));
            //                BigDecimal amount = numberWorking
            //                    .multiply(employeeSelect.getBasicSalary())
            //                    .divide(new BigDecimal(salary.getNumberWork()), 10, RoundingMode.HALF_UP)
            //                    .setScale(4, RoundingMode.HALF_UP);
            //                if (amount != null) {
            //                    salaryDetail.setAmount(amount);
            //                }
            //            }
            salaryDetailRepository.save(salaryDetail);
        }

        return salary;
    }

    public void deleteSalary(Long id) {
        List<SalaryDetail> salaryDetailList = new ArrayList<>();
        salaryDetailList = this.salaryDetailRepository.getSalaryDetailBySalaryId(id);
        if (salaryDetailList.size() > 0) {
            this.salaryDetailRepository.deleteAllBySalaryId(id);
        }
        this.salaryRepository.deleteById(id);
    }
}
