package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.service.dto.AdminUserDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
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
public class EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private CountEmployeeRepository countEmployeeRepository;
    private DepartmentRepository departmentRepository;
    private ServiceTypeRepository serviceTypeRepository;
    private TransferRepository transferRepository;
    private DiaBanRepository diaBanRepository;
    private UserService userService;

    public EmployeeService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        CountEmployeeRepository countEmployeeRepository,
        DepartmentRepository departmentRepository,
        ServiceTypeRepository serviceTypeRepository,
        TransferRepository transferRepository,
        DiaBanRepository diaBanRepository,
        UserService userService
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.countEmployeeRepository = countEmployeeRepository;
        this.departmentRepository = departmentRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.transferRepository = transferRepository;
        this.diaBanRepository = diaBanRepository;
        this.userService = userService;
    }

    public Page<Employee> getAllEmployees(
        Pageable pageable,
        String searchCode,
        String searchName,
        String searchDepartment,
        String searchNhom,
        String searchStatus,
        String searchService,
        String searchStartDate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        Page<Employee> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        if (searchStartDate.equals("")) {
            searchStartDate = "2200-12-01";
        }
        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            if (searchNhom.equals("") || searchNhom == null) {
                page =
                    employeeRepository.listAllEmployeesNoNhom(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        searchDepartment,
                        searchStatus,
                        searchService,
                        searchStartDate,
                        pageable
                    );
            } else page =
                employeeRepository.listAllEmployees(
                    searchCode.toLowerCase(),
                    searchName.toLowerCase(),
                    searchDepartment,
                    searchNhom,
                    searchStatus,
                    searchService,
                    searchStartDate,
                    pageable
                );
        } else if (
            authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            if (searchNhom.equals("") || searchNhom == null) {
                page =
                    employeeRepository.listAllEmployeesNoNhom(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        user.getDepartment(),
                        searchStatus,
                        searchService,
                        searchStartDate,
                        pageable
                    );
            } else page =
                employeeRepository.listAllEmployees(
                    searchCode.toLowerCase(),
                    searchName.toLowerCase(),
                    user.getDepartment(),
                    searchNhom,
                    searchStatus,
                    searchService,
                    searchStartDate,
                    pageable
                );
        } else if (
            authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority))
        ) {
            page = employeeRepository.listOneEmployee(user.getCode(), pageable);
        }
        return page;
    }

    public Page<Employee> getAllEmployeesBox(
        Pageable pageable,
        String searchCode,
        String searchName,
        String searchDepartment,
        String searchNhom
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        Page<Employee> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            if (searchNhom.equals("") || searchNhom == null) {
                page =
                    employeeRepository.listAllEmployeesNoNhomBox(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        searchDepartment,
                        pageable
                    );
            } else page =
                employeeRepository.listAllEmployeesBox(
                    searchCode.toLowerCase(),
                    searchName.toLowerCase(),
                    searchDepartment,
                    searchNhom,
                    pageable
                );
        } else if (
            authentication != null && !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            if (searchNhom.equals("") || searchNhom == null) {
                page =
                    employeeRepository.listAllEmployeesNoNhomBox(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        user.getDepartment(),
                        pageable
                    );
            } else page =
                employeeRepository.listAllEmployeesBox(
                    searchCode.toLowerCase(),
                    searchName.toLowerCase(),
                    user.getDepartment(),
                    searchNhom,
                    pageable
                );
        }
        return page;
    }

    public Employee saveEmployee(Employee employee) {
        Transfer transfer = new Transfer();
        ServiceType serviceType = new ServiceType();
        DiaBan diaBan = new DiaBan();
        if (employee.getDiaBan() != null) {
            diaBan = diaBanRepository.findDiaBanByName(employee.getDiaBan());
        }
        if (employee.getRank() == null || employee.getRank().equals("")) {
            employee.setRank("trống");
        }

        if (employee.getRank() != null && employee.getServiceTypeName() != null && diaBan.getVung() != null) {
            serviceType =
                serviceTypeRepository.findServiceTypeByServiceNameAndRegionRank(
                    employee.getServiceTypeName().toLowerCase(),
                    diaBan.getVung().toLowerCase(),
                    employee.getRank().toLowerCase()
                );
        }
        Department department = new Department();
        if (employee.getDepartment() != null) {
            department = departmentRepository.findDepartmentByCode(employee.getDepartment());
        }
        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setName(employee.getName());
        adminUserDTO.setCode(employee.getCodeEmployee());
        adminUserDTO.setEmail(employee.getWorkEmail());
        adminUserDTO.setContact(employee.getWorkPhone());
        adminUserDTO.setDepartment(employee.getDepartment());
        Set<String> authorities = new HashSet<>();
        authorities.add("ROLE_USER");
        adminUserDTO.setAuthorities(authorities);
        adminUserDTO.setDepartmentName(department.getName());
        adminUserDTO.setLogin(employee.getWorkEmail());
        if (adminUserDTO.getLogin() != null) {
            userService.createUser(adminUserDTO);
        }
        if (serviceType != null) {
            transfer.setServiceType(serviceType.getId());
            transfer.setNhom(serviceType.getNhom());
            employee.setServiceType(serviceType.getId());
            if (serviceType.getMucChiTraToiThieu() != null) {
                employee.setMucChiTraToiThieu(serviceType.getMucChiTraToiThieu());
            }
            if (serviceType.getBasicSalary() != null) {
                employee.setBasicSalary(serviceType.getBasicSalary());
            }
            if (serviceType.getRank() != null) {
                employee.setRank(serviceType.getRank());
            }
            if (serviceType.getRegion() != null) {
                employee.setRegion(serviceType.getRegion());
            }
            if (serviceType.getNhom() != null) {
                employee.setNhom(serviceType.getNhom());
            }
            if (serviceType.getServiceName() != null) {
                employee.setServiceTypeName(serviceType.getServiceName());
                transfer.setServiceTypeName(serviceType.getServiceName());
            }
            if (employee.getStatus() == null) {
                employee.setStatus("Đang làm việc");
            }
        }
        if (employee.getCloseDate() != null) {
            employee.setStatus("Nghỉ việc");
        }
        LocalDate closeDate = LocalDate.of(2200, 12, 12);
        transfer.setCloseDate(closeDate);
        Employee employeeSave = new Employee();
        employeeSave = employeeRepository.save(employee);
        if (employeeSave.getId() != null) {
            transfer.setEmployeeId(employeeSave.getId());
        }
        if (employee.getStatus() != null) {
            transfer.setStatus(employee.getStatus());
        }
        if (employee.getDepartment() != null) {
            transfer.setDepartment(employee.getDepartment());
        }
        if (employee.getStartDate() != null) {
            transfer.setStartDate(employee.getStartDate());
        }
        if (employee.getDiaBan() != null) {
            transfer.setDiaBan(employee.getDiaBan());
        }
        transferRepository.save(transfer);
        return employeeSave;
    }

    public Employee saveEmployeePut(Employee employee) {
        Employee employeeCheck = new Employee();
        employeeCheck = employeeRepository.findById(employee.getId()).get();

        ServiceType serviceType = new ServiceType();
        DiaBan diaBan = new DiaBan();
        if (employee.getDiaBan() != null) {
            diaBan = diaBanRepository.findDiaBanByName(employee.getDiaBan());
        }

        if (employee.getRank() != null && employee.getServiceTypeName() != null && diaBan.getVung() != null) {
            serviceType =
                serviceTypeRepository.findServiceTypeByServiceNameAndRegionRank(
                    employee.getServiceTypeName().toLowerCase(),
                    diaBan.getVung().toLowerCase(),
                    employee.getRank().toLowerCase()
                );
        }

        if (serviceType.getId() != null) {
            employee.setServiceType(serviceType.getId());
        }

        Transfer transferCheck = transferRepository.transferByCloseDateAndEmployeeId(employee.getId(), "2200-12-12");
        if (transferCheck != null) {
            if (
                employeeCheck != null &&
                (
                    !employeeCheck.getDepartment().equals(employee.getDepartment()) ||
                    !employeeCheck.getServiceType().equals(employee.getServiceType()) ||
                    !employeeCheck.getStatus().equals(employee.getStatus()) ||
                    !employeeCheck.getDiaBan().equals(employee.getDiaBan()) ||
                    !employeeCheck.getStartDate().equals(employee.getStartDate())
                )
            ) {
                if (employee.getStatus() != null) {
                    transferCheck.setStatus(employee.getStatus());
                }
                if (employee.getDepartment() != null) {
                    transferCheck.setDepartment(employee.getDepartment());
                }
                if (serviceType != null) {
                    if (serviceType.getNhom() != null) {
                        transferCheck.setNhom(serviceType.getNhom());
                    }
                    if (serviceType.getServiceName() != null) {
                        transferCheck.setServiceTypeName(serviceType.getServiceName());
                    }
                    if (serviceType.getId() != null) {
                        transferCheck.setServiceType(serviceType.getId());
                    }
                }
                if (employee.getDiaBan() != null) {
                    transferCheck.setDiaBan(employee.getDiaBan());
                }

                transferCheck.setStartDate(employee.getStartDate());
                transferRepository.save(transferCheck);
            }
        }

        if (serviceType != null) {
            transferCheck.setServiceTypeName(serviceType.getServiceName());
            if (serviceType.getMucChiTraToiThieu() != null) {
                employee.setMucChiTraToiThieu(serviceType.getMucChiTraToiThieu());
            }
            if (serviceType.getBasicSalary() != null) {
                employee.setBasicSalary(serviceType.getBasicSalary());
            }
            if (serviceType.getRank() != null) {
                employee.setRank(serviceType.getRank());
            }
            if (serviceType.getRegion() != null) {
                employee.setRegion(serviceType.getRegion());
            }
            if (serviceType.getNhom() != null) {
                employee.setNhom(serviceType.getNhom());
            }
            if (serviceType.getServiceName() != null) {
                employee.setServiceTypeName(serviceType.getServiceName());
            }
            if (employee.getStatus() == null) {
                employee.setStatus("Đang làm việc");
            }
        }
        if (employee.getCloseDate() != null) {
            employee.setStatus("Nghỉ việc");
        }
        if (employeeCheck != null) {
            employee.setNote(employeeCheck.getNote());
        }
        Employee employeeSave = employeeRepository.save(employee);
        return employeeSave;
    }

    public List<Employee> getAllEmployeeNoPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        List<Employee> employeeList = new ArrayList<>();
        if (
            authentication != null &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(USER).contains(authority)) &&
            getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority)) &&
            !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            employeeList = employeeRepository.findAll();
        } else if (
            authentication != null && !getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))
        ) {
            employeeList = employeeRepository.listAllEmployeesDepartmentNoPage(user.getDepartment());
        }
        return employeeList;
    }

    public byte[] export(
        String searchCode,
        String searchName,
        String searchDepartment,
        String searchNhom,
        String searchStatus,
        String searchService,
        String searchStartDate
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        if (searchStartDate.equals("")) {
            searchStartDate = "2200-12-01";
        }
        List<Employee> employeeList = new ArrayList<>();
        if (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))) {
            if (searchNhom.equals("") || searchNhom == null) {
                employeeList =
                    employeeRepository.listAllEmployeesExportNoNhom(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        searchDepartment,
                        searchStatus,
                        searchService,
                        searchStartDate
                    );
            } else {
                employeeList =
                    employeeRepository.listAllEmployeesExport(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        searchDepartment,
                        searchNhom,
                        searchStatus,
                        searchService,
                        searchStartDate
                    );
            }
        } else if (
            authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(SUPERUSER).contains(authority))
        ) {
            if (searchNhom.equals("") || searchNhom == null) {
                employeeList =
                    employeeRepository.listAllEmployeesExportNoNhom(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        user.getDepartment(),
                        searchStatus,
                        searchService,
                        searchStartDate
                    );
            } else {
                employeeList =
                    employeeRepository.listAllEmployeesExport(
                        searchCode.toLowerCase(),
                        searchName.toLowerCase(),
                        user.getDepartment(),
                        searchNhom,
                        searchStatus,
                        searchService,
                        searchStartDate
                    );
            }
        } else {
            employeeList = employeeRepository.listOne(user.getCode());
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("nhanvien");
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

        String[] headers = {
            "STT",
            "Mã nhân viên",
            "Họ và tên",
            "Ngày sinh",
            "Căn cước công dân",
            "Số điện thoại cá nhân",
            "Số điện thoại công việc",
            "Email cá nhân",
            "Email công việc",
            "Địa chỉ",
            "Ngày vào làm việc",
            "Đơn vị",
            "Loại hình dịch vụ",
            "Vùng",
            "Cấp",
            "Lương cơ bản",
            "Trạng thái",
        };
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
        cellTableName.setCellValue("Danh sách lao động dịch vụ hỗ trợ sản xuất kinh doanh Công ty Dịch vụ MobiFone Khu vực 4");
        cellTableName.setCellStyle(tableName);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 10));
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);

        Row rowNgayXuat = sheet.createRow(4);
        Cell cellNgayXuat = rowNgayXuat.createCell(0);
        if (!searchStartDate.equals("2200-12-01") && searchStartDate != null) {
            LocalDate localDate = LocalDate.parse(searchStartDate);

            cellNgayXuat.setCellValue("Thời điểm thống kê: " + localDate.format(formatter));
        } else cellNgayXuat.setCellValue("Thời điểm thống kê: " + formattedDate);

        cellNgayXuat.setCellStyle(tableName);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 10));

        int rowNum = 8;
        Row headerRow = sheet.createRow(rowNum++);

        for (int i = 0; i < headers.length; i++) {
            if (i == 13 || i == 0 || i == 14) {
                sheet.setColumnWidth(i, 2000);
            } else if (i == 11 || i == 12 || i == 9 || i == 2) {
                sheet.setColumnWidth(i, 7000);
            } else sheet.setColumnWidth(i, 5000);
            headerRow.setHeight((short) 800);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(centeredBoldStyle);
        }

        int stt = 1;
        for (Employee rowData : employeeList) {
            Department department = new Department();
            if (rowData.getDepartment() != null) {
                department = departmentRepository.findDepartmentByCode(rowData.getDepartment());
            }
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(stt);
            cell0.setCellStyle(detailStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(rowData.getCodeEmployee());
            cell1.setCellStyle(detailStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(rowData.getName());
            cell2.setCellStyle(detailStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(rowData.getBirthday() != null ? rowData.getBirthday().format(formatter) : "");
            cell3.setCellStyle(detailStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(rowData.getOtherId());
            cell4.setCellStyle(detailStyle);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(rowData.getMobilePhone() != null ? rowData.getMobilePhone().toString() : "");
            cell5.setCellStyle(detailStyle);

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(rowData.getWorkPhone() != null ? rowData.getWorkPhone().toString() : "");
            cell6.setCellStyle(detailStyle);

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(rowData.getPrivateEmail());
            cell7.setCellStyle(detailStyle);

            Cell cell8 = row.createCell(8);
            cell8.setCellValue(rowData.getWorkEmail());
            cell8.setCellStyle(detailStyle);

            Cell cell9 = row.createCell(9);
            cell9.setCellValue(rowData.getAddress());
            cell9.setCellStyle(detailStyle);

            Cell cell10 = row.createCell(10);
            cell10.setCellValue(rowData.getStartDate() != null ? rowData.getStartDate().format(formatter) : "");
            cell10.setCellStyle(detailStyle);

            Cell cell11 = row.createCell(11);
            cell11.setCellValue(department != null ? department.getName() : "");
            cell11.setCellStyle(detailStyle);

            Cell cell12 = row.createCell(12);
            cell12.setCellValue(rowData.getServiceTypeName() != null ? rowData.getServiceTypeName() : "");
            cell12.setCellStyle(detailStyle);

            Cell cell13 = row.createCell(13);
            cell13.setCellValue(rowData.getRegion() != null ? rowData.getRegion() : "");
            cell13.setCellStyle(detailStyle);

            Cell cell14 = row.createCell(14);
            cell14.setCellValue(rowData.getRank() != null ? rowData.getRank() : "");
            cell14.setCellStyle(detailStyle);

            Cell cell15 = row.createCell(15);
            if (rowData.getBasicSalary() != null) {
                cell15.setCellValue(rowData.getBasicSalary().doubleValue());
            } else cell15.setCellValue("");
            cell15.setCellStyle(detailStyle);

            Cell cell16 = row.createCell(16);
            cell16.setCellValue(rowData.getStatus() != null ? rowData.getStatus() : "");
            cell16.setCellStyle(detailStyle);
            stt++;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();
        return excelBytes;
    }

    public List<CountEmployee> getAllCountEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        List<CountEmployee> employeeList = countEmployeeRepository.listAllCountEmployee();
        List<Department> listDepartment = new ArrayList<>();
        listDepartment = this.departmentRepository.findAll();
        if (employeeList.size() > 0) {
            for (Department dp : listDepartment) {
                if (!employeeList.stream().anyMatch(em -> em.getCode().equals(dp.getName()))) {
                    CountEmployee countEmployee = new CountEmployee();
                    countEmployee.setCountEmployee((long) 0);
                    countEmployee.setCode(dp.getName());
                    employeeList.add(countEmployee);
                }
            }
        }
        for (CountEmployee countEmployee : employeeList) {
            if (countEmployee.getCode().equals(user.getDepartmentName())) {
                countEmployee.setDepartment(true);
                break;
            }
        }
        return employeeList;
    }

    public List<CountEmployee> getAllCountEmployeeByNhom(String startDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        Long countDLV;
        List<CountEmployee> employeeList = new ArrayList<>();
        if (startDate.equals("")) {
            startDate = "2200-12-01";
        }
        if (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))) {
            if (startDate != null && startDate != "") {
                employeeList = countEmployeeRepository.listAllCountEmployeeByNhomStartDate(startDate, "");
            } else employeeList = countEmployeeRepository.listAllCountEmployeeByNhom("");
            countDLV = employeeRepository.countEmployeeDLV();
        } else {
            if (startDate != null && startDate != "") {
                employeeList = countEmployeeRepository.listAllCountEmployeeByNhomStartDate(startDate, user.getDepartment());
            } else employeeList = countEmployeeRepository.listAllCountEmployeeByNhom(user.getDepartment());
            countDLV = employeeRepository.countEmployeeDLVDepartment(user.getDepartment());
        }
        CountEmployee employee = new CountEmployee();
        employee.setCode("Đang làm việc");
        employee.setCountEmployee(countDLV);
        employeeList.add(employee);
        return employeeList;
    }

    public List<CountEmployee> getAllCountEmployeeByStatus(String startDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        User user = new User();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        username = userDetails.getUsername();
        user = userRepository.findOneByLogin(username).get();
        List<CountEmployee> employeeList = new ArrayList<>();

        if (startDate.equals("")) {
            startDate = "2200-12-01";
        }

        if (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(ADMIN).contains(authority))) {
            if (startDate != null && startDate != "") {
                employeeList = countEmployeeRepository.listAllCountEmployeeByStatusStartDate(startDate, "");
            } else employeeList = countEmployeeRepository.listAllCountEmployeeByStatus("");
        } else {
            if (startDate != null && startDate != "") {
                employeeList = countEmployeeRepository.listAllCountEmployeeByStatusStartDate(startDate, user.getDepartment());
            } else employeeList = countEmployeeRepository.listAllCountEmployeeByStatus(user.getDepartment());
        }
        return employeeList;
    }

    public List<Employee> importEmployee(List<Employee> employeeList) {
        List<Employee> employees = new ArrayList<>();
        if (employeeList.size() > 0) {
            for (Employee employee : employeeList) {
                employee.setRank(employee.getRank() != null ? employee.getRank() : "");
                ServiceType serviceType = new ServiceType();
                Department department = new Department();
                DiaBan diaBan = new DiaBan();
                if (employee.getDiaBan() != null) {
                    diaBan = diaBanRepository.findDiaBanByName(employee.getDiaBan());
                }
                if (employee.getDepartment() != null) {
                    department = departmentRepository.findDepartmentByName(employee.getDepartment().toLowerCase());
                }
                if (department != null && department.getId() != null) {
                    employee.setDepartment(department.getCode());
                } else {
                    employee.setDepartment(" ");
                }
                if (employee.getRank() != null && employee.getServiceTypeName() != null && diaBan.getVung() != null) {
                    log.debug("loiiiii : {}", employee.getDiaBan());
                    serviceType =
                        serviceTypeRepository.findServiceTypeByServiceNameAndRegionRank(
                            employee.getServiceTypeName().toLowerCase(),
                            diaBan.getVung().toLowerCase(),
                            employee.getRank().toLowerCase()
                        );
                }
                if (serviceType != null && serviceType.getId() != null) {
                    employee.setNhom(serviceType.getNhom());
                    employee.setServiceType(serviceType.getId());
                    employee.setRegion(serviceType.getRegion());
                    employee.setServiceType(serviceType.getId());
                    employee.setRank(serviceType.getRank());
                    employee.setBasicSalary(serviceType.getBasicSalary());
                    employee.setMucChiTraToiThieu(serviceType.getMucChiTraToiThieu());
                } else {
                    // check null khi tim kiem
                    employee.setNhom(" ");
                }
                if (employee.getStatus() == null || employee.getStatus().equals("")) {
                    employee.setStatus("Đang làm việc");
                }
                AdminUserDTO adminUserDTO = new AdminUserDTO();
                adminUserDTO.setName(employee.getName());
                adminUserDTO.setEmail(employee.getWorkEmail());
                adminUserDTO.setContact(employee.getWorkPhone());
                adminUserDTO.setDepartment(employee.getDepartment());
                adminUserDTO.setCode(employee.getCodeEmployee());
                Set<String> authorities = new HashSet<>();
                authorities.add("ROLE_USER");
                adminUserDTO.setAuthorities(authorities);
                adminUserDTO.setDepartmentName(department.getName());
                adminUserDTO.setLogin(employee.getWorkEmail());
                if (adminUserDTO.getLogin() != null) {
                    userService.createUser(adminUserDTO);
                }
                Employee employeeCheck = new Employee();
                employeeCheck = employeeRepository.getByCode(employee.getCodeEmployee());
                if (employeeCheck == null) {
                    employees.add(employee);
                }
            }
        }

        List<Employee> employeeListSave = employeeRepository.saveAll(employees);
        for (Employee employee : employeeListSave) {
            Transfer transfer = new Transfer();
            LocalDate closeDate = LocalDate.of(2200, 12, 12);
            transfer.setCloseDate(closeDate);
            if (employee.getId() != null) {
                transfer.setEmployeeId(employee.getId());
            }
            if (employee.getServiceType() != null) {
                transfer.setServiceType(employee.getServiceType());
            }
            if (employee.getStatus() != null) {
                transfer.setStatus(employee.getStatus());
            }
            if (employee.getDepartment() != null) {
                transfer.setDepartment(employee.getDepartment());
            }
            if (employee.getStartDate() != null) {
                transfer.setStartDate(employee.getStartDate());
            }
            if (employee.getNhom() != null) {
                transfer.setNhom(employee.getNhom());
            }
            if (employee.getServiceTypeName() != null) {
                transfer.setServiceTypeName(employee.getServiceTypeName());
            }
            if (employee.getDiaBan() != null) {
                transfer.setDiaBan(employee.getDiaBan());
            }
            transferRepository.save(transfer);
        }
        return employeeListSave;
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
        List<Transfer> transfers = transferRepository.transferByEmployeeId(id);
        if (transfers != null && transfers.size() > 0) {
            transferRepository.deleteAllById(transfers.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
    }
}
