package com.mycompany.myapp.repository;

/**
 * A DTO representing a user, with his authorities.
 */
public class NhanVienDTO {

    private Long idEmployee;
    private String codeEmployee;
    private String nameEmployee;
    private String serviceTypeName;
    private String region;
    private String rank;
    private String nhom;
    private Long month;
    private Long year;
    private String kpis;
    private String htc;

    public NhanVienDTO() {
        // Empty constructor needed for Jackson.
    }

    public NhanVienDTO(
        Long idEmployee,
        String codeEmployee,
        String nameEmployee,
        String serviceTypeName,
        String region,
        String rank,
        String nhom,
        Long month,
        Long year,
        String kpis,
        String htc
    ) {
        this.idEmployee = idEmployee;
        this.codeEmployee = codeEmployee;
        this.nameEmployee = nameEmployee;
        this.serviceTypeName = serviceTypeName;
        this.region = region;
        this.rank = rank;
        this.nhom = nhom;
        this.month = month;
        this.year = year;
        this.kpis = kpis;
        this.htc = htc;
    }

    public Long getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Long idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getCodeEmployee() {
        return codeEmployee;
    }

    public void setCodeEmployee(String codeEmployee) {
        this.codeEmployee = codeEmployee;
    }

    public String getNameEmployee() {
        return nameEmployee;
    }

    public void setNameEmployee(String nameEmployee) {
        this.nameEmployee = nameEmployee;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
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

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
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

    public String getKpis() {
        return kpis;
    }

    public void setKpis(String kpis) {
        this.kpis = kpis;
    }

    public String getHtc() {
        return htc;
    }

    public void setHtc(String htc) {
        this.htc = htc;
    }
}
