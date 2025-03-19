import dayjs from 'dayjs/esm';

export interface ITransfer {
  id?: number;
  employeeId?: number;
  startDate?: dayjs.Dayjs | null;
  closeDate?: dayjs.Dayjs | null;
  serviceType?: number | null;
  serviceTypeOld?: number | null;
  department?: string | null;
  departmentOld?: string | null;
  status?: string | null;
  statusOld?: string | null;
}
export class Transfer implements ITransfer {
  constructor(
    public id?: number,
    public employeeId?: number,
    public startDate?: dayjs.Dayjs | null,
    public closeDate?: dayjs.Dayjs | null,
    public serviceType?: number | null,
    public serviceTypeOld?: number | null,
    public department?: string | null,
    public departmentOld?: string | null,
    public status?: string | null,
    public statusOld?: string | null
  ) {}
}

export function getTransferIdentifier(transfer: Transfer): number | undefined {
  return transfer.id;
}
