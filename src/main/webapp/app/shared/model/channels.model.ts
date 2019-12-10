import { IUser } from 'app/core/user/user.model';
import { ICompany } from 'app/shared/model/company.model';

export interface IChannels {
  id?: number;
  name?: string;
  users?: IUser[];
  company?: ICompany;
}

export class Channels implements IChannels {
  constructor(public id?: number, public name?: string, public users?: IUser[], public company?: ICompany) {}
}
