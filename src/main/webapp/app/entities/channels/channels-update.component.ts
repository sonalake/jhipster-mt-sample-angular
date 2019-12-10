import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IChannels, Channels } from 'app/shared/model/channels.model';
import { ChannelsService } from './channels.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

@Component({
  selector: 'jhi-channels-update',
  templateUrl: './channels-update.component.html'
})
export class ChannelsUpdateComponent implements OnInit {
  currentAccount: any;

  isSaving: boolean;

  users: IUser[];

  companies: ICompany[];

  editForm = this.fb.group({
    id: [],
    name: [],
    users: [],
    company: [null, Validators.required]
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected channelsService: ChannelsService,
    protected userService: UserService,
    protected companyService: CompanyService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
    private accountService: AccountService
  ) {
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
  }

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ channels }) => {
      if (this.currentAccount.company) {
        channels.company = this.currentAccount.company;
      }
      this.updateForm(channels);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    if (this.currentAccount.company) {
      return;
    }

    this.companyService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<ICompany[]>) => mayBeOk.ok),
        map((response: HttpResponse<ICompany[]>) => response.body)
      )
      .subscribe((res: ICompany[]) => (this.companies = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(channels: IChannels) {
    this.editForm.patchValue({
      id: channels.id,
      name: channels.name,
      users: channels.users,
      company: channels.company
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const channels = this.createFromForm();
    if (channels.id !== undefined) {
      this.subscribeToSaveResponse(this.channelsService.update(channels));
    } else {
      this.subscribeToSaveResponse(this.channelsService.create(channels));
    }
  }

  private createFromForm(): IChannels {
    return {
      ...new Channels(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      users: this.editForm.get(['users']).value,
      company: this.editForm.get(['company']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChannels>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  trackCompanyById(index: number, item: ICompany) {
    return item.id;
  }

  getSelected(selectedVals: any[], option: any) {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
