import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'company',
        loadChildren: () => import('./company/company.module').then(m => m.SampleMultitenancyAppAngularCompanyModule)
      },
      {
        path: 'channels',
        loadChildren: () => import('./channels/channels.module').then(m => m.SampleMultitenancyAppAngularChannelsModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class SampleMultitenancyAppAngularEntityModule {}
