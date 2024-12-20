import dayjs from 'dayjs/esm';
import { IEmployee } from '../employee/employee.model';

export interface ISalary {
  id?: number;
  createDate?: dayjs.Dayjs | null;
  nameSalary?: string | null;
  month?: number | null;
  year?: number | null;
  numberWork?: number | null;
  attendanceId?: number | null;
  employees?: IEmployee[] | null;
  isAttendance?: boolean | null;
}

export class Salary implements ISalary {
  constructor(
    public id?: number,
    public createDate?: dayjs.Dayjs | null,
    public name?: string | null,
    public month?: number | null,
    public year?: number | null,
    public numberWork?: number | null,
    public attendanceId?: number | null,
    public employees?: IEmployee[] | null,
    public isAttendance?: boolean | null
  ) {}
}

export function getSalaryIdentifier(salary: ISalary): number | undefined {
  return salary.id;
}
