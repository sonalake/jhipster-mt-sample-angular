export interface ICompany {
  id?: number;
}

export class Company implements ICompany {
  constructor(public id?: number) {}
}
