package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Attendance.
 */
@Entity
@Table(name = "attendance")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "month")
    private Long month;

    @Column(name = "year")
    private Long year;

    @Column(name = "count")
    private Long count;

    @Column(name = "numberWorking")
    private BigDecimal numberWorking;

    @Column(name = "countNot")
    private Long countNot;

    @Column(name = "note")
    private String note;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Attendance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return this.employeeId;
    }

    public Attendance employeeId(Long employeeId) {
        this.setEmployeeId(employeeId);
        return this;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCountNot() {
        return countNot;
    }

    public void setCountNot(Long countNot) {
        this.countNot = countNot;
    }

    public String getNote() {
        return this.note;
    }

    public Attendance note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getNumberWorking() {
        return numberWorking;
    }

    public void setNumberWorking(BigDecimal numberWorking) {
        this.numberWorking = numberWorking;
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
            ", employeeId=" + getEmployeeId() +
            ", note='" + getNote() + "'" +
            "}";
    }
}
