package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.AttendanceDetail;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Salary;
import com.mycompany.myapp.domain.SalaryDetail;
import com.mycompany.myapp.repository.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        //        BigDecimal amount = salaryDetail
        //            .getBasicSalary()
        //            .multiply(salaryDetail.getNumberWorking())
        //            .divide(salaryDetail.getNumberWorkInMonth(), 2, RoundingMode.HALF_UP);
        //        if (salaryDetail.getAllowance() != null) {
        //            amount = amount.add(salaryDetail.getAllowance());
        //        }
        //        if (salaryDetail.getIncentiveSalary() != null) {
        //            amount = amount.add(salaryDetail.getIncentiveSalary());
        //        }
        //        salaryDetail.setAmount(amount);
        this.salaryDetailRepository.save(salaryDetail);
        return salaryDetail;
    }

    public byte[] exportSalaryDetail(Long salaryId) throws IOException {
        List<SalaryDetail> salaryDetailList = new ArrayList<>();
        List<SalaryDetail> salaryDetailListHTVP = new ArrayList<>();
        List<SalaryDetail> salaryDetailListGDV = new ArrayList<>();
        List<SalaryDetail> salaryDetailListAM = new ArrayList<>();
        salaryDetailList = this.salaryDetailRepository.getSalaryDetailBySalaryId(salaryId);

        if (salaryDetailList.size() > 0) {
            salaryDetailListHTVP =
                salaryDetailList.stream().filter(salaryDetail -> salaryDetail.getNhom().equals("HTVP")).collect(Collectors.toList());
            salaryDetailListGDV =
                salaryDetailList.stream().filter(salaryDetail -> salaryDetail.getNhom().equals("GDV")).collect(Collectors.toList());
            salaryDetailListAM =
                salaryDetailList.stream().filter(salaryDetail -> salaryDetail.getNhom().equals("AM")).collect(Collectors.toList());
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] headersHTVP = {
            "STT",
            "Họ và tên nhân công dịch vụ",
            "Điểm cung cấp dịch vụ",
            "Chức danh",
            "Vùng",
            "Đơn giá dịch vụ",
            "Số ngày thực hiện dịch vụ tiêu chuẩn",
            "Số ngày thực hiện dịch vụ thực tế",
            "Đơn giá dịch vụ",
            "Mức chi trả tối thiểu",
            "Xếp loại chất lượng dịch vụ",
            "Hệ số hoàn thành chất lượng dịch vụ",
            "Chi phí giảm trừ",
            "Chi phí thuê dịch vụ",
        };
        String[] headersGDV = {
            "STT",
            "Mã nhân viên",
            "Họ và tên",
            "Đơn vị",
            "Địa bàn",
            "Vùng",
            "Cấp",
            "Đơn giá thuê dịch vụ",
            "Mức lương cố định",
            "KPIs",
            "Hct",
            "Số ngày thực hiện tiêu chuẩn",
            "Số ngày thực hiện thực tế",
            "Chi phí giảm trừ",
            "Mức bổ sung lương tối thiểu vùng",
            "Phí cố định thanh toán thực tế",
            "Chi phí dịch vụ khoán và khuyến khích",
            "Chi phí khoán và khuyến khích khác",
            "Tổng chi phí khoán và khuyến khích",
            "Phí dịch vụ thực tế",
        };
        String[] headersAM = {
            "STT",
            "Mã nhân viên",
            "Họ và tên",
            "Địa bàn",
            "Vùng",
            "Mức lương cố định",
            "KPIs",
            "Lương cố định thực tế",
            "Đơn giá thuê dịch vụ",
            "Số ngày thực hiện tiêu chuẩn",
            "Số ngày thực hiện thực tế",
            "Phí cố định đã thực hiện",
            "Chi phí giảm trừ",
            "Phí cố định thanh toán thực tế",
            "Chi phí dịch vụ khoán và khuyến khích",
            "Chi phí khoán và khuyến khích khác",
            "Tổng chi phí khoán và khuyến khích",
            "Phí dịch vụ thực tế",
        };

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
        centeredBoldStyle.setWrapText(true);
        centeredBoldStyle.setAlignment(HorizontalAlignment.CENTER);
        centeredBoldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centeredBoldStyle.setBorderBottom(BorderStyle.THIN);
        centeredBoldStyle.setBorderTop(BorderStyle.THIN);
        centeredBoldStyle.setBorderRight(BorderStyle.THIN);
        centeredBoldStyle.setBorderLeft(BorderStyle.THIN);
        Salary salary = this.salaryRepository.findById(salaryId).get();
        if (salaryDetailListHTVP != null && salaryDetailListHTVP.size() > 0) {
            Sheet sheetHtvp = workbook.createSheet("Service");

            // Sample data (replace with your own data)

            Row rowTieuDe = sheetHtvp.createRow(0);
            Cell cellTieuDe = rowTieuDe.createCell(0);
            cellTieuDe.setCellValue("TỔNG CÔNG TY VIÊN THÔNG MOBIFONE");
            cellTieuDe.setCellStyle(boldStyle);
            sheetHtvp.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            Row rowDonVi = sheetHtvp.createRow(1);
            Cell cellDonVi = rowDonVi.createCell(0);
            cellDonVi.setCellValue("ĐƠN VỊ: MOBIFONE KHU VỰC 4");
            cellDonVi.setCellStyle(boldStyle);
            sheetHtvp.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

            Row rowTableName = sheetHtvp.createRow(2);
            Cell cellTableName = rowTableName.createCell(0);
            cellTableName.setCellValue("Service");
            cellTableName.setCellStyle(tableName);
            sheetHtvp.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));

            Row rowDate = sheetHtvp.createRow(4);
            Cell cellDate = rowDate.createCell(0);
            cellDate.setCellValue("Bảng lương tháng " + salary.getMonth() + " năm " + salary.getYear());
            cellDate.setCellStyle(boldStyle);
            sheetHtvp.addMergedRegion(new CellRangeAddress(4, 4, 0, 8));

            int rowNum = 5;
            Row headerRow = sheetHtvp.createRow(rowNum++);
            for (int i = 0; i < headersHTVP.length; i++) {
                if (i != 4 && i != 0 && i != 5) {
                    sheetHtvp.setColumnWidth(i, 5000);
                }
                headerRow.setHeight((short) 1000);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headersHTVP[i]);
                cell.setCellStyle(centeredBoldStyle);
            }
            int stt = 1;
            for (SalaryDetail rowData : salaryDetailListHTVP) {
                Employee employee = new Employee();
                employee = employeeRepository.findById(rowData.getEmployeeId()).get();

                Row row = sheetHtvp.createRow(rowNum++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(stt);
                cell0.setCellStyle(detailStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(employee.getName());
                cell1.setCellStyle(detailStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(rowData.getDiemCungCapDV());
                cell2.setCellStyle(detailStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(rowData.getChucDanh());
                cell3.setCellStyle(detailStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(rowData.getVung());
                cell4.setCellStyle(detailStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(rowData.getDonGiaDichVu() != null ? rowData.getDonGiaDichVu().toString() : "");
                cell5.setCellStyle(detailStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(rowData.getNumberWorkInMonth() != null ? rowData.getNumberWorkInMonth().toString() : "");
                cell6.setCellStyle(detailStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(rowData.getNumberWorking() != null ? rowData.getNumberWorking().toString() : "");
                cell7.setCellStyle(detailStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(rowData.getDonGiaDichVuThucNhan() != null ? rowData.getDonGiaDichVuThucNhan().toString() : "");
                cell8.setCellStyle(detailStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(rowData.getMucChiToiThieu() != null ? rowData.getMucChiToiThieu().toString() : "");
                cell9.setCellStyle(detailStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(rowData.getXepLoai() != null ? rowData.getXepLoai().toString() : "");
                cell10.setCellStyle(detailStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(rowData.getHtc() != null ? rowData.getHtc().toString() : "");
                cell11.setCellStyle(detailStyle);

                Cell cell12 = row.createCell(12);
                cell12.setCellValue(rowData.getChiPhiGiamTru() != null ? rowData.getChiPhiGiamTru().toString() : "");
                cell12.setCellStyle(detailStyle);

                Cell cell13 = row.createCell(13);
                cell13.setCellValue(rowData.getChiPhiThueDichVu() != null ? rowData.getChiPhiThueDichVu().toString() : "");
                cell13.setCellStyle(detailStyle);

                stt++;
            }
        }

        if (salaryDetailListGDV.size() > 0) {
            Sheet sheetGDV = workbook.createSheet("GDV");
            Row rowTableNameGDV = sheetGDV.createRow(2);
            Cell cellTableNameGDV = rowTableNameGDV.createCell(0);
            cellTableNameGDV.setCellValue(
                "BẢNG XÁC NHẬN CHI PHÍ DỊCH VỤ HỖ TRỢ KINH DOANH TẠI CỬA HÀNG, SHOWROOM " + salary.getMonth() + " NĂM " + salary.getYear()
            );
            cellTableNameGDV.setCellStyle(tableName);
            sheetGDV.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

            int rowNumGDV = 4;
            Row headerRowGDV = sheetGDV.createRow(rowNumGDV++);
            for (int i = 0; i < headersGDV.length; i++) {
                if (i != 6 && i != 0 && i != 9 && i != 10) {
                    sheetGDV.setColumnWidth(i, 5000);
                }
                headerRowGDV.setHeight((short) 1000);
                Cell cell = headerRowGDV.createCell(i);
                cell.setCellValue(headersGDV[i]);
                cell.setCellStyle(centeredBoldStyle);
            }

            int sttGDV = 1;
            for (SalaryDetail rowData : salaryDetailListGDV) {
                Employee employee = new Employee();
                employee = employeeRepository.findById(rowData.getEmployeeId()).get();

                Row row = sheetGDV.createRow(rowNumGDV++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(sttGDV);
                cell0.setCellStyle(detailStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(employee.getCodeEmployee());
                cell1.setCellStyle(detailStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(employee.getName());
                cell2.setCellStyle(detailStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(rowData.getDonVi());
                cell3.setCellStyle(detailStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(rowData.getDiaBan());
                cell4.setCellStyle(detailStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(rowData.getVung() != null ? rowData.getVung().toString() : "");
                cell5.setCellStyle(detailStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(rowData.getCap());
                cell6.setCellStyle(detailStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(rowData.getDonGiaDichVu() != null ? rowData.getDonGiaDichVu().toString() : "");
                cell7.setCellStyle(detailStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(rowData.getMucChiToiThieu() != null ? rowData.getMucChiToiThieu().toString() : "");
                cell8.setCellStyle(detailStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(rowData.getKpis() != null ? rowData.getKpis().toString() : "");
                cell9.setCellStyle(detailStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(rowData.getHtc() != null ? rowData.getHtc().toString() : "");
                cell10.setCellStyle(detailStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(rowData.getNumberWorkInMonth() != null ? rowData.getNumberWorkInMonth().toString() : "");
                cell11.setCellStyle(detailStyle);

                Cell cell12 = row.createCell(12);
                cell12.setCellValue(rowData.getNumberWorking() != null ? rowData.getNumberWorking().toString() : "");
                cell12.setCellStyle(detailStyle);

                Cell cell13 = row.createCell(13);
                cell13.setCellValue(rowData.getPhiCoDinhDaThucHien() != null ? rowData.getPhiCoDinhDaThucHien().toString() : "");
                cell13.setCellStyle(detailStyle);

                Cell cell14 = row.createCell(14);
                cell14.setCellValue(rowData.getLuongCoDinhThucTe() != null ? rowData.getLuongCoDinhThucTe().toString() : "");
                cell14.setCellStyle(detailStyle);

                Cell cell15 = row.createCell(15);
                cell15.setCellValue(rowData.getChiPhiGiamTru() != null ? rowData.getChiPhiGiamTru().toString() : "");
                cell15.setCellStyle(detailStyle);

                Cell cell16 = row.createCell(16);
                cell16.setCellValue(rowData.getMucBSLuongToiThieuVung() != null ? rowData.getMucBSLuongToiThieuVung().toString() : "");
                cell16.setCellStyle(detailStyle);

                Cell cell17 = row.createCell(17);
                cell17.setCellValue(rowData.getPhiCoDinhThanhToanThucTe() != null ? rowData.getPhiCoDinhThanhToanThucTe().toString() : "");
                cell17.setCellStyle(detailStyle);

                Cell cell18 = row.createCell(18);
                cell18.setCellValue(rowData.getChiPhiDichVuKhoanVaKK() != null ? rowData.getChiPhiDichVuKhoanVaKK().toString() : "");
                cell18.setCellStyle(detailStyle);

                Cell cell19 = row.createCell(19);
                cell19.setCellValue(rowData.getChiPhiKKKhac() != null ? rowData.getChiPhiKKKhac().toString() : "");
                cell19.setCellStyle(detailStyle);

                Cell cell20 = row.createCell(20);
                cell20.setCellValue(rowData.getTongChiPhiKVKK() != null ? rowData.getTongChiPhiKVKK().toString() : "");
                cell20.setCellStyle(detailStyle);

                Cell cell21 = row.createCell(21);
                cell21.setCellValue(rowData.getChiPhiThueDichVu() != null ? rowData.getChiPhiThueDichVu().toString() : "");
                cell21.setCellStyle(detailStyle);
                sttGDV++;
            }
        }

        if (salaryDetailListAM.size() > 0) {
            Sheet sheetAM = workbook.createSheet("AM");
            Row rowTableNameGDV = sheetAM.createRow(2);
            Cell cellTableNameGDV = rowTableNameGDV.createCell(0);
            cellTableNameGDV.setCellValue(
                "BẢNG XÁC NHẬN CHI PHÍ DỊCH VỤ HỖ TRỢ KINH DOANH KHÁCH HÀNG DOANH NGHIỆP THÁNG  " +
                salary.getMonth() +
                " NĂM " +
                salary.getYear()
            );
            cellTableNameGDV.setCellStyle(tableName);
            sheetAM.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

            int rowNumAM = 4;
            Row headerRowAM = sheetAM.createRow(rowNumAM++);
            for (int i = 0; i < headersAM.length; i++) {
                if (i != 6 && i != 0 && i != 9 && i != 10) {
                    sheetAM.setColumnWidth(i, 5000);
                }
                headerRowAM.setHeight((short) 1000);
                Cell cell = headerRowAM.createCell(i);
                cell.setCellValue(headersAM[i]);
                cell.setCellStyle(centeredBoldStyle);
            }

            int sttAm = 1;
            for (SalaryDetail rowData : salaryDetailListAM) {
                Employee employee = new Employee();
                employee = employeeRepository.findById(rowData.getEmployeeId()).get();

                Row row = sheetAM.createRow(rowNumAM++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(sttAm);
                cell0.setCellStyle(detailStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(employee.getCodeEmployee());
                cell1.setCellStyle(detailStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(employee.getName());
                cell2.setCellStyle(detailStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(rowData.getDiaBan());
                cell3.setCellStyle(detailStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(rowData.getVung());
                cell4.setCellStyle(detailStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(rowData.getMucChiToiThieu() != null ? rowData.getMucChiToiThieu().toString() : "");
                cell5.setCellStyle(detailStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(rowData.getKpis());
                cell6.setCellStyle(detailStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(rowData.getLuongCoDinhThucTe() != null ? rowData.getLuongCoDinhThucTe().toString() : "");
                cell7.setCellStyle(detailStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(rowData.getDonGiaDichVu() != null ? rowData.getDonGiaDichVu().toString() : "");
                cell8.setCellStyle(detailStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(rowData.getNumberWorkInMonth() != null ? rowData.getNumberWorkInMonth().toString() : "");
                cell9.setCellStyle(detailStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(rowData.getNumberWorking() != null ? rowData.getNumberWorking().toString() : "");
                cell10.setCellStyle(detailStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(rowData.getPhiCoDinhDaThucHien() != null ? rowData.getPhiCoDinhDaThucHien().toString() : "");
                cell11.setCellStyle(detailStyle);

                Cell cell12 = row.createCell(12);
                cell12.setCellValue(rowData.getChiPhiGiamTru() != null ? rowData.getChiPhiGiamTru().toString() : "");
                cell12.setCellStyle(detailStyle);

                Cell cell13 = row.createCell(13);
                cell13.setCellValue(rowData.getPhiCoDinhThanhToanThucTe() != null ? rowData.getPhiCoDinhThanhToanThucTe().toString() : "");
                cell13.setCellStyle(detailStyle);

                Cell cell14 = row.createCell(14);
                cell14.setCellValue(rowData.getChiPhiDichVuKhoanVaKK() != null ? rowData.getChiPhiDichVuKhoanVaKK().toString() : "");
                cell14.setCellStyle(detailStyle);

                Cell cell15 = row.createCell(15);
                cell15.setCellValue(rowData.getChiPhiKKKhac() != null ? rowData.getChiPhiKKKhac().toString() : "");
                cell15.setCellStyle(detailStyle);

                Cell cell16 = row.createCell(16);
                cell16.setCellValue(rowData.getTongChiPhiKVKK() != null ? rowData.getTongChiPhiKVKK().toString() : "");
                cell16.setCellStyle(detailStyle);

                Cell cell17 = row.createCell(17);
                cell17.setCellValue(rowData.getDonGiaDichVu() != null ? rowData.getDonGiaDichVu().toString() : "");
                cell17.setCellStyle(detailStyle);

                sttAm++;
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        byte[] excelBytes = bos.toByteArray();
        return excelBytes;
    }

    public void createSalaryDetailAll(List<SalaryDetail> salaryDetailList) {
        List<SalaryDetail> salaryDetailsAm = new ArrayList<>();
        List<SalaryDetail> salaryDetailsHTVP = new ArrayList<>();
        List<SalaryDetail> salaryDetailsGDV = new ArrayList<>();
        if (salaryDetailList.size() > 0) {
            salaryDetailsHTVP =
                salaryDetailList.stream().filter(salaryDetail -> salaryDetail.getNhom().equals("HTVP")).collect(Collectors.toList());
            salaryDetailsAm =
                salaryDetailList.stream().filter(salaryDetail -> salaryDetail.getNhom().equals("AM")).collect(Collectors.toList());
            salaryDetailsGDV =
                salaryDetailList.stream().filter(salaryDetail -> salaryDetail.getNhom().equals("GDV")).collect(Collectors.toList());
        }
        if (salaryDetailsHTVP.size() > 0) {
            for (SalaryDetail salaryDetail : salaryDetailsHTVP) {
                BigDecimal chiPhiGiamTru = BigDecimal.ZERO;
                BigDecimal donGia = BigDecimal.ZERO;
                if (
                    salaryDetail.getDonGiaDichVu() != null &&
                    salaryDetail.getNumberWorking() != null &&
                    salaryDetail.getNumberWorkInMonth() != null
                ) {
                    donGia =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu());
                }
                salaryDetail.setDonGiaDichVuThucNhan(donGia);
                if (salaryDetail.getXepLoai() != null && salaryDetail.getXepLoai().equals("A")) {
                    salaryDetail.setHtc("1");
                } else if (salaryDetail.getXepLoai() != null && salaryDetail.getXepLoai().equals("B")) {
                    salaryDetail.setHtc("0.9");
                } else if (salaryDetail.getXepLoai() != null && salaryDetail.getXepLoai().equals("C")) {
                    salaryDetail.setHtc("0.8");
                }
                if (
                    salaryDetail.getMucChiToiThieu() != null &&
                    salaryDetail.getNumberWorking() != null &&
                    salaryDetail.getNumberWorkInMonth() != null &&
                    salaryDetail.getHtc() != null &&
                    !salaryDetail.getHtc().equals("")
                ) {
                    chiPhiGiamTru =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getHtc())));
                }
                salaryDetail.setChiPhiGiamTru(chiPhiGiamTru);
                salaryDetail.setChiPhiThueDichVu(salaryDetail.getDonGiaDichVuThucNhan().subtract(salaryDetail.getChiPhiGiamTru()));
                salaryDetailRepository.save(salaryDetail);
            }
        }
        if (salaryDetailsAm.size() > 0) {
            BigDecimal phiCoDinhDaThucHien = BigDecimal.ZERO;
            BigDecimal luongCoDinhThucTe = BigDecimal.ZERO;
            for (SalaryDetail salaryDetail : salaryDetailsAm) {
                if (
                    salaryDetail.getNumberWorking() != null &&
                    salaryDetail.getNumberWorkInMonth() != null &&
                    salaryDetail.getDonGiaDichVu() != null &&
                    salaryDetail.getMucChiToiThieu() != null
                ) {
                    phiCoDinhDaThucHien =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu());
                    luongCoDinhThucTe =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu());
                }

                salaryDetail.setLuongCoDinhThucTe(luongCoDinhThucTe);
                salaryDetail.setPhiCoDinhDaThucHien(phiCoDinhDaThucHien);

                BigDecimal chiPhiGiamTru = BigDecimal.ZERO;

                if (
                    salaryDetail.getMucChiToiThieu() != null &&
                    salaryDetail.getNumberWorking() != null &&
                    salaryDetail.getNumberWorkInMonth() != null &&
                    salaryDetail.getKpis() != null &&
                    !salaryDetail.getKpis().equals("")
                ) {
                    chiPhiGiamTru =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getKpis())));
                }

                salaryDetail.setChiPhiGiamTru(chiPhiGiamTru);
                salaryDetail.setPhiCoDinhThanhToanThucTe(salaryDetail.getPhiCoDinhDaThucHien().subtract(chiPhiGiamTru));
                BigDecimal tongChiPhiKVKK = BigDecimal.ZERO;
                if (salaryDetail.getChiPhiDichVuKhoanVaKK() != null) {
                    tongChiPhiKVKK = tongChiPhiKVKK.add(salaryDetail.getChiPhiDichVuKhoanVaKK());
                }
                if (salaryDetail.getChiPhiKKKhac() != null) {
                    tongChiPhiKVKK = tongChiPhiKVKK.add(salaryDetail.getChiPhiKKKhac());
                }
                salaryDetail.setTongChiPhiKVKK(tongChiPhiKVKK);
                BigDecimal phiDichVuThucTe = BigDecimal.ZERO;
                if (salaryDetail.getPhiCoDinhThanhToanThucTe() != null) {
                    phiDichVuThucTe = phiDichVuThucTe.add(salaryDetail.getPhiCoDinhThanhToanThucTe());
                }
                if (salaryDetail.getTongChiPhiKVKK() != null) {
                    phiDichVuThucTe = phiDichVuThucTe.add(salaryDetail.getTongChiPhiKVKK());
                }
                salaryDetail.setChiPhiThueDichVu(phiDichVuThucTe);
                salaryDetailRepository.save(salaryDetail);
            }
        }

        if (salaryDetailsGDV.size() > 0) {
            BigDecimal phiCoDinhDaThucHien = BigDecimal.ZERO;
            BigDecimal luongCoDinhThucTe = BigDecimal.ZERO;
            BigDecimal luongCoDinhThucTeCheckLuongbs = BigDecimal.ZERO;
            for (SalaryDetail salaryDetail : salaryDetailsGDV) {
                if (salaryDetail.getKpis() != null && salaryDetail.getKpis() != "" && Integer.parseInt(salaryDetail.getKpis()) >= 70) {
                    salaryDetail.setHtc("1");
                } else {
                    salaryDetail.setHtc("0");
                }

                if (
                    salaryDetail.getNumberWorking() != null &&
                    salaryDetail.getNumberWorkInMonth() != null &&
                    salaryDetail.getDonGiaDichVu() != null &&
                    salaryDetail.getMucChiToiThieu() != null
                ) {
                    phiCoDinhDaThucHien =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu());
                    luongCoDinhThucTe =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(salaryDetail.getHtc()))
                            .multiply(salaryDetail.getMucChiToiThieu());
                    luongCoDinhThucTeCheckLuongbs =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu());
                }

                salaryDetail.setLuongCoDinhThucTe(luongCoDinhThucTe);
                salaryDetail.setPhiCoDinhDaThucHien(phiCoDinhDaThucHien);

                BigDecimal chiPhiGiamTru = BigDecimal.ZERO;
                BigDecimal mucBSLuongToiThieuVung = BigDecimal.ZERO;
                BigDecimal checkMucBSLuong = BigDecimal.ZERO;
                if (salaryDetail.getLuongCoDinhThucTe() != null) {
                    checkMucBSLuong = checkMucBSLuong.add(salaryDetail.getLuongCoDinhThucTe());
                }
                if (salaryDetail.getChiPhiDichVuKhoanVaKK() != null) {
                    checkMucBSLuong = checkMucBSLuong.add(salaryDetail.getChiPhiDichVuKhoanVaKK());
                }
                if (salaryDetail.getHtc().equals("0") && checkMucBSLuong.compareTo(salaryDetail.getMucChiToiThieu()) < 0) {
                    mucBSLuongToiThieuVung = luongCoDinhThucTeCheckLuongbs.subtract(checkMucBSLuong);
                }
                salaryDetail.setMucBSLuongToiThieuVung(mucBSLuongToiThieuVung);
                if (
                    salaryDetail.getMucChiToiThieu() != null &&
                    salaryDetail.getNumberWorking() != null &&
                    salaryDetail.getNumberWorkInMonth() != null &&
                    salaryDetail.getHtc() != null &&
                    !salaryDetail.getHtc().equals("")
                ) {
                    chiPhiGiamTru =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 5, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getHtc())));
                }

                salaryDetail.setChiPhiGiamTru(chiPhiGiamTru);
                salaryDetail.setPhiCoDinhThanhToanThucTe(salaryDetail.getPhiCoDinhDaThucHien().subtract(chiPhiGiamTru));
                BigDecimal tongChiPhiKVKK = BigDecimal.ZERO;
                if (salaryDetail.getChiPhiDichVuKhoanVaKK() != null) {
                    tongChiPhiKVKK = tongChiPhiKVKK.add(salaryDetail.getChiPhiDichVuKhoanVaKK());
                }
                if (salaryDetail.getChiPhiKKKhac() != null) {
                    tongChiPhiKVKK = tongChiPhiKVKK.add(salaryDetail.getChiPhiKKKhac());
                }
                salaryDetail.setTongChiPhiKVKK(tongChiPhiKVKK);
                BigDecimal phiDichVuThucTe = BigDecimal.ZERO;
                if (salaryDetail.getPhiCoDinhThanhToanThucTe() != null) {
                    phiDichVuThucTe = phiDichVuThucTe.add(salaryDetail.getPhiCoDinhThanhToanThucTe());
                }
                if (salaryDetail.getTongChiPhiKVKK() != null) {
                    phiDichVuThucTe = phiDichVuThucTe.add(salaryDetail.getTongChiPhiKVKK());
                }
                if (salaryDetail.getMucBSLuongToiThieuVung() != null) {
                    phiDichVuThucTe = phiDichVuThucTe.add(salaryDetail.getMucBSLuongToiThieuVung());
                }
                salaryDetail.setChiPhiThueDichVu(phiDichVuThucTe);
                salaryDetailRepository.save(salaryDetail);
            }
        }
    }
}
