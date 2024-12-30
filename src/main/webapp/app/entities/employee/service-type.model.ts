export interface IServiceType {
  id?: number;
  serviceName?: string | null;
  region?: string | null;
  rank?: string | null;
  basicSalary?: number | null;
  mucChiTraToiThieu?: number | null;
}
export class ServiceType implements IServiceType {
  constructor(
    public id?: number,
    public serviceName?: string | null,
    public region?: string | null,
    public rank?: string | null,
    public basicSalary?: number | null,
    public mucChiTraToiThieu?: number | null
  ) {}
}
export function getServiceType(serviceType: IServiceType): number | undefined {
  return serviceType.id;
}
