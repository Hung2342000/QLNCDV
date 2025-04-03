package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

/**
 * A salaryDetail.
 */
@Entity
@Table(name = "luong_detail")
public class LuongDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "luongId")
    private Long luongId;

    @Column(name = "employeeId")
    private Long employeeId;

    @Column(name = "employeeCode")
    private String employeeCode;

    @Column(name = "employeeName")
    private String employeeName;

    @Column(name = "nhom")
    private String nhom;

    @Column(name = "mst")
    private String mst;

    @Column(name = "cccd")
    private String cccd;

    @Column(name = "diaBan")
    private String diaban;

    @Column(name = "vung")
    private String vung;

    @Column(name = "cap")
    private String cap;

    @Column(name = "kpi")
    private String kpi;

    @Column(name = "numberWorking")
    private BigDecimal numberWorking;

    @Column(name = "numberWorkInMonth")
    private BigDecimal numberWorkInMonth;

    @Column(name = "soNgayKhongThucHien")
    private BigDecimal soNgayKhongThucHien;

    @Column(name = "hct")
    private String hct;

    @Column(name = "mucDongBHXH")
    private BigDecimal mucDongBHXH;

    @Column(name = "donGiaDichVuThucNhan")
    private BigDecimal donGiaDichVuThucNhan;

    @Column(name = "luongCoDinh")
    private BigDecimal luongCoDinh;

    @Column(name = "chiPhiKhac")
    private BigDecimal chiPhiKhac;

    @Column(name = "hoaHongKhoanSanPham")
    private BigDecimal hoaHongKhoanSanPham;

    @Column(name = "chiPhiBoSungToiThieuVung")
    private BigDecimal chiPhiBoSungToiThieuVung;

    @Column(name = "tongCong")
    private BigDecimal tongCong;

    @Column(name = "bhxh")
    private BigDecimal bhxh;

    @Column(name = "bhyt")
    private BigDecimal bhyt;

    @Column(name = "bhtt")
    private BigDecimal bhtt;

    @Column(name = "tongBH")
    private BigDecimal tongBH;

    @Column(name = "tongLuongChiuThue")
    private BigDecimal tongLuongChiuThue;

    @Column(name = "npt")
    private BigDecimal npt;

    @Column(name = "giamTNCN")
    private BigDecimal giamTNCN;

    @Column(name = "cdcp")
    private BigDecimal cdcp;

    @Column(name = "bhytBaoMuon")
    private BigDecimal bhytBaoMuon;

    @Column(name = "truyThuLuongDot1")
    private BigDecimal truyThuLuongDot1;

    @Column(name = "tongLinh")
    private BigDecimal tongLinh;

    @Column(name = "luongTamUngLan1")
    private BigDecimal luongTamUngLan1;

    @Column(name = "chucDanh")
    private String chucDanh;

    @Column(name = "soLuongHopDongThucTe")
    private String soLuongHopDongThucTe;

    @Column(name = "apDungMucLuongCoDinh")
    private String apDungMucLuongCoDinh;

    @Column(name = "phuCapAnCa")
    private BigDecimal phuCapAnCa;

    @Column(name = "hoTroDiChuyen")
    private BigDecimal hoTroDiChuyen;

    @Column(name = "hoTroDienThoai")
    private BigDecimal hoTroDienThoai;

    @Column(name = "thuNhapChiuThueThuNhap")
    private BigDecimal thuNhapChiuThueThuNhap;

    @Column(name = "tncnThangTruoc")
    private BigDecimal tncnThangTruoc;

    @Column(name = "truyThuHoanThue")
    private BigDecimal truyThuHoanThue;

    @Column(name = "truyThuChiBoSung")
    private BigDecimal truyThuChiBoSung;

    @Column(name = "soNguoiPhuThuoc")
    private BigDecimal soNguoiPhuThuoc;

    @Column(name = "heSoChucVu")
    private String heSoChucVu;

    @Column(name = "truyThuCPPTTB")
    private BigDecimal truyThuCPPTTB;

    @Column(name = "thucChiLuongTamUng")
    private BigDecimal thucChiLuongTamUng;

    @Column(name = "phongBan")
    private String phongBan;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLuongId() {
        return luongId;
    }

    public void setLuongId(Long luongId) {
        this.luongId = luongId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getMst() {
        return mst;
    }

    public void setMst(String mst) {
        this.mst = mst;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getDiaban() {
        return diaban;
    }

    public void setDiaban(String diaban) {
        this.diaban = diaban;
    }

    public String getVung() {
        return vung;
    }

    public void setVung(String vung) {
        this.vung = vung;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
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

    public String getHct() {
        return hct;
    }

    public void setHct(String hct) {
        this.hct = hct;
    }

    public BigDecimal getMucDongBHXH() {
        return mucDongBHXH;
    }

    public void setMucDongBHXH(BigDecimal mucDongBHXH) {
        this.mucDongBHXH = mucDongBHXH;
    }

    public BigDecimal getDonGiaDichVuThucNhan() {
        return donGiaDichVuThucNhan;
    }

    public void setDonGiaDichVuThucNhan(BigDecimal donGiaDichVuThucNhan) {
        this.donGiaDichVuThucNhan = donGiaDichVuThucNhan;
    }

    public BigDecimal getLuongCoDinh() {
        return luongCoDinh;
    }

    public void setLuongCoDinh(BigDecimal luongCoDinh) {
        this.luongCoDinh = luongCoDinh;
    }

    public BigDecimal getChiPhiKhac() {
        return chiPhiKhac;
    }

    public void setChiPhiKhac(BigDecimal chiPhiKhac) {
        this.chiPhiKhac = chiPhiKhac;
    }

    public BigDecimal getHoaHongKhoanSanPham() {
        return hoaHongKhoanSanPham;
    }

    public void setHoaHongKhoanSanPham(BigDecimal hoaHongKhoanSanPham) {
        this.hoaHongKhoanSanPham = hoaHongKhoanSanPham;
    }

    public BigDecimal getChiPhiBoSungToiThieuVung() {
        return chiPhiBoSungToiThieuVung;
    }

    public void setChiPhiBoSungToiThieuVung(BigDecimal chiPhiBoSungToiThieuVung) {
        this.chiPhiBoSungToiThieuVung = chiPhiBoSungToiThieuVung;
    }

    public BigDecimal getTongCong() {
        return tongCong;
    }

    public void setTongCong(BigDecimal tongCong) {
        this.tongCong = tongCong;
    }

    public BigDecimal getBhxh() {
        return bhxh;
    }

    public void setBhxh(BigDecimal bhxh) {
        this.bhxh = bhxh;
    }

    public BigDecimal getBhyt() {
        return bhyt;
    }

    public void setBhyt(BigDecimal bhyt) {
        this.bhyt = bhyt;
    }

    public BigDecimal getBhtt() {
        return bhtt;
    }

    public void setBhtt(BigDecimal bhtt) {
        this.bhtt = bhtt;
    }

    public BigDecimal getTongBH() {
        return tongBH;
    }

    public void setTongBH(BigDecimal tongBH) {
        this.tongBH = tongBH;
    }

    public BigDecimal getTongLuongChiuThue() {
        return tongLuongChiuThue;
    }

    public void setTongLuongChiuThue(BigDecimal tongLuongChiuThue) {
        this.tongLuongChiuThue = tongLuongChiuThue;
    }

    public BigDecimal getNpt() {
        return npt;
    }

    public void setNpt(BigDecimal npt) {
        this.npt = npt;
    }

    public BigDecimal getGiamTNCN() {
        return giamTNCN;
    }

    public void setGiamTNCN(BigDecimal giamTNCN) {
        this.giamTNCN = giamTNCN;
    }

    public BigDecimal getCdcp() {
        return cdcp;
    }

    public void setCdcp(BigDecimal cdcp) {
        this.cdcp = cdcp;
    }

    public BigDecimal getBhytBaoMuon() {
        return bhytBaoMuon;
    }

    public void setBhytBaoMuon(BigDecimal bhytBaoMuon) {
        this.bhytBaoMuon = bhytBaoMuon;
    }

    public BigDecimal getTruyThuLuongDot1() {
        return truyThuLuongDot1;
    }

    public void setTruyThuLuongDot1(BigDecimal truyThuLuongDot1) {
        this.truyThuLuongDot1 = truyThuLuongDot1;
    }

    public BigDecimal getTongLinh() {
        return tongLinh;
    }

    public void setTongLinh(BigDecimal tongLinh) {
        this.tongLinh = tongLinh;
    }

    public BigDecimal getLuongTamUngLan1() {
        return luongTamUngLan1;
    }

    public void setLuongTamUngLan1(BigDecimal luongTamUngLan1) {
        this.luongTamUngLan1 = luongTamUngLan1;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getNhom() {
        return nhom;
    }

    public String getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(String chucDanh) {
        this.chucDanh = chucDanh;
    }

    public String getSoLuongHopDongThucTe() {
        return soLuongHopDongThucTe;
    }

    public void setSoLuongHopDongThucTe(String soLuongHopDongThucTe) {
        this.soLuongHopDongThucTe = soLuongHopDongThucTe;
    }

    public String getApDungMucLuongCoDinh() {
        return apDungMucLuongCoDinh;
    }

    public void setApDungMucLuongCoDinh(String apDungMucLuongCoDinh) {
        this.apDungMucLuongCoDinh = apDungMucLuongCoDinh;
    }

    public BigDecimal getPhuCapAnCa() {
        return phuCapAnCa;
    }

    public void setPhuCapAnCa(BigDecimal phuCapAnCa) {
        this.phuCapAnCa = phuCapAnCa;
    }

    public BigDecimal getHoTroDiChuyen() {
        return hoTroDiChuyen;
    }

    public void setHoTroDiChuyen(BigDecimal hoTroDiChuyen) {
        this.hoTroDiChuyen = hoTroDiChuyen;
    }

    public BigDecimal getHoTroDienThoai() {
        return hoTroDienThoai;
    }

    public void setHoTroDienThoai(BigDecimal hoTroDienThoai) {
        this.hoTroDienThoai = hoTroDienThoai;
    }

    public BigDecimal getThuNhapChiuThueThuNhap() {
        return thuNhapChiuThueThuNhap;
    }

    public void setThuNhapChiuThueThuNhap(BigDecimal thuNhapChiuThueThuNhap) {
        this.thuNhapChiuThueThuNhap = thuNhapChiuThueThuNhap;
    }

    public BigDecimal getTncnThangTruoc() {
        return tncnThangTruoc;
    }

    public void setTncnThangTruoc(BigDecimal tncnThangTruoc) {
        this.tncnThangTruoc = tncnThangTruoc;
    }

    public BigDecimal getTruyThuHoanThue() {
        return truyThuHoanThue;
    }

    public void setTruyThuHoanThue(BigDecimal truyThuHoanThue) {
        this.truyThuHoanThue = truyThuHoanThue;
    }

    public BigDecimal getTruyThuChiBoSung() {
        return truyThuChiBoSung;
    }

    public void setTruyThuChiBoSung(BigDecimal truyThuChiBoSung) {
        this.truyThuChiBoSung = truyThuChiBoSung;
    }

    public BigDecimal getSoNguoiPhuThuoc() {
        return soNguoiPhuThuoc;
    }

    public void setSoNguoiPhuThuoc(BigDecimal soNguoiPhuThuoc) {
        this.soNguoiPhuThuoc = soNguoiPhuThuoc;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    public String getHeSoChucVu() {
        return heSoChucVu;
    }

    public void setHeSoChucVu(String heSoChucVu) {
        this.heSoChucVu = heSoChucVu;
    }

    public BigDecimal getTruyThuCPPTTB() {
        return truyThuCPPTTB;
    }

    public void setTruyThuCPPTTB(BigDecimal truyThuCPPTTB) {
        this.truyThuCPPTTB = truyThuCPPTTB;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public BigDecimal getSoNgayKhongThucHien() {
        return soNgayKhongThucHien;
    }

    public void setSoNgayKhongThucHien(BigDecimal soNgayKhongThucHien) {
        this.soNgayKhongThucHien = soNgayKhongThucHien;
    }

    public BigDecimal getThucChiLuongTamUng() {
        return thucChiLuongTamUng;
    }

    public void setThucChiLuongTamUng(BigDecimal thucChiLuongTamUng) {
        this.thucChiLuongTamUng = thucChiLuongTamUng;
    }

    public String getPhongBan() {
        return phongBan;
    }

    public void setPhongBan(String phongBan) {
        this.phongBan = phongBan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LuongDetail)) {
            return false;
        }
        return id != null && id.equals(((LuongDetail) o).id);
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
