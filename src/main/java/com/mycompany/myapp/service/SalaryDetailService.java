package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private DepartmentRepository departmentRepository;

    public SalaryDetailService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        SalaryRepository salaryRepository,
        SalaryDetailRepository salaryDetailRepository,
        AttendanceRepository attendanceRepository,
        DepartmentRepository departmentRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.attendanceRepository = attendanceRepository;
        this.departmentRepository = departmentRepository;
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
            salaryDetailListHTVP = this.salaryDetailRepository.getAllBySalaryIdHtvp(salaryId);
            salaryDetailListGDV = this.salaryDetailRepository.getAllBySalaryIdGDV(salaryId);
            salaryDetailListAM = this.salaryDetailRepository.getAllBySalaryIdAM(salaryId);
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
            "Địa bàn",
            "Vùng",
            "Cấp",
            "Đơn giá thuê dịch vụ",
            "Mức lương cố định",
            "Hệ sô chức vụ",
            "KPIs",
            "Hct",
            "Số ngày thực hiện tiêu chuẩn",
            "Số ngày thực hiện thực tế",
            "Số ngày không thực hiện dịch vụ",
            "Phí cố định đã thực hiện",
            "Lương cố định thực tế",
            "Chi phí giảm trừ",
            "Mức bổ sung lương tối thiểu vùng",
            "Phí cố định thanh toán thực tế",
            "Chi phí dịch vụ khoán và khuyến khích",
            "Chi phí khoán và khuyến khích khác",
            "Tổng chi phí khoán và khuyến khích",
            "Phí dịch vụ thực tế",
        };

        String[] headersGDV2 = {
            "(1)",
            "(2)",
            "(3)",
            "(4)",
            "(5)",
            "(6)",
            "(7)",
            "(8)",
            "(9)",
            "(10)",
            "(11)",
            "(12)",
            "(13)",
            "(14)",
            "(15)=(7)x(13)/(12)",
            "(16)=(8)x(11)x(13)/(12)",
            "(17)=(8)x[1-(11)]x(13)/(12)",
            "(18)=(8)x(13)/(12)-[(16)+(22)]",
            "(19)=(15)-(17)",
            "(20)",
            "(21)",
            "(22)=(20)+(21)",
            "(23)=(18)+(19)+(22)",
        };
        String[] headersAM = {
            "STT",
            "Mã nhân viên",
            "Họ và tên",
            "Vùng",
            "Mức lương cố định",
            "Hệ số chức vụ",
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

        String[] headersAM2 = {
            "(1)",
            "(2)",
            "(3)",
            "(4)",
            "(5)",
            "(6)",
            "(7)",
            "(8)=(5)x(11)/(10)",
            "(9)",
            "(10)",
            "(11)",
            "(12)=(9)x(11)/(10)",
            "(13)=(8)x(11)/(10)x[1-(7)]",
            "(14)=(12)-(13)",
            "(15)",
            "(16)",
            "(17)=(15)+(16)",
            "(18)=(14)+(17)",
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
        detailStyle.setBorderBottom(BorderStyle.THIN);
        detailStyle.setBorderTop(BorderStyle.THIN);

        CellStyle detailStyleTieuDe = workbook.createCellStyle();
        detailStyleTieuDe.setFont(font);
        detailStyleTieuDe.setBorderRight(BorderStyle.THIN);
        detailStyleTieuDe.setBorderLeft(BorderStyle.THIN);
        detailStyleTieuDe.setBorderBottom(BorderStyle.THIN);
        detailStyleTieuDe.setBorderTop(BorderStyle.THIN);

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
            cellTableName.setCellValue("BẢNG XÁC NHẬN CHI TIẾT CHI PHÍ THUÊ DỊCH VỤ CHI TRẢ THEO THỜI GIAN");
            cellTableName.setCellStyle(tableName);
            sheetHtvp.addMergedRegion(new CellRangeAddress(2, 2, 0, 13));

            Row rowTableName2 = sheetHtvp.createRow(3);
            Cell cellTableName2 = rowTableName2.createCell(0);
            cellTableName2.setCellValue("CÔNG TY DỊCH VỤ MOBIFONE KHU VỰC 4");
            cellTableName2.setCellStyle(tableName);
            sheetHtvp.addMergedRegion(new CellRangeAddress(3, 3, 0, 13));

            Row rowDate = sheetHtvp.createRow(4);
            Cell cellDate = rowDate.createCell(0);
            cellDate.setCellValue("Tháng");
            cellDate.setCellStyle(tableName);
            sheetHtvp.addMergedRegion(new CellRangeAddress(4, 4, 0, 13));

            int rowNum = 7;
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
            String dichVu = "";
            BigDecimal donGiaDichVuThucTeTongCong = BigDecimal.ZERO;
            BigDecimal mucChiTraToiThieuTongCong = BigDecimal.ZERO;
            BigDecimal chiPhiGiamTruTongCong = BigDecimal.ZERO;
            BigDecimal phiDichVuThucTeTongCong = BigDecimal.ZERO;
            for (SalaryDetail rowData : salaryDetailListHTVP) {
                Employee employee = new Employee();
                employee = employeeRepository.findById(rowData.getEmployeeId()).get();

                if (!dichVu.equals(rowData.getDichVu())) {
                    dichVu = rowData.getDichVu();
                    Row rowDichVu = sheetHtvp.createRow(rowNum++);
                    Cell cellDichVu = rowDichVu.createCell(0);
                    cellDichVu.setCellValue(dichVu);
                    cellDichVu.setCellStyle(detailStyleTieuDe);
                    sheetHtvp.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 12));
                    stt = 1;
                }
                Row row = sheetHtvp.createRow(rowNum++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(stt);
                cell0.setCellStyle(detailStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(employee.getName());
                cell1.setCellStyle(detailStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(rowData.getTenDonVi());
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

                if (rowData.getDonGiaDichVuThucNhan() != null) {
                    donGiaDichVuThucTeTongCong = donGiaDichVuThucTeTongCong.add(rowData.getDonGiaDichVuThucNhan());
                }
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(rowData.getDonGiaDichVuThucNhan() != null ? rowData.getDonGiaDichVuThucNhan().toString() : "");
                cell8.setCellStyle(detailStyle);

                if (rowData.getMucChiToiThieu() != null) {
                    mucChiTraToiThieuTongCong = mucChiTraToiThieuTongCong.add(rowData.getMucChiToiThieu());
                }
                Cell cell9 = row.createCell(9);
                cell9.setCellValue(rowData.getMucChiToiThieu() != null ? rowData.getMucChiToiThieu().toString() : "");
                cell9.setCellStyle(detailStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(rowData.getXepLoai() != null ? rowData.getXepLoai().toString() : "");
                cell10.setCellStyle(detailStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(rowData.getHtc() != null ? rowData.getHtc().toString() : "");
                cell11.setCellStyle(detailStyle);

                if (rowData.getChiPhiGiamTru() != null) {
                    chiPhiGiamTruTongCong = chiPhiGiamTruTongCong.add(rowData.getChiPhiGiamTru());
                }
                Cell cell12 = row.createCell(12);
                cell12.setCellValue(
                    rowData.getChiPhiGiamTru() != null && rowData.getChiPhiGiamTru() != BigDecimal.ZERO
                        ? rowData.getChiPhiGiamTru().toString()
                        : ""
                );
                cell12.setCellStyle(detailStyle);

                if (rowData.getChiPhiThueDichVu() != null) {
                    phiDichVuThucTeTongCong = phiDichVuThucTeTongCong.add(rowData.getChiPhiThueDichVu());
                }
                Cell cell13 = row.createCell(13);
                cell13.setCellValue(rowData.getChiPhiThueDichVu() != null ? rowData.getChiPhiThueDichVu().toString() : "");
                cell13.setCellStyle(detailStyle);
                stt++;
            }

            Row tongCong = sheetHtvp.createRow(rowNum);
            for (int i = 0; i < headersHTVP.length; i++) {
                if (i == 0) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue("Tổng cộng");
                    cell.setCellStyle(centeredBoldStyle);
                    sheetHtvp.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 4));
                } else if (i == 8) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        donGiaDichVuThucTeTongCong != null && donGiaDichVuThucTeTongCong != BigDecimal.ZERO
                            ? donGiaDichVuThucTeTongCong.toString()
                            : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 12) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(chiPhiGiamTruTongCong != BigDecimal.ZERO ? chiPhiGiamTruTongCong.toString() : "");
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 13) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(phiDichVuThucTeTongCong != BigDecimal.ZERO ? phiDichVuThucTeTongCong.toString() : "");
                    cell.setCellStyle(detailStyleTieuDe);
                } else {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue("");
                    cell.setCellStyle(detailStyleTieuDe);
                }
            }
        }

        if (salaryDetailListGDV.size() > 0) {
            BigDecimal phiCoDinhDaThucHienTongCong = BigDecimal.ZERO;
            BigDecimal phiCoDinhThanhToanThucTeTongCong = BigDecimal.ZERO;
            BigDecimal chiPhiDichVuKhoanVaKKTongCong = BigDecimal.ZERO;
            BigDecimal chiPhiKhoanVaKKkTongCong = BigDecimal.ZERO;
            BigDecimal tongChiPhiKhoanVaKKTongCong = BigDecimal.ZERO;
            BigDecimal phiDichVuThucTeTongCong = BigDecimal.ZERO;

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
                if (i != 2 && i != 6 && i != 0 && i != 9 && i != 10 && i != 11 && i != 12) {
                    sheetGDV.setColumnWidth(i, 4000);
                }
                if (i == 2) {
                    sheetGDV.setColumnWidth(i, 6000);
                }
                headerRowGDV.setHeight((short) 1000);
                Cell cell = headerRowGDV.createCell(i);
                cell.setCellValue(headersGDV[i]);
                cell.setCellStyle(centeredBoldStyle);
            }

            Row headerRowGDV2 = sheetGDV.createRow(rowNumGDV++);
            for (int i = 0; i < headersGDV2.length; i++) {
                if (i != 2 && i != 6 && i != 0 && i != 9 && i != 10 && i != 11 && i != 12) {
                    sheetGDV.setColumnWidth(i, 4000);
                }
                if (i == 2) {
                    sheetGDV.setColumnWidth(i, 6000);
                }
                headerRowGDV2.setHeight((short) 1000);
                Cell cell = headerRowGDV2.createCell(i);
                cell.setCellValue(headersGDV2[i]);
                cell.setCellStyle(centeredBoldStyle);
            }
            String tenDonVi = "";
            String dichVu = "";
            int sttGDV = 1;
            for (SalaryDetail rowData : salaryDetailListGDV) {
                if (!dichVu.equals(rowData.getDichVu())) {
                    dichVu = rowData.getDichVu();
                    Row rowDichVu = sheetGDV.createRow(rowNumGDV++);
                    Cell cellDichVu = rowDichVu.createCell(0);
                    cellDichVu.setCellValue(dichVu);
                    cellDichVu.setCellStyle(detailStyleTieuDe);
                    sheetGDV.addMergedRegion(new CellRangeAddress(rowNumGDV - 1, rowNumGDV - 1, 0, 12));
                    sttGDV = 1;
                }

                if (!tenDonVi.equals(rowData.getTenDonVi())) {
                    tenDonVi = rowData.getTenDonVi();
                    Row rowTenDonVi = sheetGDV.createRow(rowNumGDV++);
                    Cell cellTenDonVi = rowTenDonVi.createCell(0);
                    cellTenDonVi.setCellValue(tenDonVi);
                    cellTenDonVi.setCellStyle(detailStyleTieuDe);
                    sheetGDV.addMergedRegion(new CellRangeAddress(rowNumGDV - 1, rowNumGDV - 1, 0, 12));
                    sttGDV = 1;
                }

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
                cell3.setCellValue(rowData.getDiaBan());
                cell3.setCellStyle(detailStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(rowData.getVung() != null ? rowData.getVung().toString() : "");
                cell4.setCellStyle(detailStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(rowData.getCap());
                cell5.setCellStyle(detailStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(rowData.getDonGiaDichVu() != null ? rowData.getDonGiaDichVu().toString() : "");
                cell6.setCellStyle(detailStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(rowData.getMucChiToiThieu() != null ? rowData.getMucChiToiThieu().toString() : "");
                cell7.setCellStyle(detailStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue("1");
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
                cell13.setCellValue(
                    rowData.getNumberWorkInMonth().subtract(rowData.getNumberWorking()) != BigDecimal.ZERO
                        ? rowData.getNumberWorkInMonth().subtract(rowData.getNumberWorking()).toString()
                        : ""
                );
                cell13.setCellStyle(detailStyle);

                if (rowData.getPhiCoDinhDaThucHien() != null) {
                    phiCoDinhDaThucHienTongCong = phiCoDinhDaThucHienTongCong.add(rowData.getPhiCoDinhDaThucHien());
                }

                Cell cell14 = row.createCell(14);
                cell14.setCellValue(rowData.getPhiCoDinhDaThucHien() != null ? rowData.getPhiCoDinhDaThucHien().toString() : "");
                cell14.setCellStyle(detailStyle);

                Cell cell15 = row.createCell(15);
                cell15.setCellValue(
                    rowData.getLuongCoDinhThucTe() != null && rowData.getLuongCoDinhThucTe() != BigDecimal.ZERO
                        ? rowData.getLuongCoDinhThucTe().toString()
                        : ""
                );
                cell15.setCellStyle(detailStyle);

                Cell cell16 = row.createCell(16);
                cell16.setCellValue(
                    rowData.getChiPhiGiamTru() != null && rowData.getChiPhiGiamTru() != BigDecimal.ZERO
                        ? rowData.getChiPhiGiamTru().toString()
                        : ""
                );
                cell16.setCellStyle(detailStyle);

                Cell cell17 = row.createCell(17);
                cell17.setCellValue(rowData.getMucBSLuongToiThieuVung() != null ? rowData.getMucBSLuongToiThieuVung().toString() : "");
                cell17.setCellStyle(detailStyle);

                if (rowData.getPhiCoDinhThanhToanThucTe() != null) {
                    phiCoDinhThanhToanThucTeTongCong = phiCoDinhThanhToanThucTeTongCong.add(rowData.getPhiCoDinhThanhToanThucTe());
                }
                Cell cell18 = row.createCell(18);
                cell18.setCellValue(
                    rowData.getPhiCoDinhThanhToanThucTe() != null && rowData.getPhiCoDinhThanhToanThucTe() != BigDecimal.ZERO
                        ? rowData.getPhiCoDinhThanhToanThucTe().toString()
                        : ""
                );
                cell18.setCellStyle(detailStyle);

                if (rowData.getChiPhiDichVuKhoanVaKK() != null) {
                    chiPhiDichVuKhoanVaKKTongCong = chiPhiDichVuKhoanVaKKTongCong.add(rowData.getChiPhiDichVuKhoanVaKK());
                }
                Cell cell19 = row.createCell(19);
                cell19.setCellValue(
                    rowData.getChiPhiDichVuKhoanVaKK() != null && rowData.getChiPhiDichVuKhoanVaKK() != BigDecimal.ZERO
                        ? rowData.getChiPhiDichVuKhoanVaKK().toString()
                        : ""
                );
                cell19.setCellStyle(detailStyle);

                if (rowData.getChiPhiKKKhac() != null) {
                    chiPhiKhoanVaKKkTongCong = chiPhiKhoanVaKKkTongCong.add(rowData.getChiPhiKKKhac());
                }
                Cell cell20 = row.createCell(20);
                cell20.setCellValue(
                    rowData.getChiPhiKKKhac() != null && rowData.getChiPhiKKKhac() != BigDecimal.ZERO
                        ? rowData.getChiPhiKKKhac().toString()
                        : ""
                );
                cell20.setCellStyle(detailStyle);

                if (rowData.getTongChiPhiKVKK() != null) {
                    tongChiPhiKhoanVaKKTongCong = tongChiPhiKhoanVaKKTongCong.add(rowData.getTongChiPhiKVKK());
                }
                Cell cell21 = row.createCell(21);
                cell21.setCellValue(
                    rowData.getTongChiPhiKVKK() != null && rowData.getTongChiPhiKVKK() != BigDecimal.ZERO
                        ? rowData.getTongChiPhiKVKK().toString()
                        : ""
                );
                cell21.setCellStyle(detailStyle);

                if (rowData.getChiPhiThueDichVu() != null) {
                    phiDichVuThucTeTongCong = phiDichVuThucTeTongCong.add(rowData.getChiPhiThueDichVu());
                }
                Cell cell22 = row.createCell(22);
                cell22.setCellValue(rowData.getChiPhiThueDichVu() != null ? rowData.getChiPhiThueDichVu().toString() : "");
                cell22.setCellStyle(detailStyle);
                sttGDV++;
            }
            Row tongCong = sheetGDV.createRow(rowNumGDV);
            for (int i = 0; i < headersGDV.length; i++) {
                if (i == 0) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue("Tổng cộng");
                    cell.setCellStyle(centeredBoldStyle);
                    sheetGDV.addMergedRegion(new CellRangeAddress(rowNumGDV, rowNumGDV, 0, 13));
                } else if (i == 14) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        phiCoDinhDaThucHienTongCong != null && phiCoDinhDaThucHienTongCong != BigDecimal.ZERO
                            ? phiCoDinhDaThucHienTongCong.toString()
                            : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 18) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        phiCoDinhThanhToanThucTeTongCong != BigDecimal.ZERO ? phiCoDinhThanhToanThucTeTongCong.toString() : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 19) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        chiPhiDichVuKhoanVaKKTongCong != null && chiPhiDichVuKhoanVaKKTongCong != BigDecimal.ZERO
                            ? chiPhiDichVuKhoanVaKKTongCong.toString()
                            : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 20) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        chiPhiKhoanVaKKkTongCong != null && chiPhiKhoanVaKKkTongCong != BigDecimal.ZERO
                            ? chiPhiKhoanVaKKkTongCong.toString()
                            : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 21) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(tongChiPhiKhoanVaKKTongCong != BigDecimal.ZERO ? tongChiPhiKhoanVaKKTongCong.toString() : "");
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 22) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(phiDichVuThucTeTongCong != BigDecimal.ZERO ? phiDichVuThucTeTongCong.toString() : "");
                    cell.setCellStyle(detailStyleTieuDe);
                } else {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue("");
                    cell.setCellStyle(detailStyleTieuDe);
                }
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

            Row headerRowAM2 = sheetAM.createRow(rowNumAM++);
            for (int i = 0; i < headersAM2.length; i++) {
                if (i != 6 && i != 0 && i != 9 && i != 10) {
                    sheetAM.setColumnWidth(i, 5000);
                }
                headerRowAM2.setHeight((short) 1000);
                Cell cell = headerRowAM2.createCell(i);
                cell.setCellValue(headersAM2[i]);
                cell.setCellStyle(centeredBoldStyle);
            }

            String tenDonVi = "";
            String dichVu = "";
            int sttAm = 1;

            BigDecimal phiCoDinhDaThucHienTongCong = BigDecimal.ZERO;
            BigDecimal phiCoDinhThanhToanThucTeTongCong = BigDecimal.ZERO;
            BigDecimal chiPhiDichVuKhoanVaKKTongCong = BigDecimal.ZERO;
            BigDecimal chiPhiKhoanVaKKkTongCong = BigDecimal.ZERO;
            BigDecimal tongChiPhiKhoanVaKKTongCong = BigDecimal.ZERO;
            BigDecimal phiDichVuThucTeTongCong = BigDecimal.ZERO;
            BigDecimal chiPhiGiamTruTong = BigDecimal.ZERO;

            for (SalaryDetail rowData : salaryDetailListAM) {
                Employee employee = new Employee();
                employee = employeeRepository.findById(rowData.getEmployeeId()).get();

                if (!dichVu.equals(rowData.getDichVu())) {
                    dichVu = rowData.getDichVu();
                    Row rowDichVu = sheetAM.createRow(rowNumAM++);
                    Cell cellDichVu = rowDichVu.createCell(0);
                    cellDichVu.setCellValue(dichVu);
                    cellDichVu.setCellStyle(detailStyleTieuDe);
                    sheetAM.addMergedRegion(new CellRangeAddress(rowNumAM - 1, rowNumAM - 1, 0, 10));
                    sttAm = 1;
                }

                if (!tenDonVi.equals(rowData.getTenDonVi())) {
                    tenDonVi = rowData.getTenDonVi();
                    Row rowTenDonVi = sheetAM.createRow(rowNumAM++);
                    Cell cellTenDonVi = rowTenDonVi.createCell(0);
                    cellTenDonVi.setCellValue(tenDonVi);
                    cellTenDonVi.setCellStyle(detailStyleTieuDe);
                    sheetAM.addMergedRegion(new CellRangeAddress(rowNumAM - 1, rowNumAM - 1, 0, 10));
                    sttAm = 1;
                }

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
                cell3.setCellValue(rowData.getVung());
                cell3.setCellStyle(detailStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(rowData.getMucChiToiThieu() != null ? rowData.getMucChiToiThieu().toString() : "");
                cell4.setCellStyle(detailStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(1);
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

                if (rowData.getPhiCoDinhDaThucHien() != null) {
                    phiCoDinhDaThucHienTongCong = phiCoDinhDaThucHienTongCong.add(rowData.getPhiCoDinhDaThucHien());
                }
                Cell cell11 = row.createCell(11);
                cell11.setCellValue(rowData.getPhiCoDinhDaThucHien() != null ? rowData.getPhiCoDinhDaThucHien().toString() : "");
                cell11.setCellStyle(detailStyle);

                if (rowData.getChiPhiGiamTru() != null) {
                    chiPhiGiamTruTong = chiPhiGiamTruTong.add(rowData.getChiPhiGiamTru());
                }
                Cell cell12 = row.createCell(12);
                cell12.setCellValue(rowData.getChiPhiGiamTru() != null ? rowData.getChiPhiGiamTru().toString() : "");
                cell12.setCellStyle(detailStyle);

                if (rowData.getPhiCoDinhThanhToanThucTe() != null) {
                    phiCoDinhThanhToanThucTeTongCong = phiCoDinhThanhToanThucTeTongCong.add(rowData.getPhiCoDinhThanhToanThucTe());
                }
                Cell cell13 = row.createCell(13);
                cell13.setCellValue(rowData.getPhiCoDinhThanhToanThucTe() != null ? rowData.getPhiCoDinhThanhToanThucTe().toString() : "");
                cell13.setCellStyle(detailStyle);

                if (rowData.getChiPhiDichVuKhoanVaKK() != null) {
                    chiPhiDichVuKhoanVaKKTongCong = chiPhiDichVuKhoanVaKKTongCong.add(rowData.getChiPhiDichVuKhoanVaKK());
                }
                Cell cell14 = row.createCell(14);
                cell14.setCellValue(rowData.getChiPhiDichVuKhoanVaKK() != null ? rowData.getChiPhiDichVuKhoanVaKK().toString() : "");
                cell14.setCellStyle(detailStyle);

                if (rowData.getChiPhiKKKhac() != null) {
                    chiPhiKhoanVaKKkTongCong = chiPhiKhoanVaKKkTongCong.add(rowData.getChiPhiKKKhac());
                }
                Cell cell15 = row.createCell(15);
                cell15.setCellValue(rowData.getChiPhiKKKhac() != null ? rowData.getChiPhiKKKhac().toString() : "");
                cell15.setCellStyle(detailStyle);

                if (rowData.getTongChiPhiKVKK() != null) {
                    tongChiPhiKhoanVaKKTongCong = tongChiPhiKhoanVaKKTongCong.add(rowData.getTongChiPhiKVKK());
                }
                Cell cell16 = row.createCell(16);
                cell16.setCellValue(rowData.getTongChiPhiKVKK() != null ? rowData.getTongChiPhiKVKK().toString() : "");
                cell16.setCellStyle(detailStyle);

                if (rowData.getDonGiaDichVu() != null) {
                    phiDichVuThucTeTongCong = phiDichVuThucTeTongCong.add(rowData.getDonGiaDichVu());
                }
                Cell cell17 = row.createCell(17);
                cell17.setCellValue(rowData.getDonGiaDichVu() != null ? rowData.getDonGiaDichVu().toString() : "");
                cell17.setCellStyle(detailStyle);

                sttAm++;
            }

            Row tongCong = sheetAM.createRow(rowNumAM);
            for (int i = 0; i < headersAM.length; i++) {
                if (i == 0) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue("Tổng cộng");
                    cell.setCellStyle(centeredBoldStyle);
                    sheetAM.addMergedRegion(new CellRangeAddress(rowNumAM, rowNumAM, 0, 10));
                } else if (i == 11) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        phiCoDinhDaThucHienTongCong != null && phiCoDinhDaThucHienTongCong != BigDecimal.ZERO
                            ? phiCoDinhDaThucHienTongCong.toString()
                            : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 12) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(chiPhiGiamTruTong != BigDecimal.ZERO ? chiPhiGiamTruTong.toString() : "");
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 13) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        phiCoDinhThanhToanThucTeTongCong != BigDecimal.ZERO ? phiCoDinhThanhToanThucTeTongCong.toString() : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 14) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        chiPhiDichVuKhoanVaKKTongCong != null && chiPhiDichVuKhoanVaKKTongCong != BigDecimal.ZERO
                            ? chiPhiDichVuKhoanVaKKTongCong.toString()
                            : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 25) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(
                        chiPhiKhoanVaKKkTongCong != null && chiPhiKhoanVaKKkTongCong != BigDecimal.ZERO
                            ? chiPhiKhoanVaKKkTongCong.toString()
                            : ""
                    );
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 16) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(tongChiPhiKhoanVaKKTongCong != BigDecimal.ZERO ? tongChiPhiKhoanVaKKTongCong.toString() : "");
                    cell.setCellStyle(detailStyleTieuDe);
                } else if (i == 17) {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue(phiDichVuThucTeTongCong != BigDecimal.ZERO ? phiDichVuThucTeTongCong.toString() : "");
                    cell.setCellStyle(detailStyleTieuDe);
                } else {
                    Cell cell = tongCong.createCell(i);
                    cell.setCellValue("");
                    cell.setCellStyle(detailStyleTieuDe);
                }
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu())
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getHtc())))
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu())
                            .setScale(0, RoundingMode.HALF_UP);
                    luongCoDinhThucTe =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getKpis())))
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu())
                            .setScale(0, RoundingMode.HALF_UP);
                    luongCoDinhThucTe =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(salaryDetail.getHtc()))
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .setScale(0, RoundingMode.HALF_UP);
                    luongCoDinhThucTeCheckLuongbs =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getHtc())))
                            .setScale(0, RoundingMode.HALF_UP);
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

    public void createSalaryDetailAllImport(List<SalaryDetail> salaryDetailList) {
        for (SalaryDetail salaryDetail : salaryDetailList) {
            Employee employee = employeeRepository.getByCode(salaryDetail.getEmployeeCode());
            salaryDetail.setEmployeeId(employee != null ? employee.getId() : null);
            salaryDetail.setNhom(employee != null ? employee.getNhom() : null);

            SalaryDetail salaryDetailCheck = salaryDetailRepository.getSalaryDetailBySalaryIdEndEmployeeCode(
                salaryDetail.getSalaryId(),
                salaryDetail.getEmployeeCode()
            );
            salaryDetail.setId(salaryDetailCheck != null ? salaryDetailCheck.getId() : null);
            salaryDetail.setDichVu(salaryDetailCheck.getDichVu());
            salaryDetail.setTenDonVi(salaryDetailCheck.getTenDonVi());
        }
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu())
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getHtc())))
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu())
                            .setScale(0, RoundingMode.HALF_UP);
                    luongCoDinhThucTe =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getKpis())))
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getDonGiaDichVu())
                            .setScale(0, RoundingMode.HALF_UP);
                    luongCoDinhThucTe =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(salaryDetail.getHtc()))
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .setScale(0, RoundingMode.HALF_UP);
                    luongCoDinhThucTeCheckLuongbs =
                        salaryDetail
                            .getNumberWorking()
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .setScale(0, RoundingMode.HALF_UP);
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
                            .divide(salaryDetail.getNumberWorkInMonth(), 15, RoundingMode.HALF_UP)
                            .multiply(salaryDetail.getMucChiToiThieu())
                            .multiply(BigDecimal.ONE.subtract(new BigDecimal(salaryDetail.getHtc())))
                            .setScale(0, RoundingMode.HALF_UP);
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
