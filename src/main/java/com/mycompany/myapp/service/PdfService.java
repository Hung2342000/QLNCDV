package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.ADMIN;
import static com.mycompany.myapp.security.AuthoritiesConstants.SUPERUSER;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
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
public class PdfService {

    private final Logger log = LoggerFactory.getLogger(PdfService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private AttendanceRepository attendanceRepository;
    private AttendanceDetailRepository attendanceDetailRepository;
    private DepartmentRepository departmentRepository;
    private final NgayNghiLeRepository ngayNghiLeRepository;

    public PdfService(
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

    public ByteArrayResource exportChamCongPdf(Long att) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();

        Attendance attendance = this.attendanceRepository.findById(att).get();
        int[] day30 = { 4, 6, 9, 11 };
        List<AttendanceDetail> attendanceDetails = new ArrayList<>();
        attendanceDetails = this.attendanceDetailRepository.selectAllByAttIdAndKhacNhom(att, "HTVP");
        List<AttendanceDetail> htvp = new ArrayList<>();
        htvp = this.attendanceDetailRepository.selectAllByAttIdAndNhom(att, "HTVP");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(document, out);
        document.open();

        // Font
        BaseFont bf = BaseFont.createFont(
            getClass().getClassLoader().getResource("fonts/time.ttf").getPath(),
            BaseFont.IDENTITY_H,
            BaseFont.EMBEDDED
        );
        Font font = new Font(bf, 8, Font.NORMAL);
        Font boldFont = new Font(bf, 8, Font.BOLD);

        // Header
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new int[] { 1, 1 });

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph("CÔNG TY DỊCH VỤ MOBIFONE KHU VỰC 4", boldFont));
        left.addElement(new Paragraph("          " + user.getDepartmentName().toUpperCase(), boldFont));

        headerTable.addCell(left);

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.addElement(new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", boldFont));
        right.addElement(new Paragraph("            Độc lập - Tự do - Hạnh Phúc", boldFont));
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(right);

        document.add(headerTable);

        Paragraph title = new Paragraph("Tháng " + attendance.getMonth() + " năm " + attendance.getYear(), boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(10);
        title.setSpacingAfter(10);
        document.add(title);

        // Bảng
        int totalColumns = 0;
        if (attendance.getMonth() != null && attendance.getYear() != null) {
            if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                totalColumns = 38;
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                totalColumns = 37;
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                totalColumns = 36;
            } else {
                totalColumns = 39;
            }
        }
        PdfPTable table = new PdfPTable(totalColumns);
        table.setWidthPercentage(100);

        table.addCell(createCell("TT", 2, 1, boldFont));
        table.addCell(createCell("Mã NV", 2, 1, boldFont));
        table.addCell(createCell("Họ và tên", 2, 1, boldFont));
        table.addCell(createCell("Chức vụ", 2, 1, boldFont));
        table.addCell(createCell("Địa bàn", 2, 1, boldFont));

        if (attendance.getMonth() != null && attendance.getYear() != null) {
            if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                table.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 30, boldFont));
                table.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                table.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                table.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 30; i++) {
                    table.addCell(createCell(String.valueOf(i), font));
                }
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                table.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 29, boldFont));
                table.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                table.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                table.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 29; i++) {
                    table.addCell(createCell(String.valueOf(i), font));
                }
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                table.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 28, boldFont));
                table.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                table.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                table.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 28; i++) {
                    table.addCell(createCell(String.valueOf(i), font));
                }
            } else {
                table.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 31, boldFont));
                table.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                table.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                table.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 31; i++) {
                    table.addCell(createCell(String.valueOf(i), font));
                }
            }
        }

        float[] columnWidths = new float[totalColumns];

        columnWidths[0] = 3f; // TT
        columnWidths[1] = 9f; // Mã NV
        columnWidths[2] = 10f; // Họ và tên
        columnWidths[3] = 10f; // Chức vụ
        columnWidths[4] = 8f; // Địa bàn

        if (attendance.getMonth() != null && attendance.getYear() != null) {
            if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                for (int i = 5; i < 35; i++) {
                    columnWidths[i] = 2f; // các ngày đều 2f
                }

                columnWidths[35] = 4f; // Số ngày thực hiện DV
                columnWidths[36] = 4f; // Chất lượng DV
                columnWidths[37] = 3f; // Ghi chú
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                for (int i = 5; i < 34; i++) {
                    columnWidths[i] = 2f; // các ngày đều 2f
                }

                columnWidths[34] = 4f; // Số ngày thực hiện DV
                columnWidths[35] = 4f; // Chất lượng DV
                columnWidths[36] = 3f; // Ghi chú
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                for (int i = 5; i < 33; i++) {
                    columnWidths[i] = 2f; // các ngày đều 2f
                }

                columnWidths[33] = 4f; // Số ngày thực hiện DV
                columnWidths[34] = 4f; // Chất lượng DV
                columnWidths[35] = 3f; // Ghi chú
            } else {
                for (int i = 5; i < 36; i++) {
                    columnWidths[i] = 2f; // các ngày đều 1f
                }

                columnWidths[36] = 4f; // Số ngày thực hiện DV
                columnWidths[37] = 4f; // Chất lượng DV
                columnWidths[38] = 3f; // Ghi chú
            }
        }
        table.setWidths(columnWidths);

        int stt = 1;

        for (AttendanceDetail detail : htvp) {
            table.addCell(createCell(String.valueOf(stt), font));
            Employee employeeCheck = employeeRepository.findById(detail.getEmployeeId()).get();

            if (employeeCheck != null) {
                table.addCell(createCell(employeeCheck.getCodeEmployee() != null ? employeeCheck.getCodeEmployee() : "", font));
                table.addCell(createCell(employeeCheck.getName() != null ? employeeCheck.getName() : "", font));
                table.addCell(createCell(employeeCheck.getServiceTypeName() != null ? employeeCheck.getServiceTypeName() : "", font));
                table.addCell(createCell(employeeCheck.getDiaBan() != null ? employeeCheck.getDiaBan() : "", font));
            }
            table.addCell(createCell(detail.getDay1(), font));
            table.addCell(createCell(detail.getDay2(), font));
            table.addCell(createCell(detail.getDay3(), font));
            table.addCell(createCell(detail.getDay4(), font));
            table.addCell(createCell(detail.getDay5(), font));
            table.addCell(createCell(detail.getDay6(), font));
            table.addCell(createCell(detail.getDay7(), font));
            table.addCell(createCell(detail.getDay8(), font));
            table.addCell(createCell(detail.getDay9(), font));
            table.addCell(createCell(detail.getDay10(), font));
            table.addCell(createCell(detail.getDay11(), font));
            table.addCell(createCell(detail.getDay12(), font));
            table.addCell(createCell(detail.getDay13(), font));
            table.addCell(createCell(detail.getDay14(), font));
            table.addCell(createCell(detail.getDay15(), font));
            table.addCell(createCell(detail.getDay16(), font));
            table.addCell(createCell(detail.getDay17(), font));
            table.addCell(createCell(detail.getDay18(), font));
            table.addCell(createCell(detail.getDay19(), font));
            table.addCell(createCell(detail.getDay20(), font));
            table.addCell(createCell(detail.getDay21(), font));
            table.addCell(createCell(detail.getDay22(), font));
            table.addCell(createCell(detail.getDay23(), font));
            table.addCell(createCell(detail.getDay24(), font));
            table.addCell(createCell(detail.getDay25(), font));
            table.addCell(createCell(detail.getDay26(), font));
            table.addCell(createCell(detail.getDay27(), font));

            if (attendance.getMonth() != null && attendance.getYear() != null) {
                if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                    table.addCell(createCell(detail.getDay28(), font));
                    table.addCell(createCell(detail.getDay29(), font));
                    table.addCell(createCell(detail.getDay30(), font));
                } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                    table.addCell(createCell(detail.getDay28(), font));
                    table.addCell(createCell(detail.getDay29(), font));
                } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                    table.addCell(createCell(detail.getDay28(), font));
                } else {
                    table.addCell(createCell(detail.getDay28(), font));
                    table.addCell(createCell(detail.getDay29(), font));
                    table.addCell(createCell(detail.getDay30(), font));
                    table.addCell(createCell(detail.getDay31(), font));
                }
            }
            table.addCell(createCell(detail.getPaidWorking().toString(), font));
            table.addCell(createCell("A", font));
            table.addCell(createCell("", font));
            stt++;
        }

        document.add(table);
        Paragraph sign = new Paragraph();
        sign.setAlignment(Element.ALIGN_CENTER);
        document.add(sign);

        // Tạo bảng có 2 cột, không có viền
        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(80);
        signTable.setWidths(new float[] { 1, 1 });

        // Ô bên trái: Người lập bảng
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        leftCell.addElement(new Paragraph("NGƯỜI LẬP BẢNG", boldFont));
        leftCell.addElement(new Paragraph("  (Ký, ghi rõ họ tên)", font));

        // Ô bên phải: Ngày + Trưởng đơn vị
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(new Paragraph("     , ngày     tháng     năm     ", font));
        rightCell.addElement(new Paragraph("   TRƯỞNG ĐƠN VỊ", boldFont));
        rightCell.addElement(new Paragraph("   (Ký, ghi rõ họ tên)", font));

        signTable.addCell(leftCell);
        signTable.addCell(rightCell);

        // Thêm bảng vào document
        document.add(signTable);

        document.newPage();
        PdfPTable headerTablePage2 = new PdfPTable(2);
        headerTablePage2.setWidthPercentage(100);
        headerTablePage2.setWidths(new int[] { 1, 1 });

        PdfPCell leftPage2 = new PdfPCell();
        leftPage2.setBorder(Rectangle.NO_BORDER);
        leftPage2.addElement(new Paragraph("CÔNG TY DỊCH VỤ MOBIFONE KHU VỰC 4", boldFont));
        leftPage2.addElement(new Paragraph("          " + user.getDepartmentName().toUpperCase(), boldFont));

        headerTablePage2.addCell(leftPage2);

        PdfPCell rightPage2 = new PdfPCell();
        rightPage2.setBorder(Rectangle.NO_BORDER);
        rightPage2.addElement(new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", boldFont));
        rightPage2.addElement(new Paragraph("            Độc lập - Tự do - Hạnh Phúc", boldFont));
        rightPage2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTablePage2.addCell(right);

        document.add(headerTablePage2);

        Paragraph titlePage2 = new Paragraph("BÁO CÁO TỔNG HỢP THỜI LƯỢNG, CHẤT LƯỢNG DỊCH VỤ HỖ TRỢ SXKD", boldFont);
        titlePage2.setAlignment(Element.ALIGN_CENTER);
        titlePage2.setSpacingBefore(10);
        titlePage2.setSpacingAfter(10);
        document.add(titlePage2);

        Paragraph titlePage22 = new Paragraph("Tháng " + attendance.getMonth() + " năm " + attendance.getYear(), boldFont);
        titlePage22.setAlignment(Element.ALIGN_CENTER);
        titlePage22.setSpacingBefore(10);
        titlePage22.setSpacingAfter(10);
        document.add(titlePage22);

        PdfPTable tablePage2 = new PdfPTable(totalColumns);
        tablePage2.setWidthPercentage(100);

        tablePage2.addCell(createCell("TT", 2, 1, boldFont));
        tablePage2.addCell(createCell("Mã NV", 2, 1, boldFont));
        tablePage2.addCell(createCell("Họ và tên", 2, 1, boldFont));
        tablePage2.addCell(createCell("Chức vụ", 2, 1, boldFont));
        tablePage2.addCell(createCell("Địa bàn", 2, 1, boldFont));

        if (attendance.getMonth() != null && attendance.getYear() != null) {
            if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                tablePage2.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 30, boldFont));
                tablePage2.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 30; i++) {
                    tablePage2.addCell(createCell(String.valueOf(i), font));
                }
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                tablePage2.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 29, boldFont));
                tablePage2.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 29; i++) {
                    tablePage2.addCell(createCell(String.valueOf(i), font));
                }
            } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                tablePage2.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 28, boldFont));
                tablePage2.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 28; i++) {
                    tablePage2.addCell(createCell(String.valueOf(i), font));
                }
            } else {
                tablePage2.addCell(createCell("Các ngày thực hiện dịch vụ trong tháng", 1, 31, boldFont));
                tablePage2.addCell(createCell("Số ngày thực hiện DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Chất lượng DV", 2, 1, boldFont));
                tablePage2.addCell(createCell("Ghi chú", 2, 1, boldFont));
                for (int i = 1; i <= 31; i++) {
                    tablePage2.addCell(createCell(String.valueOf(i), font));
                }
            }
        }

        tablePage2.setWidths(columnWidths);

        int sttPage2 = 1;

        for (AttendanceDetail detail : attendanceDetails) {
            tablePage2.addCell(createCell(String.valueOf(sttPage2), font));
            Employee employeeCheck = employeeRepository.findById(detail.getEmployeeId()).get();

            if (employeeCheck != null) {
                tablePage2.addCell(createCell(employeeCheck.getCodeEmployee() != null ? employeeCheck.getCodeEmployee() : "", font));
                tablePage2.addCell(createCell(employeeCheck.getName() != null ? employeeCheck.getName() : "", font));
                tablePage2.addCell(createCell(employeeCheck.getServiceTypeName() != null ? employeeCheck.getServiceTypeName() : "", font));
                tablePage2.addCell(createCell(employeeCheck.getDiaBan() != null ? employeeCheck.getDiaBan() : "", font));
            }
            tablePage2.addCell(createCell(detail.getDay1(), font));
            tablePage2.addCell(createCell(detail.getDay2(), font));
            tablePage2.addCell(createCell(detail.getDay3(), font));
            tablePage2.addCell(createCell(detail.getDay4(), font));
            tablePage2.addCell(createCell(detail.getDay5(), font));
            tablePage2.addCell(createCell(detail.getDay6(), font));
            tablePage2.addCell(createCell(detail.getDay7(), font));
            tablePage2.addCell(createCell(detail.getDay8(), font));
            tablePage2.addCell(createCell(detail.getDay9(), font));
            tablePage2.addCell(createCell(detail.getDay10(), font));
            tablePage2.addCell(createCell(detail.getDay11(), font));
            tablePage2.addCell(createCell(detail.getDay12(), font));
            tablePage2.addCell(createCell(detail.getDay13(), font));
            tablePage2.addCell(createCell(detail.getDay14(), font));
            tablePage2.addCell(createCell(detail.getDay15(), font));
            tablePage2.addCell(createCell(detail.getDay16(), font));
            tablePage2.addCell(createCell(detail.getDay17(), font));
            tablePage2.addCell(createCell(detail.getDay18(), font));
            tablePage2.addCell(createCell(detail.getDay19(), font));
            tablePage2.addCell(createCell(detail.getDay20(), font));
            tablePage2.addCell(createCell(detail.getDay21(), font));
            tablePage2.addCell(createCell(detail.getDay22(), font));
            tablePage2.addCell(createCell(detail.getDay23(), font));
            tablePage2.addCell(createCell(detail.getDay24(), font));
            tablePage2.addCell(createCell(detail.getDay25(), font));
            tablePage2.addCell(createCell(detail.getDay26(), font));
            tablePage2.addCell(createCell(detail.getDay27(), font));
            if (attendance.getMonth() != null && attendance.getYear() != null) {
                if (Arrays.stream(day30).anyMatch(day -> day == attendance.getMonth().intValue())) {
                    tablePage2.addCell(createCell(detail.getDay28(), font));
                    tablePage2.addCell(createCell(detail.getDay29(), font));
                    tablePage2.addCell(createCell(detail.getDay30(), font));
                } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 == 0) {
                    tablePage2.addCell(createCell(detail.getDay28(), font));
                    tablePage2.addCell(createCell(detail.getDay29(), font));
                } else if (attendance.getMonth().intValue() == 2 && attendance.getYear().intValue() % 4 != 0) {
                    tablePage2.addCell(createCell(detail.getDay28(), font));
                } else {
                    tablePage2.addCell(createCell(detail.getDay28(), font));
                    tablePage2.addCell(createCell(detail.getDay29(), font));
                    tablePage2.addCell(createCell(detail.getDay30(), font));
                    tablePage2.addCell(createCell(detail.getDay31(), font));
                }
            }
            tablePage2.addCell(createCell(detail.getPaidWorking().toString(), font));
            tablePage2.addCell(createCell("A", font));
            tablePage2.addCell(createCell("", font));
            sttPage2++;
        }

        document.add(tablePage2);
        Paragraph signPage2 = new Paragraph();
        signPage2.setAlignment(Element.ALIGN_CENTER);
        document.add(signPage2);

        // Tạo bảng có 2 cột, không có viền
        PdfPTable signTablePage2 = new PdfPTable(2);
        signTablePage2.setWidthPercentage(80);
        signTablePage2.setWidths(new float[] { 1, 1 });

        // Ô bên trái: Người lập bảng
        PdfPCell leftCellPage2 = new PdfPCell();
        leftCellPage2.setBorder(Rectangle.NO_BORDER);
        leftCellPage2.setHorizontalAlignment(Element.ALIGN_LEFT);
        leftCellPage2.addElement(new Paragraph("NGƯỜI LẬP BẢNG", boldFont));
        leftCellPage2.addElement(new Paragraph("  (Ký, ghi rõ họ tên)", font));

        // Ô bên phải: Ngày + Trưởng đơn vị
        PdfPCell rightCellPage2 = new PdfPCell();
        rightCellPage2.setBorder(Rectangle.NO_BORDER);
        rightCellPage2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCellPage2.addElement(new Paragraph("     , ngày     tháng     năm     ", font));
        rightCellPage2.addElement(new Paragraph("   TRƯỞNG ĐƠN VỊ", boldFont));
        rightCellPage2.addElement(new Paragraph("   (Ký, ghi rõ họ tên)", font));

        signTablePage2.addCell(leftCellPage2);
        signTablePage2.addCell(rightCellPage2);
        document.add(signTablePage2);
        document.close();
        return new ByteArrayResource(out.toByteArray());
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell createCell(String content, int rowspan, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setRowspan(rowspan);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
