import dayjs from 'dayjs/esm';

export interface INgayNghiLe {
  id?: number;
  holidayDate?: dayjs.Dayjs | null;
  description?: string | null;
}

export class NgayNghiLe implements INgayNghiLe {
  constructor(public id?: number, public holidayDate?: dayjs.Dayjs | null, public description?: string | null) {}
}

export function getNgayNghiLeIdentifier(ngayNghiLe: INgayNghiLe): number | undefined {
  return ngayNghiLe.id;
}
