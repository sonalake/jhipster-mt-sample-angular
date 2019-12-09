import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from './company.service';

@Component({
  selector: 'jhi-company-delete-dialog',
  templateUrl: './company-delete-dialog.component.html'
})
export class CompanyDeleteDialogComponent {
  company: ICompany;

  constructor(protected companyService: CompanyService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.companyService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'companyListModification',
        content: 'Deleted an company'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-company-delete-popup',
  template: ''
})
export class CompanyDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ company }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(CompanyDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.company = company;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/company', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/company', { outlets: { popup: null } }]);
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
