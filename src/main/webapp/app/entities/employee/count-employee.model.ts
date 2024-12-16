export interface ICountEmployee {
  code?: string;
  countEmployee?: number;
  isDepartment?: boolean;
}

export class CountEmployee implements ICountEmployee {
  constructor(public code?: string, public countEmployee?: number, public isDepartment?: boolean) {}
}
