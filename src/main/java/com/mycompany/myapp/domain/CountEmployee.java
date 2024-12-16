package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jhi_Count_Employee")
public class CountEmployee implements Serializable {

    @Id
    @Column(length = 50)
    private String code;

    private Long countEmployee;
    private Boolean isDepartment;

    public Boolean getDepartment() {
        return isDepartment;
    }

    public void setDepartment(Boolean department) {
        isDepartment = department;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCountEmployee() {
        return countEmployee;
    }

    public void setCountEmployee(Long countEmployee) {
        this.countEmployee = countEmployee;
    }

    public CountEmployee() {}

    public CountEmployee(String code, Long countEmployee) {
        this.code = code;
        this.countEmployee = countEmployee;
    }
}
