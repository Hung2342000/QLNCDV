export interface IDiaBan {
  id?: number;
  name?: string | null;
  vung?: string | null;
  tinh?: string | null;
  note?: string | null;
}

export class DiaBan implements IDiaBan {
  constructor(
    public id?: number,
    public name?: string | null,
    public vung?: string | null,
    public tinh?: string | null,
    public note?: string | null
  ) {}
}

export function getDiaBan(diaBan: IDiaBan): number | undefined {
  return diaBan.id;
}
