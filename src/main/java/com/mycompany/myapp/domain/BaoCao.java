package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

@Entity
@Table(name = "bao_cao")
public class BaoCao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_employee")
    private Long idEmployee;

    @Column(name = "code_employee")
    private String codeEmployee;

    @Column(name = "name_employee")
    private String nameEmployee;

    @Column(name = "service_type_name")
    private String serviceTypeName;

    @Column(name = "region")
    private String region;

    @Column(name = "rank")
    private String rank;

    @Column(name = "nhom")
    private String nhom;

    @Column(name = "thang1")
    private String thang1;

    @Column(name = "thang2")
    private String thang2;

    @Column(name = "thang3")
    private String thang3;

    @Column(name = "thang4")
    private String thang4;

    @Column(name = "thang5")
    private String thang5;

    @Column(name = "thang6")
    private String thang6;

    @Column(name = "thang7")
    private String thang7;

    @Column(name = "thang8")
    private String thang8;

    @Column(name = "thang9")
    private String thang9;

    @Column(name = "thang10")
    private String thang10;

    @Column(name = "thang11")
    private String thang11;

    @Column(name = "thang12")
    private String thang12;

    @Column(name = "nam")
    private Long nam;

    @Column(name = "kpi_trung_binh")
    private String kpiTrungBinh;

    @Column(name = "note")
    private String note;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getThang1() {
        return thang1;
    }

    public void setThang1(String thang1) {
        this.thang1 = thang1;
    }

    public String getThang2() {
        return thang2;
    }

    public void setThang2(String thang2) {
        this.thang2 = thang2;
    }

    public String getThang3() {
        return thang3;
    }

    public void setThang3(String thang3) {
        this.thang3 = thang3;
    }

    public String getThang4() {
        return thang4;
    }

    public void setThang4(String thang4) {
        this.thang4 = thang4;
    }

    public String getThang5() {
        return thang5;
    }

    public void setThang5(String thang5) {
        this.thang5 = thang5;
    }

    public String getThang6() {
        return thang6;
    }

    public void setThang6(String thang6) {
        this.thang6 = thang6;
    }

    public String getThang7() {
        return thang7;
    }

    public void setThang7(String thang7) {
        this.thang7 = thang7;
    }

    public String getThang8() {
        return thang8;
    }

    public void setThang8(String thang8) {
        this.thang8 = thang8;
    }

    public String getThang9() {
        return thang9;
    }

    public void setThang9(String thang9) {
        this.thang9 = thang9;
    }

    public String getThang10() {
        return thang10;
    }

    public void setThang10(String thang10) {
        this.thang10 = thang10;
    }

    public String getThang11() {
        return thang11;
    }

    public void setThang11(String thang11) {
        this.thang11 = thang11;
    }

    public String getThang12() {
        return thang12;
    }

    public void setThang12(String thang12) {
        this.thang12 = thang12;
    }

    public Long getNam() {
        return nam;
    }

    public void setNam(Long nam) {
        this.nam = nam;
    }

    public String getKpiTrungBinh() {
        return kpiTrungBinh;
    }

    public void setKpiTrungBinh(String kpiTrungBinh) {
        this.kpiTrungBinh = kpiTrungBinh;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaoCao)) {
            return false;
        }
        return id != null && id.equals(((BaoCao) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BaoCao{" +
            "id=" + getId() +
            "}";
    }
}
