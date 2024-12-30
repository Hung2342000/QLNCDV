package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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
    private AttendanceDetailRepository attendanceDetailRepository;
    private ServiceTypeRepository serviceTypeRepository;

    public SalaryService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        SalaryRepository salaryRepository,
        SalaryDetailRepository salaryDetailRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository,
        ServiceTypeRepository serviceTypeRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public Salary createSalary(Salary salary) {
        Salary salaryCreate = this.salaryRepository.save(salary);
        Attendance attendance = new Attendance();
        if (salary.getAttendanceId() != null && salary.getIsAttendance()) {
            attendance = this.attendanceRepository.findById(salary.getAttendanceId()).get();
        }

        for (Employee employee : salary.getEmployees()) {
            Employee employeeSelect = employeeRepository.findById(employee.getId()).get();
            SalaryDetail salaryDetail = new SalaryDetail();
            salaryDetail.setEmployeeId(employeeSelect.getId());
            salaryDetail.setSalaryId(salaryCreate.getId());
            AttendanceDetail attendanceDetail = new AttendanceDetail();
            if (salary.getAttendanceId() != null) {
                attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(salary.getAttendanceId(), employeeSelect.getId());
            } else {
                ////                attendance = this.attendanceRepository.getAttendanceByMonthEndYear(salary.getMonth(), salary.getYear());
                //                if (attendance.getId() != null) {
                //                    attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(attendance.getId(), employeeSelect.getId());
                //                }

            }
            BigDecimal salaryAmount = BigDecimal.ZERO;
            if (attendanceDetail.getId() != null) {
                salaryDetail.setNumberWorkInMonth(attendanceDetail.getPaidWorking());
                if (
                    salary.getNumberWork() != null && attendanceDetail.getPaidWorking() != null && employeeSelect.getBasicSalary() != null
                ) {
                    salaryAmount =
                        attendanceDetail
                            .getPaidWorking()
                            .divide(salary.getNumberWork(), 5, RoundingMode.HALF_UP)
                            .multiply(employeeSelect.getBasicSalary());
                } else if (
                    attendanceDetail.getNumberWork() != null &&
                    attendanceDetail.getPaidWorking() != null &&
                    employeeSelect.getBasicSalary() != null
                ) {
                    salaryCreate.setMonth(attendance.getMonth());
                    salaryCreate.setYear(attendance.getYear());
                    salaryCreate.setNumberWork(attendanceDetail.getNumberWork());
                    salaryAmount =
                        attendanceDetail
                            .getPaidWorking()
                            .divide(attendanceDetail.getNumberWork(), 5, RoundingMode.HALF_UP)
                            .multiply(employeeSelect.getBasicSalary());
                }
            }
            salaryDetail.setDonGiaDichVuThucNhan(salaryAmount);
            salaryDetail.setChucDanh(serviceTypeName(employeeSelect.getServiceType()));
            salaryDetail.setDonGiaDichVu(employeeSelect.getBasicSalary());
            salaryDetail.setNumberWorking(attendanceDetail.getNumberWork());
            salaryDetailRepository.save(salaryDetail);
        }
        salaryRepository.save(salaryCreate);

        return salary;
    }

    public String serviceTypeName(Long id) {
        ServiceType serviceType = serviceTypeRepository.findById(id).get();
        if (serviceType == null) {
            return "";
        }
        return serviceType.getServiceName();
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
