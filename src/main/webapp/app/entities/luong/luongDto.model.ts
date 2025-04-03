import dayjs from 'dayjs/esm';
import { IEmployee } from '../employee/employee.model';
import { ILuongDetail } from './luongDetail.model';

export interface ILuongDto {
  id?: number;
  createDate?: dayjs.Dayjs | null;
  nameSalary?: string | null;
  month?: string | null;
  year?: string | null;
  luongDetails?: ILuongDetail[] | null;
}

export class LuongDto implements ILuongDto {
  constructor(
    public id?: number,
    public createDate?: dayjs.Dayjs | null,
    public name?: string | null,
    public month?: string | null,
    public year?: string | null,
    public luongDetails?: ILuongDetail[] | null
  ) {}
}

export function getSalaryIdentifier(luong: ILuongDto): number | undefined {
  return luong.id;
}
