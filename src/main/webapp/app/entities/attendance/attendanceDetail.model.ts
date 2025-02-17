import dayjs from 'dayjs/esm';

export interface IAttendanceDetail {
  id?: number;
  attendanceId?: number;
  employeeId?: number;
  employeeCode?: string | null;
  employeeName?: string | null;
  serviceTypeName?: string | null;
  department?: string | null;
  day1?: string | null;
  day2?: string | null;
  day3?: string | null;
  day4?: string | null;
  day5?: string | null;
  day6?: string | null;
  day7?: string | null;
  day8?: string | null;
  day9?: string | null;
  day10?: string | null;
  day11?: string | null;
  day12?: string | null;
  day13?: string | null;
  day14?: string | null;
  day15?: string | null;
  day16?: string | null;
  day17?: string | null;
  day18?: string | null;
  day19?: string | null;
  day20?: string | null;
  day21?: string | null;
  day22?: string | null;
  day23?: string | null;
  day24?: string | null;
  day25?: string | null;
  day26?: string | null;
  day27?: string | null;
  day28?: string | null;
  day29?: string | null;
  day30?: string | null;
  day31?: string | null;
  paidWorking?: number | null;
  stopWorking?: number | null;
  numberWork?: number | null;
}

export class AttendanceDetail implements IAttendanceDetail {
  constructor(
    public id?: number,
    public attendanceId?: number,
    public employeeId?: number,
    public employeeCode?: string,
    public employeeName?: string,
    public serviceTypeName?: string | null,
    public department?: string | null,
    public day1?: string | null,
    public day2?: string | null,
    public day3?: string | null,
    public day4?: string | null,
    public day5?: string | null,
    public day6?: string | null,
    public day7?: string | null,
    public day8?: string | null,
    public day9?: string | null,
    public day10?: string | null,
    public day11?: string | null,
    public day12?: string | null,
    public day13?: string | null,
    public day14?: string | null,
    public day15?: string | null,
    public day16?: string | null,
    public day17?: string | null,
    public day18?: string | null,
    public day19?: string | null,
    public day20?: string | null,
    public day21?: string | null,
    public day22?: string | null,
    public day23?: string | null,
    public day24?: string | null,
    public day25?: string | null,
    public day26?: string | null,
    public day27?: string | null,
    public day28?: string | null,
    public day29?: string | null,
    public day30?: string | null,
    public day31?: string | null,
    public paidWorking?: number | null,
    public stopWorking?: number | null,
    public numberWork?: number | null
  ) {}
}

export function getAttendanceDetailIdentifier(attendanceDetail: IAttendanceDetail): number | undefined {
  return attendanceDetail.id;
}
