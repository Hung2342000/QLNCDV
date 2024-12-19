import dayjs from 'dayjs/esm';
import { IEmployee } from '../employee/employee.model';

export interface IAttendance {
  id?: number;
  createDate?: dayjs.Dayjs | null;
  name?: string | null;
  employees?: IEmployee[] | null;
  month?: number | null;
  year?: number | null;
  count?: number | null;
  note?: string | null;
}

export class Attendance implements IAttendance {
  constructor(
    public id?: number,
    public createDate?: dayjs.Dayjs | null,
    public name?: string | null,
    public employees?: IEmployee[] | null,
    public month?: number | null,
    public year?: number | null,
    public count?: number | null,
    public note?: string | null
  ) {}
}

export function getAttendanceIdentifier(attendance: IAttendance): number | undefined {
  return attendance.id;
}
