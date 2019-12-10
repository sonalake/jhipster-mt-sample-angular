import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IChannels } from 'app/shared/model/channels.model';
import { ChannelsService } from './channels.service';

@Component({
  selector: 'jhi-channels-delete-dialog',
  templateUrl: './channels-delete-dialog.component.html'
})
export class ChannelsDeleteDialogComponent {
  channels: IChannels;

  constructor(protected channelsService: ChannelsService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.channelsService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'channelsListModification',
        content: 'Deleted an channels'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-channels-delete-popup',
  template: ''
})
export class ChannelsDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ channels }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ChannelsDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.channels = channels;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/channels', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/channels', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
