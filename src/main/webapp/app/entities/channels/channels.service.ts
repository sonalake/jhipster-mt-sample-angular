import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IChannels } from 'app/shared/model/channels.model';

type EntityResponseType = HttpResponse<IChannels>;
type EntityArrayResponseType = HttpResponse<IChannels[]>;

@Injectable({ providedIn: 'root' })
export class ChannelsService {
  public resourceUrl = SERVER_API_URL + 'api/channels';

  constructor(protected http: HttpClient) {}

  create(channels: IChannels): Observable<EntityResponseType> {
    return this.http.post<IChannels>(this.resourceUrl, channels, { observe: 'response' });
  }

  update(channels: IChannels): Observable<EntityResponseType> {
    return this.http.put<IChannels>(this.resourceUrl, channels, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IChannels>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IChannels[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
