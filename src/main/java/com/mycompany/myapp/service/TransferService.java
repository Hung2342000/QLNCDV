package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.AuthoritiesConstants.*;
import static com.mycompany.myapp.security.SecurityUtils.getAuthorities;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TransferService {

    private final Logger log = LoggerFactory.getLogger(TransferService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private CountEmployeeRepository countEmployeeRepository;
    private DepartmentRepository departmentRepository;
    private ServiceTypeRepository serviceTypeRepository;
    private TransferRepository transferRepository;

    public TransferService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        CountEmployeeRepository countEmployeeRepository,
        DepartmentRepository departmentRepository,
        ServiceTypeRepository serviceTypeRepository,
        TransferRepository transferRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.countEmployeeRepository = countEmployeeRepository;
        this.departmentRepository = departmentRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.transferRepository = transferRepository;
    }

    public Transfer saveTransfer(Transfer transfer) {
        ServiceType serviceTypeNew = new ServiceType();
        serviceTypeNew = serviceTypeRepository.findbyId(transfer.getServiceType());

        Employee employeeCheck = new Employee();
        if (transfer.getEmployeeId() != null) {
            employeeCheck = employeeRepository.findById(transfer.getEmployeeId()).get();
        }

        String note = "Từ ngày ";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        if (
            employeeCheck != null &&
            (
                !employeeCheck.getDepartment().equals(transfer.getDepartment()) ||
                !employeeCheck.getServiceType().equals(transfer.getServiceType()) ||
                !employeeCheck.getStatus().equals(transfer.getStatus())
            )
        ) {
            LocalDate closeDate = LocalDate.of(2200, 12, 12);
            transfer.setCloseDate(closeDate);
            if (serviceTypeNew != null) {
                transfer.setNhom(serviceTypeNew.getNhom());
                transfer.setServiceTypeName(serviceTypeNew.getServiceName());
            }

            Transfer transferCheck = new Transfer();
            transferCheck = transferRepository.transferByCloseDateAndEmployeeId(transfer.getEmployeeId(), closeDate.toString());
            if (transferCheck != null) {
                transferCheck.setCloseDate(transfer.getStartDate());
                transfer.setDepartmentOld(transferCheck.getDepartment());
                transfer.setServiceTypeOld(transferCheck.getServiceType());
                transfer.setStatusOld(transferCheck.getStatus());
                transferRepository.save(transferCheck);
            }
            transferRepository.save(transfer);

            String formattedDate = today.format(formatter);
            if (transfer.getStartDate() != null) {
                employeeCheck.setNgayDieuChuyen(transfer.getStartDate());
                note = note + transfer.getStartDate().format(formatter) + " ";
            } else note = note + formattedDate + " ";

            if (!employeeCheck.getDepartment().equals(transfer.getDepartment())) {
                String nameDepartmentCheck = departmentRepository.getDepartmentName(employeeCheck.getDepartment());
                String nameDepartment = departmentRepository.getDepartmentName(transfer.getDepartment());
                note = note + " điều chuyển " + nameDepartmentCheck + " đến " + nameDepartment + ".";
                employeeCheck.setDepartment(transfer.getDepartment());
                employeeCheck.setNote(employeeCheck.getNote() != null ? employeeCheck.getNote() + " " + note : note);
            }
            if (!employeeCheck.getServiceType().equals(transfer.getServiceType())) {
                employeeCheck.setServiceType(transfer.getServiceType());
                if (serviceTypeNew != null) {
                    employeeCheck.setServiceTypeName(serviceTypeNew.getServiceName());
                    note = note + " điều chuyển " + employeeCheck.getServiceTypeName() + " thành " + serviceTypeNew.getServiceName() + ".";
                }
                employeeCheck.setNote(employeeCheck.getNote() != null ? employeeCheck.getNote() + " " + note : note);
            }
            if (!employeeCheck.getStatus().equals(transfer.getStatus())) {
                note = note + " Điều chuyển trạng thái " + employeeCheck.getStatus() + " thành " + transfer.getStatus() + ".";
                employeeCheck.setStatus(transfer.getStatus());
                employeeCheck.setNote(note);
            }

            employeeRepository.save(employeeCheck);
        }

        return transfer;
    }
}
