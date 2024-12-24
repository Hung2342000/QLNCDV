export interface IServiceType {
  id?: number;
  serviceName?: string | null;
  region?: string | null;
  rank?: string | null;
}
export class ServiceType implements IServiceType {
  constructor(public id?: number, public serviceName?: string | null, public region?: string | null, public rank?: string | null) {}
}
export function getServiceType(serviceType: IServiceType): number | undefined {
  return serviceType.id;
}
