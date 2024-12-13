package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.AttendanceDetailRepository;
import com.mycompany.myapp.repository.AttendanceRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    public byte[] exportAttendance(Long att) throws IOException {
        List<AttendanceDetail> attendanceDetails = new ArrayList<>();
        attendanceDetails = this.attendanceDetailRepository.selectAllByAttId(att);

        Attendance attendance = this.attendanceRepository.findById(att).get();

        Employee employee = new Employee();
        employee = employeeRepository.findById(attendance.getEmployeeId()).get();

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
        String[] headers = { "STT", "Ngày chấm công", "Thời gian đến", "Thời gian về", "Số giờ làm việc", "Trạng thái", "Ghi chú" };

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 6000);

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
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

        Row rowEmployee = sheet.createRow(5);
        Cell cellEmployee = rowEmployee.createCell(0);
        cellEmployee.setCellValue("Họ và tên nhân viên: " + employee.getName());
        cellEmployee.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 6));

        Row rowDate = sheet.createRow(6);
        Cell cellDate = rowDate.createCell(0);
        cellDate.setCellValue("Bảng chấm công tháng " + attendance.getMonth() + " năm " + attendance.getYear());
        cellDate.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 6));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Định dạng

        int rowNum = 8;
        Row headerRow = sheet.createRow(rowNum++);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(centeredBoldStyle);
        }
        int stt = 1;
        for (AttendanceDetail rowData : attendanceDetails) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stt);
            cell0.setCellStyle(detailStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(rowData.getTime() != null ? rowData.getTime().format(formatter).toString() : "");
            cell1.setCellStyle(detailStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(rowData.getInTime() != null ? rowData.getInTime().toString() : "");
            cell2.setCellStyle(detailStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(rowData.getOutTime() != null ? rowData.getOutTime().toString() : "");
            cell3.setCellStyle(detailStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(rowData.getCountTime() != null ? rowData.getCountTime().toString() : "");
            cell4.setCellStyle(detailStyle);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(rowData.getStatus() != null ? rowData.getStatus().toString() : "");
            cell5.setCellStyle(detailStyle);

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(rowData.getNote() != null ? rowData.getNote().toString() : "");
            cell6.setCellStyle(detailStyle);
            stt++;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        byte[] excelBytes = bos.toByteArray();
        return excelBytes;
    }
}
