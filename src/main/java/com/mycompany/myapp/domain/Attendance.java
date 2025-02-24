package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Attendance.
 */
@Entity
@Table(name = "attendance")
public class Attendance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "month")
    private Long month;

    @Column(name = "year")
    private Long year;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "attendance_employee",
        joinColumns = @JoinColumn(name = "attendance_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id")
    )
    private List<Employee> employees;

    @Column(name = "note")
    private String note;

    @Column(name = "departmentCode")
    private String departmentCode;

    @Column(name = "numberWork")
    private BigDecimal numberWork;

    @Column(name = "search_name")
    private String searchName;

    @Column(name = "search_nhom")
    private String searchNhom;

    @Column(name = "search_department")
    private String searchDepartment;

    @Column(name = "ngay_nghi")
    private String ngayNghi;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchNhom() {
        return searchNhom;
    }

    public void setSearchNhom(String searchNhom) {
        this.searchNhom = searchNhom;
    }

    public String getSearchDepartment() {
        return searchDepartment;
    }

    public void setSearchDepartment(String searchDepartment) {
        this.searchDepartment = searchDepartment;
    }

    public BigDecimal getNumberWork() {
        return numberWork;
    }

    public void setNumberWork(BigDecimal numberWork) {
        this.numberWork = numberWork;
    }

    public String getNgayNghi() {
        return ngayNghi;
    }

    public void setNgayNghi(String ngayNghi) {
        this.ngayNghi = ngayNghi;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attendance)) {
            return false;
        }
        return id != null && id.equals(((Attendance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Attendance{" +
            "id=" + getId() +
            ", note='" + getNote() + "'" +
            "}";
    }
}
