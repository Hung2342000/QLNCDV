export interface ISalaryDetail {
  id?: number;
  salaryId?: number | null;
  employeeId?: number | null;
  employeeCode?: string | null;
  employeeName?: string | null;
  diemCungCapDV?: string | null;
  chucDanh?: string | null;
  vung?: string | null;
  cap?: string | null;
  donGiaDichVu?: number | null;
  numberWorking?: number | null;
  numberWorkInMonth?: number | null;
  donGiaDichVuThucNhan?: number | null;
  mucChiToiThieu?: number | null;
  xepLoai?: string | null;
  htc?: string | null;
  chiPhiGiamTru?: number | null;
  chiPhiThueDichVu?: number | null;
  note?: string | null;
  kpis?: string | null;
  diaBan?: string | null;
  luongCoDinhThucTe?: number | null;
  mucBSLuongToiThieuVung?: number | null;
  phiCoDinhDaThucHien?: number | null;
  phiCoDinhThanhToanThucTe?: number | null;
  chiPhiDichVuKhoanVaKK?: number | null;
  chiPhiKKKhac?: number | null;
  tongChiPhiKVKK?: number | null;
  nhom?: string | null;
  tenDonVi?: string | null;
  dichVu?: string | null;
  heSoChucVu?: string | null;
  soLuongHopDong?: number | null;
  apDungMucLuongCoDinh?: string | null;
  mucChiPhiCoDinhThucTe?: number | null;
  chiPhiBoSungCPTTV?: number | null;
}

export class SalaryDetail implements ISalaryDetail {
  constructor(
    public id?: number,
    public salaryId?: number | null,
    public employeeId?: number | null,
    public employeeCode?: string | null,
    public employeeName?: string | null,
    public diemCungCapDV?: string | null,
    public chucDanh?: string | null,
    public vung?: string | null,
    public cap?: string | null,
    public donGiaDichVu?: number | null,
    public numberWorking?: number | null,
    public numberWorkInMonth?: number | null,
    public donGiaDichVuThucNhan?: number | null,
    public mucChiToiThieu?: number | null,
    public xepLoai?: string | null,
    public htc?: string | null,
    public chiPhiGiamTru?: number | null,
    public chiPhiThueDichVu?: number | null,
    public note?: string | null,
    public kpis?: string | null,
    public diaBan?: string | null,
    public luongCoDinhThucTe?: number | null,
    public mucBSLuongToiThieuVung?: number | null,
    public phiCoDinhDaThucHien?: number | null,
    public phiCoDinhThanhToanThucTe?: number | null,
    public chiPhiDichVuKhoanVaKK?: number | null,
    public chiPhiKKKhac?: number | null,
    public tongChiPhiKVKK?: number | null,
    public nhom?: string | null,
    public tenDonVi?: string | null,
    public dichVu?: string | null,
    public heSoChucVu?: string | null,
    public soLuongHopDong?: number | null,
    public apDungMucLuongCoDinh?: string | null,
    public mucChiPhiCoDinhThucTe?: number | null,
    public chiPhiBoSungCPTTV?: number | null
  ) {}
}

export function getSalaryDetailIdentifier(salaryDetail: ISalaryDetail): number | undefined {
  return salaryDetail.id;
}
