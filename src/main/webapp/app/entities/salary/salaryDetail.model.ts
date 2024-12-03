export interface ISalaryDetail {
  id?: number;
  salaryId?: number | null;
  employeeId?: number | null;
  basicSalary?: number | null;
  numberWorking?: number | null;
  numberWorkInMonth?: number | null;
  allowance?: number | null;
  incentiveSalary?: number | null;
  amount?: number | null;
  note?: string | null;
}

export class SalaryDetail implements ISalaryDetail {
  constructor(
    public id?: number,
    public salaryId?: number | null,
    public employeeId?: number | null,
    public basicSalary?: number | null,
    public numberWorking?: number | null,
    public numberWorkInMonth?: number | null,
    public allowance?: number | null,
    public incentiveSalary?: number | null,
    public amount?: number | null,
    public note?: string | null
  ) {}
}

export function getSalaryDetailIdentifier(salaryDetail: ISalaryDetail): number | undefined {
  return salaryDetail.id;
}
