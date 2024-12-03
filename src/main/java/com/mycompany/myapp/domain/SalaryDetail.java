package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

/**
 * A salaryDetail.
 */
@Entity
@Table(name = "salary_detail")
public class SalaryDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "salaryId")
    private Long salaryId;

    @Column(name = "employeeId")
    private Long employeeId;

    @Column(name = "basicSalary")
    private BigDecimal basicSalary;

    @Column(name = "numberWorking")
    private BigDecimal numberWorking;

    @Column(name = "numberWorkInMonth")
    private BigDecimal numberWorkInMonth;

    @Column(name = "allowance")
    private BigDecimal allowance;

    @Column(name = "incentiveSalary")
    private BigDecimal incentiveSalary;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "note")
    private String note;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getNumberWorking() {
        return numberWorking;
    }

    public void setNumberWorking(BigDecimal numberWorking) {
        this.numberWorking = numberWorking;
    }

    public BigDecimal getAllowance() {
        return allowance;
    }

    public void setAllowance(BigDecimal allowance) {
        this.allowance = allowance;
    }

    public BigDecimal getIncentiveSalary() {
        return incentiveSalary;
    }

    public void setIncentiveSalary(BigDecimal incentiveSalary) {
        this.incentiveSalary = incentiveSalary;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getNumberWorkInMonth() {
        return numberWorkInMonth;
    }

    public void setNumberWorkInMonth(BigDecimal numberWorkInMonth) {
        this.numberWorkInMonth = numberWorkInMonth;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalaryDetail)) {
            return false;
        }
        return id != null && id.equals(((SalaryDetail) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalaryDetail{" +
            "id=" + getId() +
            "}";
    }
}
