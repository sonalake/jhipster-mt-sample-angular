import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SampleMultitenancyAppAngularSharedModule } from 'app/shared/shared.module';
import { ChannelsComponent } from './channels.component';
import { ChannelsDetailComponent } from './channels-detail.component';
import { ChannelsUpdateComponent } from './channels-update.component';
import { ChannelsDeletePopupComponent, ChannelsDeleteDialogComponent } from './channels-delete-dialog.component';
import { channelsRoute, channelsPopupRoute } from './channels.route';

const ENTITY_STATES = [...channelsRoute, ...channelsPopupRoute];

@NgModule({
  imports: [SampleMultitenancyAppAngularSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ChannelsComponent,
    ChannelsDetailComponent,
    ChannelsUpdateComponent,
    ChannelsDeleteDialogComponent,
    ChannelsDeletePopupComponent
  ],
  entryComponents: [ChannelsDeleteDialogComponent]
})
export class SampleMultitenancyAppAngularChannelsModule {}
