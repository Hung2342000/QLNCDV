import dayjs from 'dayjs/esm';

export interface IEmployee {
  id?: number;
  codeEmployee?: string;
  name?: string | null;
  birthday?: dayjs.Dayjs | null;
  otherId?: string | null;
  address?: string | null;
  mobilePhone?: string | null;
  workPhone?: string | null;
  workEmail?: string | null;
  privateEmail?: string | null;
  department?: string | null;
  startDate?: dayjs.Dayjs | null;
  basicSalary?: number | null;
  serviceType?: number | null;
  serviceTypeName?: string | null;
  region?: string | null;
  isTeller?: boolean | null;
  rank?: string | null;
  nhom?: string | null;
  status?: string | null;
}

export class Employee implements IEmployee {
  constructor(
    public id?: number,
    public codeEmployee?: string,
    public name?: string | null,
    public birthday?: dayjs.Dayjs | null,
    public otherId?: string | null,
    public address?: string | null,
    public mobilePhone?: string | null,
    public workPhone?: string | null,
    public workEmail?: string | null,
    public privateEmail?: string | null,
    public department?: string | null,
    public startDate?: dayjs.Dayjs | null,
    public basicSalary?: number | null,
    public serviceType?: number | null,
    public serviceTypeName?: string | null,
    public region?: string | null,
    public isTeller?: boolean | null,
    public rank?: string | null,
    public nhom?: string | null,
    public status?: string | null
  ) {}
}

export function getEmployeeIdentifier(employee: IEmployee): number | undefined {
  return employee.id;
}
