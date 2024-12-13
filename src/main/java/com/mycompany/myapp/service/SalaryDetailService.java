package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.domain.SalaryDetail;
import com.mycompany.myapp.repository.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import liquibase.pro.packaged.E;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    public byte[] exportSalaryDetail(Long salaryId) throws IOException {
        List<SalaryDetail> salaryDetailList = new ArrayList<>();
        salaryDetailList = this.salaryDetailRepository.getSalaryDetailBySalaryId(salaryId);

        Salary salary = this.salaryRepository.findById(salaryId).get();
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
        String[] headers = { "STT", "Tên nhân viên", "Lương cơ bản", "Số ngày nhận lương", "Phụ cấp", "Lương khuyến khích", "Thành tiền" };

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

        //        Cell cellTieuDeBenPhai = rowTieuDe.createCell(8);

        //        cellTieuDeBenPhai.setCellValue("Biểu số 08b/TSCD/KK");
        //        cellTieuDeBenPhai.setCellStyle(boldStyle);

        Row rowDonVi = sheet.createRow(1);
        Cell cellDonVi = rowDonVi.createCell(0);
        cellDonVi.setCellValue("ĐƠN VỊ: MOBIFONE KHU VỰC 4");
        cellDonVi.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        Row rowTableName = sheet.createRow(2);
        Cell cellTableName = rowTableName.createCell(0);
        cellTableName.setCellValue("BẢNG LƯƠNG");
        cellTableName.setCellStyle(tableName);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));

        Row rowDate = sheet.createRow(4);
        Cell cellDate = rowDate.createCell(0);
        cellDate.setCellValue("Bảng lương tháng " + salary.getMonth() + " năm " + salary.getYear());
        cellDate.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 8));

        int rowNum = 5;
        Row headerRow = sheet.createRow(rowNum++);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(centeredBoldStyle);
        }
        int stt = 1;
        for (SalaryDetail rowData : salaryDetailList) {
            Employee employee = new Employee();
            employee = employeeRepository.findById(rowData.getEmployeeId()).get();
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stt);
            cell0.setCellStyle(detailStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(employee.getName());
            cell1.setCellStyle(detailStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(rowData.getBasicSalary() != null ? rowData.getBasicSalary().toString() : "");
            cell2.setCellStyle(detailStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(rowData.getNumberWorking() != null ? rowData.getNumberWorking().toString() : "");
            cell3.setCellStyle(detailStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(rowData.getAllowance() != null ? rowData.getAllowance().toString() : "");
            cell4.setCellStyle(detailStyle);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(rowData.getIncentiveSalary() != null ? rowData.getIncentiveSalary().toString() : "");
            cell5.setCellStyle(detailStyle);

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(rowData.getAmount() != null ? rowData.getAmount().toString() : "");
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
