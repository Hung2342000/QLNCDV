package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class TransferService {

    private final Logger log = LoggerFactory.getLogger(TransferService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private CountEmployeeRepository countEmployeeRepository;
    private DepartmentRepository departmentRepository;
    private ServiceTypeRepository serviceTypeRepository;
    private TransferRepository transferRepository;
    private DiaBanRepository diaBanRepository;

    public TransferService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        CountEmployeeRepository countEmployeeRepository,
        DepartmentRepository departmentRepository,
        ServiceTypeRepository serviceTypeRepository,
        TransferRepository transferRepository,
        DiaBanRepository diaBanRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.countEmployeeRepository = countEmployeeRepository;
        this.departmentRepository = departmentRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.transferRepository = transferRepository;
        this.diaBanRepository = diaBanRepository;
    }

    public Transfer saveTransfer(Transfer transfer) {
        Employee employeeCheck = new Employee();
        if (transfer.getEmployeeId() != null) {
            employeeCheck = employeeRepository.findById(transfer.getEmployeeId()).get();
        }

        DiaBan diaBan = new DiaBan();
        if (transfer.getDiaBan() != null) {
            diaBan = diaBanRepository.findDiaBanByName(transfer.getDiaBan());
        }

        if (transfer.getRank() == null || transfer.getRank() == "") {
            transfer.setRank("trống");
        }

        ServiceType serviceTypeNew = new ServiceType();
        serviceTypeNew =
            serviceTypeRepository.findServiceTypeByServiceNameAndRegionRank(
                transfer.getServiceTypeName().toLowerCase(),
                diaBan.getVung().toLowerCase(),
                transfer.getRank().toLowerCase()
            );

        String note = "Từ ngày ";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        if (
            employeeCheck != null &&
            (
                !employeeCheck.getDepartment().equals(transfer.getDepartment()) ||
                !employeeCheck.getServiceType().equals(transfer.getServiceTypeName()) ||
                !employeeCheck.getDiaBan().equals(transfer.getDiaBan()) ||
                !employeeCheck.getStatus().equals(transfer.getStatus())
            )
        ) {
            LocalDate closeDate = LocalDate.of(2200, 12, 12);
            transfer.setCloseDate(closeDate);
            if (serviceTypeNew != null) {
                transfer.setNhom(serviceTypeNew.getNhom());
                transfer.setServiceTypeName(serviceTypeNew.getServiceName());
                transfer.setServiceType(serviceTypeNew.getId());
            }

            Transfer transferCheck = new Transfer();
            transferCheck = transferRepository.transferByCloseDateAndEmployeeId(transfer.getEmployeeId(), closeDate.toString());
            if (transferCheck != null) {
                transferCheck.setCloseDate(transfer.getStartDate());
                transfer.setDepartmentOld(transferCheck.getDepartment());
                transfer.setServiceTypeNameOld(transferCheck.getServiceTypeName());
                transfer.setDiaBanOld(transferCheck.getDiaBan());
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
            if (!employeeCheck.getServiceTypeName().equals(transfer.getServiceTypeName())) {
                if (serviceTypeNew != null) {
                    note = note + " điều chuyển " + employeeCheck.getServiceTypeName() + " thành " + serviceTypeNew.getServiceName() + ".";
                    employeeCheck.setServiceTypeName(serviceTypeNew.getServiceName());
                }
                employeeCheck.setNote(employeeCheck.getNote() != null ? employeeCheck.getNote() + " " + note : note);
            }
            if (!employeeCheck.getStatus().equals(transfer.getStatus())) {
                note = note + " Điều chuyển trạng thái " + employeeCheck.getStatus() + " thành " + transfer.getStatus() + ".";
                employeeCheck.setStatus(transfer.getStatus());
                employeeCheck.setNote(note);
            }
            if (!employeeCheck.getDiaBan().equals(transfer.getDiaBan())) {
                note = note + " Điều chuyển địa bàn " + employeeCheck.getDiaBan() + " thành " + transfer.getDiaBan() + ".";
                employeeCheck.setDiaBan(transfer.getDiaBan());
                employeeCheck.setNote(note);
            }

            employeeRepository.save(employeeCheck);
        }

        return transfer;
    }
}
