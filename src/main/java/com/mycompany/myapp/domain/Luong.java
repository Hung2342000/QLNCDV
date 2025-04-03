package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;

/**
 * A salary.
 */
@Entity
@Table(name = "luong")
public class Luong implements Serializable {

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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Luong)) {
            return false;
        }
        return id != null && id.equals(((Luong) o).id);
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
