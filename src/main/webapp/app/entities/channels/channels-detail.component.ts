import { Component, OnInit } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';
import { ActivatedRoute } from '@angular/router';

import { IChannels } from 'app/shared/model/channels.model';

@Component({
  selector: 'jhi-channels-detail',
  templateUrl: './channels-detail.component.html'
})
export class ChannelsDetailComponent implements OnInit {
  currentAccount: any;
  channels: IChannels;

  constructor(protected activatedRoute: ActivatedRoute, protected accountService: AccountService) {}

  ngOnInit() {
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.activatedRoute.data.subscribe(({ channels }) => {
      this.channels = channels;
    });
  }

  previousState() {
    window.history.back();
  }
}
