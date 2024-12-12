export interface ICountEmployee {
  code?: string;
  count?: number;
}

export class CountEmployee implements ICountEmployee {
  constructor(public code?: string, public count?: number) {}
}
