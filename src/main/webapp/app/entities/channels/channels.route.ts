import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Channels } from 'app/shared/model/channels.model';
import { ChannelsService } from './channels.service';
import { ChannelsComponent } from './channels.component';
import { ChannelsDetailComponent } from './channels-detail.component';
import { ChannelsUpdateComponent } from './channels-update.component';
import { ChannelsDeletePopupComponent } from './channels-delete-dialog.component';
import { IChannels } from 'app/shared/model/channels.model';

@Injectable({ providedIn: 'root' })
export class ChannelsResolve implements Resolve<IChannels> {
  constructor(private service: ChannelsService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IChannels> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Channels>) => response.ok),
        map((channels: HttpResponse<Channels>) => channels.body)
      );
    }
    return of(new Channels());
  }
}

export const channelsRoute: Routes = [
  {
    path: '',
    component: ChannelsComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'sampleMultitenancyAppAngularApp.channels.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ChannelsDetailComponent,
    resolve: {
      channels: ChannelsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'sampleMultitenancyAppAngularApp.channels.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ChannelsUpdateComponent,
    resolve: {
      channels: ChannelsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'sampleMultitenancyAppAngularApp.channels.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ChannelsUpdateComponent,
    resolve: {
      channels: ChannelsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'sampleMultitenancyAppAngularApp.channels.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const channelsPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ChannelsDeletePopupComponent,
    resolve: {
      channels: ChannelsResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'sampleMultitenancyAppAngularApp.channels.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
