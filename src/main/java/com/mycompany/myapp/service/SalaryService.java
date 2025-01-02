package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
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
                salaryCreate.setMonth(attendance.getMonth());
                salaryCreate.setYear(attendance.getYear());
                attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(salary.getAttendanceId(), employeeSelect.getId());
                if (attendanceDetail.getId() != null) {
                    if (
                        attendanceDetail.getNumberWork() != null &&
                        attendanceDetail.getPaidWorking() != null &&
                        employeeSelect.getBasicSalary() != null
                    ) {
                        salaryDetail.setNumberWorkInMonth(attendanceDetail.getNumberWork());
                        salaryDetail.setNumberWorking(attendanceDetail.getPaidWorking());
                        salaryCreate.setNumberWork(attendanceDetail.getNumberWork());
                    }
                }
            } else {
                if (salary.getYear() != null && salary.getMonth() != null) {
                    int numberWorking = countWorkingInMonth(salary.getYear().intValue(), salary.getMonth().intValue());
                    salaryDetail.setNumberWorking(BigDecimal.valueOf(numberWorking));
                    salaryDetail.setNumberWorkInMonth(BigDecimal.valueOf(numberWorking));
                }
            }
            BigDecimal salaryAmount = BigDecimal.ZERO;

            if (
                salaryDetail.getNumberWorking() != null &&
                salaryDetail.getNumberWorkInMonth() != null &&
                employeeSelect.getBasicSalary() != null
            ) {
                salaryAmount =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                        .multiply(employeeSelect.getBasicSalary());
            }
            salaryDetail.setDonGiaDichVuThucNhan(salaryAmount);
            salaryDetail.setVung(employeeSelect.getRegion());
            salaryDetail.setMucChiToiThieu(employeeSelect.getMucChiTraToiThieu());
            salaryDetail.setChucDanh(serviceTypeName(employeeSelect.getServiceType()));
            salaryDetail.setDonGiaDichVu(employeeSelect.getBasicSalary());
            salaryDetailRepository.save(salaryDetail);
        }
        salaryRepository.save(salaryCreate);
        return salary;
    }

    public static int countWorkingInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalSundays = 0;

        // Duyệt qua từng ngày trong tháng
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            if (date.getDayOfWeek().getValue() == 7 || date.getDayOfWeek().getValue() == 6) {
                totalSundays++;
            }
        }
        return yearMonth.lengthOfMonth() - totalSundays;
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
