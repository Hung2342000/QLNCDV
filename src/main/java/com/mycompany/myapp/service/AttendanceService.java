package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    private DepartmentRepository departmentRepository;
    private final NgayNghiLeRepository ngayNghiLeRepository;

    public AttendanceService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository,
        DepartmentRepository departmentRepository,
        NgayNghiLeRepository ngayNghiLeRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
        this.departmentRepository = departmentRepository;
        this.ngayNghiLeRepository = ngayNghiLeRepository;
    }

    public Attendance createAttendance(Attendance attendance) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();

        List<Employee> listEmployee = new ArrayList<>();
        if (attendance.getSearchNhom() == null || attendance.getSearchNhom().equals("")) {
            if (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))) {
                listEmployee =
                    this.employeeRepository.listAllEmployeesNoNhom(
                            attendance.getSearchName() != null ? attendance.getSearchName().toLowerCase() : "",
                            attendance.getSearchDepartment() != null ? attendance.getSearchDepartment() : ""
                        );
            } else if (
                (
                    authentication != null &&
                    getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
                )
            ) {
                listEmployee =
                    this.employeeRepository.listAllEmployeesNoNhom(
                            attendance.getSearchName() != null ? attendance.getSearchName().toLowerCase() : "",
                            user.getDepartment()
                        );
            }
        } else {
            if (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))) {
                listEmployee =
                    this.employeeRepository.listAllEmployees(
                            attendance.getSearchName() != null ? attendance.getSearchName().toLowerCase() : "",
                            attendance.getSearchDepartment() != null ? attendance.getSearchDepartment() : "",
                            attendance.getSearchNhom() != null ? attendance.getSearchNhom() : ""
                        );
            } else if (
                authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
            ) {
                listEmployee =
                    this.employeeRepository.listAllEmployees(
                            attendance.getSearchName() != null ? attendance.getSearchName().toLowerCase() : "",
                            user.getDepartment(),
                            attendance.getSearchNhom() != null ? attendance.getSearchNhom() : ""
                        );
            }
        }

        if (listEmployee != null && listEmployee.size() > 0) {
            attendance.setEmployees(listEmployee);
        }

        attendance.setDepartmentCode(user.getDepartment());
        String ngayNghi = attendance.getNgayNghi();
        List<String> ngayNghiCheck = new ArrayList<>();
        if (ngayNghi != null) {
            ngayNghiCheck.addAll(Arrays.asList(ngayNghi.split("[,;\\s]+")));
        }
        Attendance attendanceCreate = this.attendanceRepository.save(attendance);
        List<AttendanceDetail> attendanceDetailList = new ArrayList<>();
        for (Employee employee : listEmployee) {
            AttendanceDetail attendanceDetail = new AttendanceDetail();
            attendanceDetail.setAttendanceId(attendance.getId());
            attendanceDetail.setEmployeeId(employee.getId());
            attendanceDetail.setEmployeeName(employee.getName());
            attendanceDetail.setServiceTypeName(employee.getServiceTypeName());
            attendanceDetail.setEmployeeCode(employee.getCodeEmployee());
            Department department = departmentRepository.findDepartmentByCode(employee.getDepartment());
            if (department != null) {
                attendanceDetail.setDepartment(department.getName());
            }

            Field[] fields = attendanceDetail.getClass().getDeclaredFields();
            int[] day31 = { 1, 3, 5, 7, 8, 10, 12 };
            BigDecimal countDay = BigDecimal.ZERO;
            int[] day30 = { 4, 6, 9, 11 };
            for (Field field : fields) {
                field.setAccessible(true); // Cho phép truy cập thuộc tính private
                try {
                    // Kiểm tra kiểu dữ liệu và cập nhật giá trị
                    if (field.getName().contains("day") && attendance.getMonth() != null && attendance.getYear() != null) {
                        String daycheck = field.getName().replaceAll("day", "");

                        // check tháng có 31 ngày
                        if (Arrays.stream(day31).anyMatch(day -> day == attendance.getMonth().intValue())) {
                            LocalDate date = LocalDate.of(
                                attendance.getYear().intValue(),
                                attendance.getMonth().intValue(),
                                Integer.parseInt(daycheck)
                            );
                            Boolean check = isWeekend(date);
                            Boolean checkHoliday = isHoliday(date);
                            if (check) {
                                field.set(attendanceDetail, "off");
                            } else if (ngayNghiCheck.contains(daycheck)) {
                                field.set(attendanceDetail, "L");
                            } else if (checkHoliday) {
                                field.set(attendanceDetail, "L");
                            } else {
                                field.set(attendanceDetail, "+");
                                countDay = countDay.add(BigDecimal.valueOf(1));
                            }
                        }
                        //check tháng có 30 ngày
                        else if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                            if (!daycheck.equals("31")) {
                                LocalDate date = LocalDate.of(
                                    attendance.getYear().intValue(),
                                    attendance.getMonth().intValue(),
                                    Integer.parseInt(daycheck)
                                );
                                Boolean check = isWeekend(date);
                                Boolean checkHoliday = isHoliday(date);
                                if (check) {
                                    field.set(attendanceDetail, "off");
                                } else if (ngayNghiCheck.contains(daycheck)) {
                                    field.set(attendanceDetail, "L");
                                } else if (checkHoliday) {
                                    field.set(attendanceDetail, "L");
                                } else {
                                    field.set(attendanceDetail, "+");
                                    countDay = countDay.add(BigDecimal.valueOf(1));
                                }
                            }
                        } else {
                            //check tháng 2 và thêm dữ liệu
                            if (attendance.getYear().intValue() % 4 == 0) {
                                if (!daycheck.equals("30") && !daycheck.equals("31")) {
                                    LocalDate date = LocalDate.of(
                                        attendance.getYear().intValue(),
                                        attendance.getMonth().intValue(),
                                        Integer.parseInt(daycheck)
                                    );
                                    Boolean check = isWeekend(date);
                                    Boolean checkHoliday = isHoliday(date);
                                    if (check) {
                                        field.set(attendanceDetail, "off");
                                    } else if (ngayNghiCheck.contains(daycheck)) {
                                        field.set(attendanceDetail, "L");
                                    } else if (checkHoliday) {
                                        field.set(attendanceDetail, "L");
                                    } else {
                                        field.set(attendanceDetail, "+");
                                        countDay = countDay.add(BigDecimal.valueOf(1));
                                    }
                                }
                            } else {
                                if (!daycheck.equals("29") && !daycheck.equals("30") && !daycheck.equals("31")) {
                                    LocalDate date = LocalDate.of(
                                        attendance.getYear().intValue(),
                                        attendance.getMonth().intValue(),
                                        Integer.parseInt(daycheck)
                                    );
                                    Boolean check = isWeekend(date);
                                    Boolean checkHoliday = isHoliday(date);
                                    if (check) {
                                        field.set(attendanceDetail, "off");
                                    } else if (ngayNghiCheck.contains(daycheck)) {
                                        field.set(attendanceDetail, "L");
                                    } else if (checkHoliday) {
                                        field.set(attendanceDetail, "L");
                                    } else {
                                        field.set(attendanceDetail, "+");
                                        countDay = countDay.add(BigDecimal.valueOf(1));
                                    }
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            attendanceDetail.setPaidWorking(countDay);
            if (attendance.getNumberWork() != null) {
                attendanceDetail.setNumberWork(attendance.getNumberWork());
            } else {
                attendanceDetail.setNumberWork(countDay);
            }
            attendanceDetailList.add(attendanceDetail);
        }
        attendanceDetailRepository.saveAll(attendanceDetailList);
        return attendanceCreate;
    }

    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SUNDAY;
    }

    public boolean isHoliday(LocalDate date) {
        List<NgayNghiLe> list = new ArrayList<>();
        list = ngayNghiLeRepository.getNgayNghiLeByHolidayDate(date);
        if (list.size() > 0) {
            return true;
        }
        return false;
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
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            page = this.attendanceRepository.getAllAttendancePage(pageable);
        } else if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            page = this.attendanceRepository.getAttendanceByDepartment(user.getDepartment(), pageable);
        }
        return page;
    }

    public List<Attendance> getAllByDepartmentAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        List<Attendance> list = new ArrayList<>();
        if (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))) {
            list = this.attendanceRepository.getAllAttendanceNoPage();
        } else if (
            authentication != null && !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            list = this.attendanceRepository.getAttendanceByDepartment(user.getDepartment());
        }
        return list;
    }

    public void deleteAttendance(Long id) {
        List<AttendanceDetail> list = new ArrayList<>();
        list = this.attendanceDetailRepository.selectAllByAttId(id);
        if (list != null && list.size() > 0) {
            this.attendanceDetailRepository.deleteAllByAttendanceId(id);
        }
        this.attendanceRepository.deleteById(id);
    }

    public byte[] exportAttendance(Long att) throws IOException {
        List<AttendanceDetail> attendanceDetails = new ArrayList<>();
        attendanceDetails = this.attendanceDetailRepository.selectAllByAttId(att);

        Attendance attendance = this.attendanceRepository.findById(att).get();

        int[] day31 = { 1, 3, 5, 7, 8, 10, 12 };
        BigDecimal countDay = BigDecimal.ZERO;
        int[] day30 = { 4, 6, 9, 11 };

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");

        Font fontDetail = workbook.createFont();
        fontDetail.setFontName("Times New Roman");

        CellStyle boldStyle = workbook.createCellStyle();
        boldStyle.setFont(font);

        CellStyle detailStyle = workbook.createCellStyle();
        detailStyle.setFont(fontDetail);
        detailStyle.setBorderRight(BorderStyle.THIN);
        detailStyle.setBorderLeft(BorderStyle.THIN);

        CellStyle tableName = workbook.createCellStyle();
        tableName.setFont(font);
        tableName.setAlignment(HorizontalAlignment.CENTER);
        tableName.setVerticalAlignment(VerticalAlignment.CENTER);
        // Tạo một CellStyle mới và thiết lập Font chữ đậm và căn giữa cho nó
        CellStyle centeredBoldStyle = workbook.createCellStyle();
        centeredBoldStyle.setFont(font);
        centeredBoldStyle.setAlignment(HorizontalAlignment.CENTER);
        centeredBoldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centeredBoldStyle.setBorderBottom(BorderStyle.THIN);
        centeredBoldStyle.setBorderTop(BorderStyle.THIN);
        centeredBoldStyle.setBorderRight(BorderStyle.THIN);
        centeredBoldStyle.setBorderLeft(BorderStyle.THIN);

        // Sample data (replace with your own data)
        String[] headers = {
            "STT",
            "Nhân viên",
            "Ngày 1",
            "Ngày 2",
            "Nga 3",
            "Ngày 4",
            "Ngày 5",
            "Ngày 6",
            "Ngày 7",
            "Ngày 8",
            "Ngày 9",
            "Ngày 10",
            "Ngày 11",
            "Ngày 12",
            "Ngày 13",
            "Ngày 14",
            "Ngày 15",
            "Ngày 16",
            "Ngày 17",
            "Ngày 18",
            "Ngày 19",
            "Ngày 20",
            "Ngày 21",
            "Ngày 22",
            "Ngày 23",
            "Ngày 24",
            "Ngày 25",
            "Ngày 26",
            "Ngày 27",
            "Ngày 28",
            "Ngày 29",
            "Ngày 30",
            "Ngày 31",
            "Số ngày công hưởng lương",
            "Số ngày làm việc trong tháng",
        };

        String[] header30 = {
            "STT",
            "Nhân viên",
            "Ngày 1",
            "Ngày 2",
            "Nga 3",
            "Ngày 4",
            "Ngày 5",
            "Ngày 6",
            "Ngày 7",
            "Ngày 8",
            "Ngày 9",
            "Ngày 10",
            "Ngày 11",
            "Ngày 12",
            "Ngày 13",
            "Ngày 14",
            "Ngày 15",
            "Ngày 16",
            "Ngày 17",
            "Ngày 18",
            "Ngày 19",
            "Ngày 20",
            "Ngày 21",
            "Ngày 22",
            "Ngày 23",
            "Ngày 24",
            "Ngày 25",
            "Ngày 26",
            "Ngày 27",
            "Ngày 28",
            "Ngày 29",
            "Ngày 30",
            "Số ngày công hưởng lương",
            "Số ngày làm việc trong tháng",
        };

        String[] header28 = {
            "STT",
            "Nhân viên",
            "Ngày 1",
            "Ngày 2",
            "Nga 3",
            "Ngày 4",
            "Ngày 5",
            "Ngày 6",
            "Ngày 7",
            "Ngày 8",
            "Ngày 9",
            "Ngày 10",
            "Ngày 11",
            "Ngày 12",
            "Ngày 13",
            "Ngày 14",
            "Ngày 15",
            "Ngày 16",
            "Ngày 17",
            "Ngày 18",
            "Ngày 19",
            "Ngày 20",
            "Ngày 21",
            "Ngày 22",
            "Ngày 23",
            "Ngày 24",
            "Ngày 25",
            "Ngày 26",
            "Ngày 27",
            "Ngày 28",
            "Số ngày công hưởng lương",
            "Số ngày làm việc trong tháng",
        };

        String[] header29 = {
            "STT",
            "Nhân viên",
            "Ngày 1",
            "Ngày 2",
            "Nga 3",
            "Ngày 4",
            "Ngày 5",
            "Ngày 6",
            "Ngày 7",
            "Ngày 8",
            "Ngày 9",
            "Ngày 10",
            "Ngày 11",
            "Ngày 12",
            "Ngày 13",
            "Ngày 14",
            "Ngày 15",
            "Ngày 16",
            "Ngày 17",
            "Ngày 18",
            "Ngày 19",
            "Ngày 20",
            "Ngày 21",
            "Ngày 22",
            "Ngày 23",
            "Ngày 24",
            "Ngày 25",
            "Ngày 26",
            "Ngày 27",
            "Ngày 28",
            "Ngày 29",
            "Số ngày công hưởng lương",
            "Số ngày làm việc trong tháng",
        };

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 6000);

        Row rowTieuDe = sheet.createRow(0);
        Cell cellTieuDe = rowTieuDe.createCell(0);
        cellTieuDe.setCellValue("TỔNG CÔNG TY VIÊN THÔNG MOBIFONE");
        cellTieuDe.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        Row rowDonVi = sheet.createRow(1);
        Cell cellDonVi = rowDonVi.createCell(0);
        cellDonVi.setCellValue("ĐƠN VỊ: MOBIFONE KHU VỰC 4");
        cellDonVi.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        Row rowTableName = sheet.createRow(3);
        Cell cellTableName = rowTableName.createCell(0);
        cellTableName.setCellValue("BẢNG CHẤM CÔNG");
        cellTableName.setCellStyle(tableName);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 32));

        Row rowDate = sheet.createRow(6);
        Cell cellDate = rowDate.createCell(0);
        cellDate.setCellValue("Bảng chấm công tháng " + attendance.getMonth() + " năm " + attendance.getYear());
        cellDate.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 6));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Định dạng

        int rowNum = 8;
        Row headerRow = sheet.createRow(rowNum++);

        if (attendance.getMonth() != null && attendance.getYear() != null) {
            if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                for (int i = 0; i < header30.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(header30[i]);
                    cell.setCellStyle(centeredBoldStyle);
                }
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                for (int i = 0; i < header29.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(header29[i]);
                    cell.setCellStyle(centeredBoldStyle);
                }
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                for (int i = 0; i < header28.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(header28[i]);
                    cell.setCellStyle(centeredBoldStyle);
                }
            } else {
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(centeredBoldStyle);
                }
            }
        }

        int stt = 1;
        for (AttendanceDetail rowData : attendanceDetails) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stt);
            cell0.setCellStyle(detailStyle);

            Employee employeeCheck = employeeRepository.findById(rowData.getEmployeeId()).get();

            Cell cellEmployee = row.createCell(1);
            if (employeeCheck != null) {
                cellEmployee.setCellValue(employeeCheck.getName() != null ? employeeCheck.getName() : "");
            }
            cellEmployee.setCellStyle(detailStyle);

            Cell cellDay1 = row.createCell(2);
            cellDay1.setCellValue(rowData.getDay1() != null ? rowData.getDay1().toString() : "");
            cellDay1.setCellStyle(detailStyle);

            Cell cellDay2 = row.createCell(3);
            cellDay2.setCellValue(rowData.getDay2() != null ? rowData.getDay2().toString() : "");
            cellDay2.setCellStyle(detailStyle);

            Cell cellDay3 = row.createCell(4);
            cellDay3.setCellValue(rowData.getDay3() != null ? rowData.getDay3().toString() : "");
            cellDay3.setCellStyle(detailStyle);

            Cell cellDay4 = row.createCell(5);
            cellDay4.setCellValue(rowData.getDay4() != null ? rowData.getDay4().toString() : "");
            cellDay4.setCellStyle(detailStyle);

            Cell cellDay5 = row.createCell(6);
            cellDay5.setCellValue(rowData.getDay5() != null ? rowData.getDay5().toString() : "");
            cellDay5.setCellStyle(detailStyle);

            Cell cellDay6 = row.createCell(7);
            cellDay6.setCellValue(rowData.getDay6() != null ? rowData.getDay6().toString() : "");
            cellDay6.setCellStyle(detailStyle);

            Cell cellDay7 = row.createCell(8);
            cellDay7.setCellValue(rowData.getDay7() != null ? rowData.getDay7().toString() : "");
            cellDay7.setCellStyle(detailStyle);

            Cell cellDay8 = row.createCell(9);
            cellDay8.setCellValue(rowData.getDay8() != null ? rowData.getDay8().toString() : "");
            cellDay8.setCellStyle(detailStyle);

            Cell cellDay9 = row.createCell(10);
            cellDay9.setCellValue(rowData.getDay9() != null ? rowData.getDay9().toString() : "");
            cellDay9.setCellStyle(detailStyle);

            Cell cellDay10 = row.createCell(11);
            cellDay10.setCellValue(rowData.getDay10() != null ? rowData.getDay10().toString() : "");
            cellDay10.setCellStyle(detailStyle);

            Cell cellDay11 = row.createCell(12);
            cellDay11.setCellValue(rowData.getDay11() != null ? rowData.getDay11().toString() : "");
            cellDay11.setCellStyle(detailStyle);

            Cell cellDay12 = row.createCell(13);
            cellDay12.setCellValue(rowData.getDay12() != null ? rowData.getDay12().toString() : "");
            cellDay12.setCellStyle(detailStyle);

            Cell cellDay13 = row.createCell(14);
            cellDay13.setCellValue(rowData.getDay13() != null ? rowData.getDay13().toString() : "");
            cellDay13.setCellStyle(detailStyle);

            Cell cellDay14 = row.createCell(15);
            cellDay14.setCellValue(rowData.getDay14() != null ? rowData.getDay14().toString() : "");
            cellDay14.setCellStyle(detailStyle);

            Cell cellDay15 = row.createCell(16);
            cellDay15.setCellValue(rowData.getDay15() != null ? rowData.getDay15().toString() : "");
            cellDay15.setCellStyle(detailStyle);

            Cell cellDay16 = row.createCell(17);
            cellDay16.setCellValue(rowData.getDay16() != null ? rowData.getDay16().toString() : "");
            cellDay16.setCellStyle(detailStyle);

            Cell cellDay17 = row.createCell(18);
            cellDay17.setCellValue(rowData.getDay17() != null ? rowData.getDay17().toString() : "");
            cellDay17.setCellStyle(detailStyle);

            Cell cellDay18 = row.createCell(19);
            cellDay18.setCellValue(rowData.getDay18() != null ? rowData.getDay18().toString() : "");
            cellDay18.setCellStyle(detailStyle);

            Cell cellDay19 = row.createCell(20);
            cellDay19.setCellValue(rowData.getDay19() != null ? rowData.getDay19().toString() : "");
            cellDay19.setCellStyle(detailStyle);

            Cell cellDay20 = row.createCell(21);
            cellDay20.setCellValue(rowData.getDay20() != null ? rowData.getDay20().toString() : "");
            cellDay20.setCellStyle(detailStyle);

            Cell cellDay21 = row.createCell(22);
            cellDay21.setCellValue(rowData.getDay21() != null ? rowData.getDay21().toString() : "");
            cellDay21.setCellStyle(detailStyle);

            Cell cellDay22 = row.createCell(23);
            cellDay22.setCellValue(rowData.getDay22() != null ? rowData.getDay22().toString() : "");
            cellDay22.setCellStyle(detailStyle);

            Cell cellDay23 = row.createCell(24);
            cellDay23.setCellValue(rowData.getDay23() != null ? rowData.getDay23().toString() : "");
            cellDay23.setCellStyle(detailStyle);

            Cell cellDay24 = row.createCell(25);
            cellDay24.setCellValue(rowData.getDay24() != null ? rowData.getDay24().toString() : "");
            cellDay24.setCellStyle(detailStyle);

            Cell cellDay25 = row.createCell(26);
            cellDay25.setCellValue(rowData.getDay25() != null ? rowData.getDay25().toString() : "");
            cellDay25.setCellStyle(detailStyle);

            Cell cellDay26 = row.createCell(27);
            cellDay26.setCellValue(rowData.getDay26() != null ? rowData.getDay26().toString() : "");
            cellDay26.setCellStyle(detailStyle);

            Cell cellDay27 = row.createCell(28);
            cellDay27.setCellValue(rowData.getDay27() != null ? rowData.getDay27().toString() : "");
            cellDay27.setCellStyle(detailStyle);

            Cell cellDay28 = row.createCell(29);
            cellDay28.setCellValue(rowData.getDay28() != null ? rowData.getDay28().toString() : "");
            cellDay28.setCellStyle(detailStyle);

            if (attendance.getMonth() != null && attendance.getYear() != null) {
                if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                    Cell cellDay29 = row.createCell(30);
                    cellDay29.setCellValue(rowData.getDay29() != null ? rowData.getDay29().toString() : "");
                    cellDay29.setCellStyle(detailStyle);

                    Cell cellDay30 = row.createCell(31);
                    cellDay30.setCellValue(rowData.getDay30() != null ? rowData.getDay30().toString() : "");
                    cellDay30.setCellStyle(detailStyle);

                    Cell cellPaidWorking = row.createCell(32);
                    cellPaidWorking.setCellValue(rowData.getPaidWorking() != null ? rowData.getPaidWorking().toString() : "");
                    cellPaidWorking.setCellStyle(detailStyle);
                    sheet.setColumnWidth(32, 6000);

                    Cell cellNumberWork = row.createCell(33);
                    cellNumberWork.setCellValue(rowData.getNumberWork() != null ? rowData.getNumberWork().toString() : "");
                    cellNumberWork.setCellStyle(detailStyle);
                    sheet.setColumnWidth(33, 6000);
                } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                    Cell cellDay29 = row.createCell(30);
                    cellDay29.setCellValue(rowData.getDay29() != null ? rowData.getDay29().toString() : "");
                    cellDay29.setCellStyle(detailStyle);

                    Cell cellPaidWorking = row.createCell(31);
                    cellPaidWorking.setCellValue(rowData.getPaidWorking() != null ? rowData.getPaidWorking().toString() : "");
                    cellPaidWorking.setCellStyle(detailStyle);
                    sheet.setColumnWidth(31, 6000);

                    Cell cellNumberWork = row.createCell(32);
                    cellNumberWork.setCellValue(rowData.getNumberWork() != null ? rowData.getNumberWork().toString() : "");
                    cellNumberWork.setCellStyle(detailStyle);
                    sheet.setColumnWidth(32, 6000);
                } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                    Cell cellPaidWorking = row.createCell(30);
                    cellPaidWorking.setCellValue(rowData.getPaidWorking() != null ? rowData.getPaidWorking().toString() : "");
                    cellPaidWorking.setCellStyle(detailStyle);
                    sheet.setColumnWidth(30, 6000);

                    Cell cellNumberWork = row.createCell(31);
                    cellNumberWork.setCellValue(rowData.getNumberWork() != null ? rowData.getNumberWork().toString() : "");
                    cellNumberWork.setCellStyle(detailStyle);
                    sheet.setColumnWidth(31, 6000);
                } else {
                    Cell cellDay29 = row.createCell(30);
                    cellDay29.setCellValue(rowData.getDay29() != null ? rowData.getDay29().toString() : "");
                    cellDay29.setCellStyle(detailStyle);

                    Cell cellDay30 = row.createCell(31);
                    cellDay30.setCellValue(rowData.getDay30() != null ? rowData.getDay30().toString() : "");
                    cellDay30.setCellStyle(detailStyle);

                    Cell cellDay31 = row.createCell(32);
                    cellDay31.setCellValue(rowData.getDay31() != null ? rowData.getDay31().toString() : "");
                    cellDay31.setCellStyle(detailStyle);

                    Cell cellPaidWorking = row.createCell(33);
                    cellPaidWorking.setCellValue(rowData.getPaidWorking() != null ? rowData.getPaidWorking().toString() : "");
                    cellPaidWorking.setCellStyle(detailStyle);
                    sheet.setColumnWidth(33, 6000);

                    Cell cellNumberWork = row.createCell(34);
                    cellNumberWork.setCellValue(rowData.getNumberWork() != null ? rowData.getNumberWork().toString() : "");
                    cellNumberWork.setCellStyle(detailStyle);
                    sheet.setColumnWidth(34, 6000);
                }
            }
            stt++;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        byte[] excelBytes = bos.toByteArray();
        return excelBytes;
    }
}
