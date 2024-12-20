import dayjs from 'dayjs/esm';

export interface IAttendance {
  id?: number;
  employeeId?: number;
  month?: number | null;
  year?: number | null;
  count?: number | null;
  countNot?: number | null;
  note?: string | null;
}

export class Attendance implements IAttendance {
  constructor(
    public id?: number,
    public employeeId?: number,
    public month?: number | null,
    public year?: number | null,
    public count?: number | null,
    public countNot?: number | null,
    public note?: string | null
  ) {}
}

export function getAttendanceIdentifier(attendance: IAttendance): number | undefined {
  return attendance.id;
}
