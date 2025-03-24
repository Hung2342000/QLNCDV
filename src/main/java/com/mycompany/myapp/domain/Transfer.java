package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "transfer")
public class Transfer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @Column(name = "service_type_name")
    private String serviceTypeName;

    @Column(name = "service_type")
    private Long serviceType;

    @Column(name = "nhom")
    private String nhom;

    @Column(name = "service_type_name_old")
    private String serviceTypeNameOld;

    @Column(name = "department")
    private String department;

    @Column(name = "department_old")
    private String departmentOld;

    @Column(name = "status")
    private String status;

    @Column(name = "status_old")
    private String statusOld;

    @Column(name = "diaBan")
    private String diaBan;

    @Column(name = "dia_ban_old")
    private String diaBanOld;

    @Column(name = "rank")
    private String rank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transfer id(Long id) {
        this.setId(id);
        return this;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public String getServiceTypeNameOld() {
        return serviceTypeNameOld;
    }

    public void setServiceTypeNameOld(String serviceTypeNameOld) {
        this.serviceTypeNameOld = serviceTypeNameOld;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentOld() {
        return departmentOld;
    }

    public void setDepartmentOld(String departmentOld) {
        this.departmentOld = departmentOld;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusOld() {
        return statusOld;
    }

    public void setStatusOld(String statusOld) {
        this.statusOld = statusOld;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    public Long getServiceType() {
        return serviceType;
    }

    public void setServiceType(Long serviceType) {
        this.serviceType = serviceType;
    }

    public String getDiaBan() {
        return diaBan;
    }

    public void setDiaBan(String diaBan) {
        this.diaBan = diaBan;
    }

    public String getDiaBanOld() {
        return diaBanOld;
    }

    public void setDiaBanOld(String diaBanOld) {
        this.diaBanOld = diaBanOld;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof com.mycompany.myapp.domain.Transfer)) {
            return false;
        }
        return id != null && id.equals(((com.mycompany.myapp.domain.Transfer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transfer{" +
            "id=" + getId() +
            "}";
    }
}
