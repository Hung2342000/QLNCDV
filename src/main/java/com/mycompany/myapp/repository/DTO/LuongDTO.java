package com.mycompany.myapp.repository.DTO;

import com.mycompany.myapp.domain.LuongDetail;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;

/**
 * A DTO representing a user, with his authorities.
 */
public class LuongDTO {

    private Long id;

    private LocalDate createDate;

    private String nameSalary;

    private String dot;

    private Long month;

    private Long year;

    private List<LuongDetail> luongDetails;

    public LuongDTO() {
        // Empty constructor needed for Jackson.
    }

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

    public List<LuongDetail> getLuongDetails() {
        return luongDetails;
    }

    public void setLuongDetails(List<LuongDetail> luongDetails) {
        this.luongDetails = luongDetails;
    }

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }
}
