import dayjs from 'dayjs/esm';

export interface IAttendanceDetail {
  id?: number;
  attendanceId?: number;
  inTime?: dayjs.Dayjs | null;
  outTime?: dayjs.Dayjs | null;
  note?: string | null;
}

export class AttendanceDetail implements IAttendanceDetail {
  constructor(
    public id?: number,
    public employeeId?: number,
    public inTime?: dayjs.Dayjs | null,
    public outTime?: dayjs.Dayjs | null,
    public note?: string | null
  ) {}
}

export function getAttendanceIdentifier(attendanceDetail: IAttendanceDetail): number | undefined {
  return attendanceDetail.id;
}
