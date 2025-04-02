package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.BaoCao;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.service.dto.NhanVienDTO;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class BaoCaoService {

    private final Logger log = LoggerFactory.getLogger(BaoCaoService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private AttendanceRepository attendanceRepository;
    private AttendanceDetailRepository attendanceDetailRepository;
    private DepartmentRepository departmentRepository;
    private SalaryRepository salaryRepository;
    private final NgayNghiLeRepository ngayNghiLeRepository;

    public BaoCaoService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository,
        DepartmentRepository departmentRepository,
        SalaryRepository salaryRepository,
        NgayNghiLeRepository ngayNghiLeRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
        this.departmentRepository = departmentRepository;
        this.salaryRepository = salaryRepository;
        this.ngayNghiLeRepository = ngayNghiLeRepository;
    }

    public List<BaoCao> pageBaoCao(String searchNam, String searchNhom) {
        Long number = Long.parseLong(searchNam);
        List<BaoCao> baoCaoList = new ArrayList<>();

        List<Employee> employees = employeeRepository.listAllEmployeesNhom(searchNhom);
        for (Employee employee : employees) {
            List<NhanVienDTO> nhanVienDTOList = salaryRepository.getNhanVienDTO(number, searchNhom, employee.getId());
            if (nhanVienDTOList.size() > 0) {
                BaoCao baoCao = new BaoCao();
                baoCao.setCodeEmployee(nhanVienDTOList.get(0).getCodeEmployee());
                baoCao.setNameEmployee(nhanVienDTOList.get(0).getNameEmployee());
                baoCao.setServiceTypeName(nhanVienDTOList.get(0).getServiceTypeName());
                baoCao.setNhom(nhanVienDTOList.get(0).getNhom());
                baoCao.setNam(nhanVienDTOList.get(0).getNam());
                baoCao.setIdEmployee(nhanVienDTOList.get(0).getIdEmployee());
                double trungBinh = 0;
                for (NhanVienDTO nhanVienDTO : nhanVienDTOList) {
                    if (searchNhom.equals("KAM")) {
                        trungBinh = trungBinh + Double.parseDouble(nhanVienDTO.getHct()) / nhanVienDTOList.size();
                    } else trungBinh = trungBinh + Double.parseDouble(nhanVienDTO.getKpi()) / nhanVienDTOList.size();
                }
                baoCao.setKpiTrungBinh(Double.toString(trungBinh));
                Field[] fields = baoCao.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true); // Cho phép truy cập thuộc tính private
                    try {
                        // Kiểm tra kiểu dữ liệu và cập nhật giá trị
                        if (field.getName().contains("thang")) {
                            String thangcheck = field.getName().replaceAll("thang", "");
                            for (NhanVienDTO nhanVienDTO : nhanVienDTOList) {
                                if (nhanVienDTO.getThang().toString().equals(thangcheck)) {
                                    if (searchNhom.equals("KAM")) {
                                        field.set(baoCao, nhanVienDTO.getHct());
                                    } else {
                                        field.set(baoCao, nhanVienDTO.getKpi());
                                    }
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                baoCaoList.add(baoCao);
            }
        }

        return baoCaoList;
    }
}
