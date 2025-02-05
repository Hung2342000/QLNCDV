package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A salary.
 */
@Entity
@Table(name = "salary")
public class Salary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "createDate")
    private LocalDate createDate;

    @Column(name = "nameSalary")
    private String nameSalary;

    @Column(name = "month")
    private Long month;

    @Column(name = "year")
    private Long year;

    @Column(name = "numberWork")
    private BigDecimal numberWork;

    @Column(name = "attendanceId")
    private Long attendanceId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "salary_employee",
        joinColumns = @JoinColumn(name = "salary_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id")
    )
    private List<Employee> employees;

    @Column(name = "isAttendance", nullable = false)
    private boolean isAttendance;

    @Column(name = "search_name")
    private String searchName;

    @Column(name = "search_nhom")
    private String searchNhom;

    @Column(name = "search_department")
    private String searchDepartment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getNameSalary() {
        return nameSalary;
    }

    public void setNameSalary(String nameSalary) {
        this.nameSalary = nameSalary;
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

    public BigDecimal getNumberWork() {
        return numberWork;
    }

    public void setNumberWork(BigDecimal numberWork) {
        this.numberWork = numberWork;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public boolean getIsAttendance() {
        return isAttendance;
    }

    public void setIsAttendance(boolean attendance) {
        isAttendance = attendance;
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Salary)) {
            return false;
        }
        return id != null && id.equals(((Salary) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Salary{" +
            "id=" + getId() +
            "nameSalary=" + getNameSalary() +
            "}";
    }
}
