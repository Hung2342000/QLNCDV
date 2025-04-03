import dayjs from 'dayjs/esm';
import { IEmployee } from '../employee/employee.model';

export interface ILuong {
  id?: number;
  createDate?: dayjs.Dayjs | null;
  nameSalary?: string | null;
  month?: number | null;
  year?: number | null;
}

export class Luong implements ILuong {
  constructor(
    public id?: number,
    public createDate?: dayjs.Dayjs | null,
    public name?: string | null,
    public month?: number | null,
    public year?: number | null
  ) {}
}

export function getSalaryIdentifier(luong: ILuong): number | undefined {
  return luong.id;
}
