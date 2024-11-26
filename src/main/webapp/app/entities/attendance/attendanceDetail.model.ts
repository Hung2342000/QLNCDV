import dayjs from 'dayjs/esm';

export interface IAttendanceDetail {
  id?: number;
  attendanceId?: number;
  time?: dayjs.Dayjs | null;
  inTime?: dayjs.Dayjs | null;
  outTime?: dayjs.Dayjs | null;
  countTime?: number | null;
  status?: string | null;
  note?: string | null;
}

export class AttendanceDetail implements IAttendanceDetail {
  constructor(
    public id?: number,
    public attendanceId?: number,
    public time?: dayjs.Dayjs | null,
    public inTime?: dayjs.Dayjs | null,
    public outTime?: dayjs.Dayjs | null,
    public countTime?: number | null,
    public status?: string | null,
    public note?: string | null
  ) {}
}

export function getAttendanceDetailIdentifier(attendanceDetail: IAttendanceDetail): number | undefined {
  return attendanceDetail.id;
}
