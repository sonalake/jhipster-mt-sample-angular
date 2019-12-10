import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IChannels } from 'app/shared/model/channels.model';
import { AccountService } from 'app/core/auth/account.service';
import { ChannelsService } from './channels.service';

@Component({
  selector: 'jhi-channels',
  templateUrl: './channels.component.html'
})
export class ChannelsComponent implements OnInit, OnDestroy {
  channels: IChannels[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected channelsService: ChannelsService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.channelsService
      .query()
      .pipe(
        filter((res: HttpResponse<IChannels[]>) => res.ok),
        map((res: HttpResponse<IChannels[]>) => res.body)
      )
      .subscribe(
        (res: IChannels[]) => {
          this.channels = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInChannels();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IChannels) {
    return item.id;
  }

  registerChangeInChannels() {
    this.eventSubscriber = this.eventManager.subscribe('channelsListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
