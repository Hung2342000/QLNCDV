import dayjs from 'dayjs/esm';
import { IEmployee } from '../employee/employee.model';

export interface IBaoCao {
  id?: number;
  idEmployee?: number | null;
  codeEmployee?: string | null;
  nameEmployee?: string | null;
  serviceTypeName?: string | null;
  region?: string | null;
  rank?: string | null;
  nhom?: string | null;
  thang1?: number | null;
  thang2?: number | null;
  thang3?: number | null;
  thang4?: number | null;
  thang5?: number | null;
  thang6?: number | null;
  thang7?: number | null;
  thang8?: number | null;
  thang9?: number | null;
  thang10?: number | null;
  thang11?: number | null;
  thang12?: number | null;
  nam?: number | null;
  kpiTrungBinh?: number | null;
  note?: number | null;
}

export class BaoCao implements IBaoCao {
  constructor(
    public id?: number,
    public idEmployee?: number | null,
    public codeEmployee?: string | null,
    public nameEmployee?: string | null,
    public serviceTypeName?: string | null,
    public region?: string | null,
    public rank?: string | null,
    public nhom?: string | null,
    public thang1?: number | null,
    public thang2?: number | null,
    public thang3?: number | null,
    public thang4?: number | null,
    public thang5?: number | null,
    public thang6?: number | null,
    public thang7?: number | null,
    public thang8?: number | null,
    public thang9?: number | null,
    public thang10?: number | null,
    public thang11?: number | null,
    public thang12?: number | null,
    public nam?: number | null,
    public kpiTrungBinh?: number | null,
    public note?: number | null
  ) {}
}

export function getSalaryIdentifier(baoCao: IBaoCao): number | undefined {
  return baoCao.id;
}
