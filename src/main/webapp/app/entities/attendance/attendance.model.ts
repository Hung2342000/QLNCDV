import dayjs from 'dayjs/esm';

export interface IAttendance {
  id?: number;
  employeeId?: number;
  inTime?: dayjs.Dayjs | null;
  outTime?: dayjs.Dayjs | null;
  note?: string | null;
}

export class Attendance implements IAttendance {
  constructor(
    public id?: number,
    public employeeId?: number,
    public inTime?: dayjs.Dayjs | null,
    public outTime?: dayjs.Dayjs | null,
    public note?: string | null
  ) {}
}

export function getAttendanceIdentifier(attendance: IAttendance): number | undefined {
  return attendance.id;
}
