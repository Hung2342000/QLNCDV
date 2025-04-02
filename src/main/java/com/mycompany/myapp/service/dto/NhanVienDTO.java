package com.mycompany.myapp.service.dto;

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
    private Long thang;
    private Long nam;
    private String kpi;
    private String hct;

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
        Long thang,
        Long nam,
        String kpi,
        String hct
    ) {
        this.idEmployee = idEmployee;
        this.codeEmployee = codeEmployee;
        this.nameEmployee = nameEmployee;
        this.serviceTypeName = serviceTypeName;
        this.region = region;
        this.rank = rank;
        this.nhom = nhom;
        this.thang = thang;
        this.nam = nam;
        this.kpi = kpi;
        this.hct = hct;
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

    public Long getThang() {
        return thang;
    }

    public void setThang(Long thang) {
        this.thang = thang;
    }

    public Long getNam() {
        return nam;
    }

    public void setNam(Long nam) {
        this.nam = nam;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }

    public String getHct() {
        return hct;
    }

    public void setHct(String hct) {
        this.hct = hct;
    }
}
