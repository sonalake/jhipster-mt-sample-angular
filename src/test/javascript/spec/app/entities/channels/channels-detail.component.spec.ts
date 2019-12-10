import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SampleMultitenancyAppAngularTestModule } from '../../../test.module';
import { ChannelsDetailComponent } from 'app/entities/channels/channels-detail.component';
import { Channels } from 'app/shared/model/channels.model';

describe('Component Tests', () => {
  describe('Channels Management Detail Component', () => {
    let comp: ChannelsDetailComponent;
    let fixture: ComponentFixture<ChannelsDetailComponent>;
    const route = ({ data: of({ channels: new Channels(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SampleMultitenancyAppAngularTestModule],
        declarations: [ChannelsDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ChannelsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ChannelsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.channels).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
