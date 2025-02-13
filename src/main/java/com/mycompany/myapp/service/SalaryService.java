package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private DepartmentRepository departmentRepository;

    public SalaryService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        SalaryRepository salaryRepository,
        SalaryDetailRepository salaryDetailRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository,
        ServiceTypeRepository serviceTypeRepository,
        DepartmentRepository departmentRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.departmentRepository = departmentRepository;
    }

    public Salary createSalary(Salary salary) {
        Salary salaryCreate = this.salaryRepository.save(salary);
        Attendance attendance = new Attendance();
        List<Employee> employeeList = new ArrayList<>();
        if (salary.getAttendanceId() != null && salary.getIsAttendance()) {
            attendance = this.attendanceRepository.findById(salary.getAttendanceId()).get();
            employeeList = attendance.getEmployees();
        } else {
            employeeList =
                employeeRepository.listAllEmployees(salary.getSearchName(), salary.getSearchDepartment(), salary.getSearchNhom());
        }
        List<Employee> employeeListAm = new ArrayList<>();
        List<Employee> employeeListHTVP = new ArrayList<>();
        List<Employee> employeeListGDV = new ArrayList<>();
        List<Employee> employeeListKAM = new ArrayList<>();
        List<Employee> employeeListNVBH = new ArrayList<>();
        if (employeeList.size() > 0) {
            employeeListAm = employeeList.stream().filter(employee -> employee.getNhom().equals("AM")).collect(Collectors.toList());
            employeeListHTVP = employeeList.stream().filter(employee -> employee.getNhom().equals("HTVP")).collect(Collectors.toList());
            employeeListGDV = employeeList.stream().filter(employee -> employee.getNhom().equals("GDV")).collect(Collectors.toList());
            employeeListKAM = employeeList.stream().filter(employee -> employee.getNhom().equals("KAM")).collect(Collectors.toList());
            employeeListNVBH = employeeList.stream().filter(employee -> employee.getNhom().equals("NVBH")).collect(Collectors.toList());
        }

        List<SalaryDetail> salaryDetailListHTVP = new ArrayList<>();
        for (Employee employee : employeeListHTVP) {
            SalaryDetail salaryDetail = new SalaryDetail();
            salaryDetail.setEmployeeId(employee.getId());
            salaryDetail.setSalaryId(salaryCreate.getId());
            Department department = departmentRepository.findDepartmentByCode(employee.getDepartment());
            if (department != null) {
                salaryDetail.setTenDonVi(department.getName());
            }
            salaryDetail.setDichVu(employee.getServiceTypeName() + " Vùng " + employee.getRegion());
            AttendanceDetail attendanceDetail = new AttendanceDetail();
            if (salary.getAttendanceId() != null) {
                salaryCreate.setMonth(attendance.getMonth());
                salaryCreate.setYear(attendance.getYear());
                attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(salary.getAttendanceId(), employee.getId());
                if (attendanceDetail.getId() != null) {
                    if (
                        attendanceDetail.getNumberWork() != null &&
                        attendanceDetail.getPaidWorking() != null &&
                        employee.getBasicSalary() != null
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
                    salaryCreate.setNumberWork(BigDecimal.valueOf(numberWorking));
                }
            }
            BigDecimal salaryAmount = BigDecimal.ZERO;

            if (
                salaryDetail.getNumberWorking() != null && salaryDetail.getNumberWorkInMonth() != null && employee.getBasicSalary() != null
            ) {
                salaryAmount =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                        .multiply(employee.getBasicSalary())
                        .setScale(0, RoundingMode.HALF_UP);
            }
            salaryDetail.setDonGiaDichVuThucNhan(salaryAmount);
            salaryDetail.setVung(employee.getRegion());
            salaryDetail.setMucChiToiThieu(employee.getMucChiTraToiThieu());
            salaryDetail.setChucDanh(serviceTypeName(employee.getServiceType()));
            salaryDetail.setDonGiaDichVu(employee.getBasicSalary());
            salaryDetail.setNhom(employee.getNhom());
            salaryDetailListHTVP.add(salaryDetail);
            //            salaryDetailRepository.save(salaryDetail);
        }

        List<SalaryDetail> salaryDetailListAM = new ArrayList<>();
        for (Employee employee : employeeListAm) {
            SalaryDetail salaryDetail = new SalaryDetail();
            salaryDetail.setEmployeeId(employee.getId());
            salaryDetail.setSalaryId(salaryCreate.getId());
            Department department = departmentRepository.findDepartmentByCode(employee.getDepartment());
            if (department != null) {
                salaryDetail.setTenDonVi(department.getName());
            }
            salaryDetail.setDichVu(employee.getServiceTypeName() + " Vùng " + employee.getRegion());
            AttendanceDetail attendanceDetail = new AttendanceDetail();
            if (salary.getAttendanceId() != null) {
                salaryCreate.setMonth(attendance.getMonth());
                salaryCreate.setYear(attendance.getYear());
                attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(salary.getAttendanceId(), employee.getId());
                if (attendanceDetail.getId() != null) {
                    if (
                        attendanceDetail.getNumberWork() != null &&
                        attendanceDetail.getPaidWorking() != null &&
                        employee.getBasicSalary() != null
                    ) {
                        salaryDetail.setNumberWorkInMonth(attendanceDetail.getNumberWork());
                        salaryDetail.setNumberWorking(attendanceDetail.getPaidWorking());
                        salaryCreate.setNumberWork(attendanceDetail.getNumberWork());
                    }
                }
            } else {
                if (salary.getNumberWork() != null) {
                    salaryDetail.setNumberWorking(salary.getNumberWork());
                    salaryDetail.setNumberWorkInMonth(salary.getNumberWork());
                } else if (salary.getYear() != null && salary.getMonth() != null) {
                    int numberWorking = countWorkingInMonth(salary.getYear().intValue(), salary.getMonth().intValue());
                    salaryDetail.setNumberWorking(BigDecimal.valueOf(numberWorking));
                    salaryDetail.setNumberWorkInMonth(BigDecimal.valueOf(numberWorking));
                }
            }
            BigDecimal salaryAmount = BigDecimal.ZERO;
            BigDecimal luongCoDinhThucTe = BigDecimal.ZERO;

            if (
                salaryDetail.getNumberWorking() != null && salaryDetail.getNumberWorkInMonth() != null && employee.getBasicSalary() != null
            ) {
                salaryAmount =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                        .multiply(employee.getBasicSalary())
                        .setScale(0, RoundingMode.HALF_UP);
                luongCoDinhThucTe =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                        .multiply(employee.getMucChiTraToiThieu())
                        .setScale(0, RoundingMode.HALF_UP);
            }
            salaryDetail.setPhiCoDinhDaThucHien(salaryAmount);
            salaryDetail.setChiPhiThueDichVu(salaryAmount);
            salaryDetail.setKpis("1");
            salaryDetail.setChiPhiDichVuKhoanVaKK(BigDecimal.ZERO);
            salaryDetail.setChiPhiKKKhac(BigDecimal.ZERO);
            salaryDetail.setTongChiPhiKVKK(BigDecimal.ZERO);
            salaryDetail.setVung(employee.getRegion());
            salaryDetail.setMucChiToiThieu(employee.getMucChiTraToiThieu());
            salaryDetail.setLuongCoDinhThucTe(luongCoDinhThucTe);
            salaryDetail.setChucDanh(serviceTypeName(employee.getServiceType()));
            salaryDetail.setDonGiaDichVu(employee.getBasicSalary());
            salaryDetail.setNhom(employee.getNhom());
            salaryDetailListAM.add(salaryDetail);
            //            salaryDetailRepository.save(salaryDetail);
        }

        List<SalaryDetail> salaryDetailListGDV = new ArrayList<>();
        for (Employee employee : employeeListGDV) {
            SalaryDetail salaryDetail = new SalaryDetail();
            salaryDetail.setEmployeeId(employee.getId());
            salaryDetail.setSalaryId(salaryCreate.getId());
            Department department = departmentRepository.findDepartmentByCode(employee.getDepartment());
            if (department != null) {
                salaryDetail.setTenDonVi(department.getName());
            }
            salaryDetail.setDichVu(employee.getServiceTypeName() + " cấp " + employee.getRank() + " - vùng " + employee.getRegion());
            AttendanceDetail attendanceDetail = new AttendanceDetail();
            if (salary.getAttendanceId() != null) {
                salaryCreate.setMonth(attendance.getMonth());
                salaryCreate.setYear(attendance.getYear());
                attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(salary.getAttendanceId(), employee.getId());
                if (attendanceDetail.getId() != null) {
                    if (
                        attendanceDetail.getNumberWork() != null &&
                        attendanceDetail.getPaidWorking() != null &&
                        employee.getBasicSalary() != null
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
            BigDecimal luongCoDinhThucTe = BigDecimal.ZERO;

            if (
                salaryDetail.getNumberWorking() != null && salaryDetail.getNumberWorkInMonth() != null && employee.getBasicSalary() != null
            ) {
                salaryAmount =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                        .multiply(employee.getBasicSalary())
                        .setScale(0, RoundingMode.HALF_UP);
                luongCoDinhThucTe =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                        .multiply(employee.getMucChiTraToiThieu())
                        .setScale(0, RoundingMode.HALF_UP);
            }
            salaryDetail.setPhiCoDinhDaThucHien(salaryAmount);
            salaryDetail.setVung(employee.getRegion());
            salaryDetail.setCap(employee.getRank());
            salaryDetail.setMucChiToiThieu(employee.getMucChiTraToiThieu());
            salaryDetail.setLuongCoDinhThucTe(luongCoDinhThucTe);
            salaryDetail.setChucDanh(serviceTypeName(employee.getServiceType()));
            salaryDetail.setDonGiaDichVu(employee.getBasicSalary());
            salaryDetail.setNhom(employee.getNhom());
            salaryDetail.setDiaBan(employee.getDepartment());
            salaryDetailListGDV.add(salaryDetail);
        }

        List<SalaryDetail> salaryDetailListKAM = new ArrayList<>();
        for (Employee employee : employeeListKAM) {
            SalaryDetail salaryDetail = new SalaryDetail();
            salaryDetail.setEmployeeId(employee.getId());
            salaryDetail.setSalaryId(salaryCreate.getId());
            Department department = departmentRepository.findDepartmentByCode(employee.getDepartment());
            if (department != null) {
                salaryDetail.setTenDonVi(department.getName());
            }
            salaryDetail.setDichVu(employee.getServiceTypeName() + " Vùng " + employee.getRegion());
            salaryDetail.setHtc("1");
            AttendanceDetail attendanceDetail = new AttendanceDetail();
            if (salary.getAttendanceId() != null) {
                salaryCreate.setMonth(attendance.getMonth());
                salaryCreate.setYear(attendance.getYear());
                attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(salary.getAttendanceId(), employee.getId());
                if (attendanceDetail.getId() != null) {
                    if (
                        attendanceDetail.getNumberWork() != null &&
                        attendanceDetail.getPaidWorking() != null &&
                        employee.getBasicSalary() != null
                    ) {
                        salaryDetail.setNumberWorkInMonth(attendanceDetail.getNumberWork());
                        salaryDetail.setNumberWorking(attendanceDetail.getPaidWorking());
                        salaryCreate.setNumberWork(attendanceDetail.getNumberWork());
                    }
                }
            } else {
                if (salary.getNumberWork() != null) {
                    salaryDetail.setNumberWorking(salary.getNumberWork());
                    salaryDetail.setNumberWorkInMonth(salary.getNumberWork());
                } else if (salary.getYear() != null && salary.getMonth() != null) {
                    int numberWorking = countWorkingInMonth(salary.getYear().intValue(), salary.getMonth().intValue());
                    salaryDetail.setNumberWorking(BigDecimal.valueOf(numberWorking));
                    salaryDetail.setNumberWorkInMonth(BigDecimal.valueOf(numberWorking));
                }
            }
            BigDecimal salaryAmount = BigDecimal.ZERO;
            BigDecimal luongCoDinhThucTe = BigDecimal.ZERO;

            if (
                salaryDetail.getNumberWorking() != null && salaryDetail.getNumberWorkInMonth() != null && employee.getBasicSalary() != null
            ) {
                salaryAmount =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                        .multiply(employee.getBasicSalary())
                        .setScale(0, RoundingMode.HALF_UP);
                luongCoDinhThucTe =
                    salaryDetail
                        .getNumberWorking()
                        .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                        .multiply(employee.getMucChiTraToiThieu())
                        .setScale(0, RoundingMode.HALF_UP);
            }
            salaryDetail.setPhiCoDinhDaThucHien(salaryAmount);
            salaryDetail.setChiPhiThueDichVu(salaryAmount);
            salaryDetail.setChiPhiDichVuKhoanVaKK(BigDecimal.ZERO);
            salaryDetail.setChiPhiKKKhac(BigDecimal.ZERO);
            salaryDetail.setTongChiPhiKVKK(BigDecimal.ZERO);
            salaryDetail.setVung(employee.getRegion());
            salaryDetail.setMucChiToiThieu(employee.getMucChiTraToiThieu());
            salaryDetail.setLuongCoDinhThucTe(luongCoDinhThucTe);
            salaryDetail.setChucDanh(serviceTypeName(employee.getServiceType()));
            salaryDetail.setDonGiaDichVu(employee.getBasicSalary());
            salaryDetail.setNhom(employee.getNhom());
            salaryDetailListKAM.add(salaryDetail);
            //            salaryDetailRepository.save(salaryDetail);
        }

        List<SalaryDetail> salaryDetailListNVBH = new ArrayList<>();
        for (Employee employee : employeeListNVBH) {
            SalaryDetail salaryDetail = new SalaryDetail();
            salaryDetail.setEmployeeId(employee.getId());
            salaryDetail.setSalaryId(salaryCreate.getId());
            Department department = departmentRepository.findDepartmentByCode(employee.getDepartment());
            if (department != null) {
                salaryDetail.setTenDonVi(department.getName());
                salaryDetail.setDiaBan(department.getName());
            }
            salaryDetail.setDichVu(employee.getServiceTypeName() + " Vùng " + employee.getRegion());
            AttendanceDetail attendanceDetail = new AttendanceDetail();
            if (salary.getAttendanceId() != null) {
                salaryCreate.setMonth(attendance.getMonth());
                salaryCreate.setYear(attendance.getYear());
                attendanceDetail = this.attendanceDetailRepository.selectAllByAttIdAndEm(salary.getAttendanceId(), employee.getId());
                if (attendanceDetail.getId() != null) {
                    if (
                        attendanceDetail.getNumberWork() != null &&
                        attendanceDetail.getPaidWorking() != null &&
                        employee.getBasicSalary() != null
                    ) {
                        salaryDetail.setNumberWorkInMonth(attendanceDetail.getNumberWork());
                        salaryDetail.setNumberWorking(attendanceDetail.getPaidWorking());
                        salaryCreate.setNumberWork(attendanceDetail.getNumberWork());
                    }
                }
            } else {
                if (salary.getNumberWork() != null) {
                    salaryDetail.setNumberWorking(salary.getNumberWork());
                    salaryDetail.setNumberWorkInMonth(salary.getNumberWork());
                } else if (salary.getYear() != null && salary.getMonth() != null) {
                    int numberWorking = countWorkingInMonth(salary.getYear().intValue(), salary.getMonth().intValue());
                    salaryDetail.setNumberWorking(BigDecimal.valueOf(numberWorking));
                    salaryDetail.setNumberWorkInMonth(BigDecimal.valueOf(numberWorking));
                }
            }

            salaryDetail.setVung(employee.getRegion());
            salaryDetail.setChucDanh(serviceTypeName(employee.getServiceType()));
            salaryDetail.setDonGiaDichVu(employee.getBasicSalary());
            salaryDetail.setMucChiToiThieu(employee.getMucChiTraToiThieu());
            salaryDetail.setNhom(employee.getNhom());
            salaryDetailListAM.add(salaryDetail);
        }

        if (salaryDetailListGDV.size() > 0) {
            salaryDetailRepository.saveAll(salaryDetailListGDV);
        }
        if (salaryDetailListKAM.size() > 0) {
            salaryDetailRepository.saveAll(salaryDetailListKAM);
        }
        if (salaryDetailListAM.size() > 0) {
            salaryDetailRepository.saveAll(salaryDetailListAM);
        }
        if (salaryDetailListHTVP.size() > 0) {
            salaryDetailRepository.saveAll(salaryDetailListHTVP);
        }
        if (salaryDetailListNVBH.size() > 0) {
            salaryDetailRepository.saveAll(salaryDetailListNVBH);
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
            if (date.getDayOfWeek().getValue() == 7) {
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
