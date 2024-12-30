export interface ISalaryDetail {
  id?: number;
  salaryId?: number | null;
  employeeId?: number | null;
  diemCungCapDV?: string | null;
  chucDanh?: string | null;
  vung?: string | null;
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
}

export class SalaryDetail implements ISalaryDetail {
  constructor(
    public id?: number,
    public salaryId?: number | null,
    public employeeId?: number | null,
    public diemCungCapDV?: string | null,
    public chucDanh?: string | null,
    public vung?: string | null,
    public donGiaDichVu?: number | null,
    public numberWorking?: number | null,
    public numberWorkInMonth?: number | null,
    public donGiaDichVuThucNhan?: number | null,
    public mucChiToiThieu?: number | null,
    public xepLoai?: string | null,
    public htc?: string | null,
    public chiPhiGiamTru?: number | null,
    public chiPhiThueDichVu?: number | null,
    public note?: string | null
  ) {}
}

export function getSalaryDetailIdentifier(salaryDetail: ISalaryDetail): number | undefined {
  return salaryDetail.id;
}
