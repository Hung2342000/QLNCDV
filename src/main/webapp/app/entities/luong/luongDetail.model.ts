export interface ILuongDetail {
  id?: number;
  luongId?: number | null;
  employeeId?: number | null;
  employeeCode?: string | null;
  employeeName?: string | null;
  phongBan?: string | null;
  nhom?: string | null;
  mst?: string | null;
  cccd?: string | null;
  diaban?: string | null;
  vung?: string | null;
  cap?: string | null;
  kpi?: string | null;
  numberWorking?: number | null;
  numberWorkInMonth?: number | null;
  soNgayKhongThucHien?: number | null;
  hct?: string | null;
  luongCoDinh?: number | null;
  mucDongBHXH?: number | null;
  chiPhiKhac?: number | null;
  hoaHongKhoanSanPham?: number | null;
  chiPhiBoSungToiThieuVung?: number | null;
  tongCong?: number | null;
  bhxh?: number | null;
  bhtnld?: number | null;
  bhyt?: number | null;
  bhtt?: number | null;
  tongBH?: number | null;
  bhxhdn?: number | null;
  bhtnlddn?: number | null;
  bhytdn?: number | null;
  bhttdn?: number | null;
  tongBHdn?: number | null;
  tongLuongChiuThue?: number | null;
  npt?: number | null;
  giamTNCN?: number | null;
  cdcp?: number | null;
  cdcp2?: number | null;
  bhytBaoMuon?: number | null;
  truyThuLuongDot1?: number | null;
  tongLinh?: number | null;
  luongTamUngLan1?: number | null;
  chucDanh?: string | null;
  soLuongHopDongThucTe?: string | null;
  apDungMucLuongCoDinh?: string | null;
  phuCapAnCa?: number | null;
  hoTroDiChuyen?: number | null;
  hoTroDienThoai?: number | null;
  thuNhapChiuThueThuNhap?: number | null;
  tncnThangTruoc?: number | null;
  truyThuHoanThue?: number | null;
  truyThuChiBoSung?: number | null;
  soNguoiPhuThuoc?: number | null;
  heSoChucVu?: string | null;
  truyThuCPPTTB?: number | null;
  thucChiLuongTamUng?: number | null;
  chiPhiKK?: number | null;
  chiPhiKKDaChi?: number | null;
  hoaHongKhoanConLai?: number | null;
  luongLan2?: number | null;
  conLai?: number | null;
  chiPhiTrucLe?: number | null;
}

export class LuongDetail implements ILuongDetail {
  constructor(
    public id?: number,
    public luongId?: number | null,
    public employeeId?: number | null,
    public employeeCode?: string | null,
    public employeeName?: string | null,
    public phongBan?: string | null,
    public nhom?: string | null,
    public mst?: string | null,
    public cccd?: string | null,
    public diaban?: string | null,
    public vung?: string | null,
    public cap?: string | null,
    public kpi?: string | null,
    public numberWorking?: number | null,
    public numberWorkInMonth?: number | null,
    public soNgayKhongThucHien?: number | null,
    public hct?: string | null,
    public luongCoDinh?: number | null,
    public mucDongBHXH?: number | null,
    public chiPhiKhac?: number | null,
    public hoaHongKhoanSanPham?: number | null,
    public chiPhiBoSungToiThieuVung?: number | null,
    public tongCong?: number | null,
    public bhxh?: number | null,
    public bhtnld?: number | null,
    public bhyt?: number | null,
    public bhtt?: number | null,
    public tongBH?: number | null,
    public bhxhdn?: number | null,
    public bhtnlddn?: number | null,
    public bhytdn?: number | null,
    public bhttdn?: number | null,
    public tongBHdn?: number | null,
    public tongLuongChiuThue?: number | null,
    public npt?: number | null,
    public giamTNCN?: number | null,
    public cdcp?: number | null,
    public cdcp2?: number | null,
    public bhytBaoMuon?: number | null,
    public truyThuLuongDot1?: number | null,
    public tongLinh?: number | null,
    public luongTamUngLan1?: number | null,
    public chucDanh?: string | null,
    public soLuongHopDongThucTe?: string | null,
    public apDungMucLuongCoDinh?: string | null,
    public phuCapAnCa?: number | null,
    public hoTroDiChuyen?: number | null,
    public hoTroDienThoai?: number | null,
    public thuNhapChiuThueThuNhap?: number | null,
    public tncnThangTruoc?: number | null,
    public truyThuHoanThue?: number | null,
    public truyThuChiBoSung?: number | null,
    public soNguoiPhuThuoc?: number | null,
    public heSoChucVu?: string | null,
    public truyThuCPPTTB?: number | null,
    public thucChiLuongTamUng?: number | null,
    public chiPhiKK?: number | null,
    public chiPhiKKDaChi?: number | null,
    public hoaHongKhoanConLai?: number | null,
    public luongLan2?: number | null,
    public conLai?: number | null,
    public chiPhiTrucLe?: number | null
  ) {}
}

export function getLuongDetailIdentifier(luongDetail: ILuongDetail): number | undefined {
  return luongDetail.id;
}
