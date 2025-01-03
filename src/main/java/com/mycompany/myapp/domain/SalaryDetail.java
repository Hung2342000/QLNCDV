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

    @Column(name = "chucDanh")
    private String chucDanh;

    @Column(name = "diemCungCapDV")
    private String diemCungCapDV;

    @Column(name = "vung")
    private String vung;

    @Column(name = "donGiaDichVu")
    private BigDecimal donGiaDichVu;

    @Column(name = "numberWorking")
    private BigDecimal numberWorking;

    @Column(name = "numberWorkInMonth")
    private BigDecimal numberWorkInMonth;

    @Column(name = "donGiaDichVuThucNhan")
    private BigDecimal donGiaDichVuThucNhan;

    @Column(name = "mucChiToiThieu")
    private BigDecimal mucChiToiThieu;

    @Column(name = "xepLoai")
    private String xepLoai;

    @Column(name = "htc")
    private String htc;

    @Column(name = "chiPhiGiamTru")
    private BigDecimal chiPhiGiamTru;

    @Column(name = "chiPhiThueDichVu")
    private BigDecimal chiPhiThueDichVu;

    @Column(name = "note")
    private String note;

    @Column(name = "kpis")
    private String kpis;

    @Column(name = "diaBan")
    private String diaBan;

    @Column(name = "luongCoDinhThucTe")
    private BigDecimal luongCoDinhThucTe;

    @Column(name = "phiCoDinhDaThucHien")
    private BigDecimal phiCoDinhDaThucHien;

    @Column(name = "phiCoDinhThanhToanThucTe")
    private BigDecimal phiCoDinhThanhToanThucTe;

    @Column(name = "chiPhiDichVuKhoanVaKK")
    private BigDecimal chiPhiDichVuKhoanVaKK;

    @Column(name = "chiPhiKKKhac")
    private BigDecimal chiPhiKKKhac;

    @Column(name = "tongChiPhiKVKK")
    private BigDecimal tongChiPhiKVKK;

    @Column(name = "nhom")
    private String nhom;

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

    public String getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(String chucDanh) {
        this.chucDanh = chucDanh;
    }

    public String getDiemCungCapDV() {
        return diemCungCapDV;
    }

    public void setDiemCungCapDV(String diemCungCapDV) {
        this.diemCungCapDV = diemCungCapDV;
    }

    public String getVung() {
        return vung;
    }

    public void setVung(String vung) {
        this.vung = vung;
    }

    public BigDecimal getDonGiaDichVu() {
        return donGiaDichVu;
    }

    public void setDonGiaDichVu(BigDecimal donGiaDichVu) {
        this.donGiaDichVu = donGiaDichVu;
    }

    public BigDecimal getNumberWorking() {
        return numberWorking;
    }

    public void setNumberWorking(BigDecimal numberWorking) {
        this.numberWorking = numberWorking;
    }

    public BigDecimal getNumberWorkInMonth() {
        return numberWorkInMonth;
    }

    public void setNumberWorkInMonth(BigDecimal numberWorkInMonth) {
        this.numberWorkInMonth = numberWorkInMonth;
    }

    public BigDecimal getDonGiaDichVuThucNhan() {
        return donGiaDichVuThucNhan;
    }

    public void setDonGiaDichVuThucNhan(BigDecimal donGiaDichVuThucNhan) {
        this.donGiaDichVuThucNhan = donGiaDichVuThucNhan;
    }

    public BigDecimal getMucChiToiThieu() {
        return mucChiToiThieu;
    }

    public void setMucChiToiThieu(BigDecimal mucChiToiThieu) {
        this.mucChiToiThieu = mucChiToiThieu;
    }

    public String getXepLoai() {
        return xepLoai;
    }

    public void setXepLoai(String xepLoai) {
        this.xepLoai = xepLoai;
    }

    public String getHtc() {
        return htc;
    }

    public void setHtc(String htc) {
        this.htc = htc;
    }

    public BigDecimal getChiPhiGiamTru() {
        return chiPhiGiamTru;
    }

    public void setChiPhiGiamTru(BigDecimal chiPhiGiamTru) {
        this.chiPhiGiamTru = chiPhiGiamTru;
    }

    public BigDecimal getChiPhiThueDichVu() {
        return chiPhiThueDichVu;
    }

    public void setChiPhiThueDichVu(BigDecimal chiPhiThueDichVu) {
        this.chiPhiThueDichVu = chiPhiThueDichVu;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getKpis() {
        return kpis;
    }

    public void setKpis(String kpis) {
        this.kpis = kpis;
    }

    public String getDiaBan() {
        return diaBan;
    }

    public void setDiaBan(String diaBan) {
        this.diaBan = diaBan;
    }

    public BigDecimal getPhiCoDinhDaThucHien() {
        return phiCoDinhDaThucHien;
    }

    public void setPhiCoDinhDaThucHien(BigDecimal phiCoDinhDaThucHien) {
        this.phiCoDinhDaThucHien = phiCoDinhDaThucHien;
    }

    public BigDecimal getPhiCoDinhThanhToanThucTe() {
        return phiCoDinhThanhToanThucTe;
    }

    public void setPhiCoDinhThanhToanThucTe(BigDecimal phiCoDinhThanhToanThucTe) {
        this.phiCoDinhThanhToanThucTe = phiCoDinhThanhToanThucTe;
    }

    public BigDecimal getChiPhiDichVuKhoanVaKK() {
        return chiPhiDichVuKhoanVaKK;
    }

    public void setChiPhiDichVuKhoanVaKK(BigDecimal chiPhiDichVuKhoanVaKK) {
        this.chiPhiDichVuKhoanVaKK = chiPhiDichVuKhoanVaKK;
    }

    public BigDecimal getChiPhiKKKhac() {
        return chiPhiKKKhac;
    }

    public void setChiPhiKKKhac(BigDecimal chiPhiKKKhac) {
        this.chiPhiKKKhac = chiPhiKKKhac;
    }

    public BigDecimal getTongChiPhiKVKK() {
        return tongChiPhiKVKK;
    }

    public void setTongChiPhiKVKK(BigDecimal tongChiPhiKVKK) {
        this.tongChiPhiKVKK = tongChiPhiKVKK;
    }

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public BigDecimal getLuongCoDinhThucTe() {
        return luongCoDinhThucTe;
    }

    public void setLuongCoDinhThucTe(BigDecimal luongCoDinhThucTe) {
        this.luongCoDinhThucTe = luongCoDinhThucTe;
    }

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
