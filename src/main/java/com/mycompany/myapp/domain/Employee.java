package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "code_employee", nullable = false)
    private String codeEmployee;

    @Column(name = "name")
    private String name;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "other_id")
    private String otherId;

    @Column(name = "address")
    private String address;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "work_phone")
    private String workPhone;

    @Column(name = "work_email")
    private String workEmail;

    @Column(name = "private_email")
    private String privateEmail;

    @Column(name = "department")
    private String department;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @Column(name = "basic_salary")
    private BigDecimal basicSalary;

    @Column(name = "service_type")
    private Long serviceType;

    @Column(name = "service_type_name")
    private String serviceTypeName;

    @Column(name = "region")
    private String region;

    @Column(name = "rank")
    private String rank;

    @Column(name = "muc_chi_tra_toi_thieu")
    private BigDecimal mucChiTraToiThieu;

    @Column(name = "nhom")
    private String nhom;

    @Column(name = "dia_ban")
    private String diaBan;

    @Column(name = "status")
    private String status;

    @Column(name = "note")
    private String note;

    @Column(name = "ngay_nghi_sinh")
    private LocalDate ngayNghiSinh;

    @Column(name = "ngay_dieu_chuyen")
    private LocalDate ngayDieuChuyen;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Employee() {}

    public Employee(
        Long id,
        String codeEmployee,
        String name,
        LocalDate birthday,
        String otherId,
        String address,
        String mobilePhone,
        String workPhone,
        String workEmail,
        String privateEmail,
        String department,
        LocalDate startDate,
        LocalDate closeDate,
        BigDecimal basicSalary,
        Long serviceType,
        String serviceTypeName,
        String region,
        String rank,
        BigDecimal mucChiTraToiThieu,
        String nhom,
        String diaBan,
        String status,
        String note,
        LocalDate ngayNghiSinh,
        LocalDate ngayDieuChuyen
    ) {
        this.id = id;
        this.codeEmployee = codeEmployee;
        this.name = name;
        this.birthday = birthday;
        this.otherId = otherId;
        this.address = address;
        this.mobilePhone = mobilePhone;
        this.workPhone = workPhone;
        this.workEmail = workEmail;
        this.privateEmail = privateEmail;
        this.department = department;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.basicSalary = basicSalary;
        this.serviceType = serviceType;
        this.serviceTypeName = serviceTypeName;
        this.region = region;
        this.rank = rank;
        this.mucChiTraToiThieu = mucChiTraToiThieu;
        this.nhom = nhom;
        this.diaBan = diaBan;
        this.status = status;
        this.note = note;
        this.ngayNghiSinh = ngayNghiSinh;
        this.ngayDieuChuyen = ngayDieuChuyen;
    }

    public Employee(
        Long id,
        String codeEmployee,
        String name,
        String department,
        LocalDate startDate,
        LocalDate closeDate,
        Long serviceType,
        String serviceTypeName,
        String nhom,
        String status
    ) {
        this.id = id;
        this.codeEmployee = codeEmployee;
        this.name = name;
        this.department = department;
        this.startDate = startDate;
        this.closeDate = closeDate;
        this.serviceType = serviceType;
        this.serviceTypeName = serviceTypeName;
        this.nhom = nhom;
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public Employee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeEmployee() {
        return codeEmployee;
    }

    public void setCodeEmployee(String codeEmployee) {
        this.codeEmployee = codeEmployee;
    }

    public Employee codeEmployee(String codeEmployee) {
        this.setCodeEmployee(codeEmployee);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Employee name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return this.birthday;
    }

    public Employee birthday(LocalDate birthday) {
        this.setBirthday(birthday);
        return this;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getOtherId() {
        return this.otherId;
    }

    public Employee otherId(String otherId) {
        this.setOtherId(otherId);
        return this;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getAddress() {
        return this.address;
    }

    public Employee address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobilePhone() {
        return this.mobilePhone;
    }

    public Employee mobilePhone(String mobilePhone) {
        this.setMobilePhone(mobilePhone);
        return this;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkPhone() {
        return this.workPhone;
    }

    public Employee workPhone(String workPhone) {
        this.setWorkPhone(workPhone);
        return this;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getWorkEmail() {
        return this.workEmail;
    }

    public Employee workEmail(String workEmail) {
        this.setWorkEmail(workEmail);
        return this;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getPrivateEmail() {
        return this.privateEmail;
    }

    public Employee privateEmail(String privateEmail) {
        this.setPrivateEmail(privateEmail);
        return this;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }

    public String getDepartment() {
        return this.department;
    }

    public Employee department(String department) {
        this.setDepartment(department);
        return this;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Employee startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Long getServiceType() {
        return serviceType;
    }

    public void setServiceType(Long serviceType) {
        this.serviceType = serviceType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public BigDecimal getMucChiTraToiThieu() {
        return mucChiTraToiThieu;
    }

    public void setMucChiTraToiThieu(BigDecimal mucChiTraToiThieu) {
        this.mucChiTraToiThieu = mucChiTraToiThieu;
    }

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public String getDiaBan() {
        return diaBan;
    }

    public void setDiaBan(String diaBan) {
        this.diaBan = diaBan;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getNgayNghiSinh() {
        return ngayNghiSinh;
    }

    public void setNgayNghiSinh(LocalDate ngayNghiSinh) {
        this.ngayNghiSinh = ngayNghiSinh;
    }

    public LocalDate getNgayDieuChuyen() {
        return ngayDieuChuyen;
    }

    public void setNgayDieuChuyen(LocalDate ngayDieuChuyen) {
        this.ngayDieuChuyen = ngayDieuChuyen;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return id != null && id.equals(((Employee) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", codeEmployee=" + getCodeEmployee() +
            ", name='" + getName() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", otherId='" + getOtherId() + "'" +
            ", address='" + getAddress() + "'" +
            ", mobilePhone='" + getMobilePhone() + "'" +
            ", workPhone='" + getWorkPhone() + "'" +
            ", workEmail='" + getWorkEmail() + "'" +
            ", privateEmail='" + getPrivateEmail() + "'" +
            ", department='" + getDepartment() + "'" +
            ", startDate='" + getStartDate() + "'" +
            "}";
    }
}
