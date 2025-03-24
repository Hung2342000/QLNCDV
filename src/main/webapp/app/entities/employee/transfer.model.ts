import dayjs from 'dayjs/esm';

export interface ITransfer {
  id?: number;
  employeeId?: number;
  startDate?: dayjs.Dayjs | null;
  closeDate?: dayjs.Dayjs | null;
  serviceTypeName?: string | null;
  serviceTypeNameOld?: string | null;
  department?: string | null;
  departmentOld?: string | null;
  status?: string | null;
  statusOld?: string | null;
  diaBan?: string | null;
  diaBanOld?: string | null;
  rank?: string | null;
}
export class Transfer implements ITransfer {
  constructor(
    public id?: number,
    public employeeId?: number,
    public startDate?: dayjs.Dayjs | null,
    public closeDate?: dayjs.Dayjs | null,
    public serviceTypeName?: string | null,
    public serviceTypeNameOld?: string | null,
    public department?: string | null,
    public departmentOld?: string | null,
    public status?: string | null,
    public statusOld?: string | null,
    public diaBan?: string | null,
    public diaBanOld?: string | null,
    public rank?: string | null
  ) {}
}

export function getTransferIdentifier(transfer: Transfer): number | undefined {
  return transfer.id;
}
