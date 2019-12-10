import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SampleMultitenancyAppAngularTestModule } from '../../../test.module';
import { ChannelsComponent } from 'app/entities/channels/channels.component';
import { ChannelsService } from 'app/entities/channels/channels.service';
import { Channels } from 'app/shared/model/channels.model';

describe('Component Tests', () => {
  describe('Channels Management Component', () => {
    let comp: ChannelsComponent;
    let fixture: ComponentFixture<ChannelsComponent>;
    let service: ChannelsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SampleMultitenancyAppAngularTestModule],
        declarations: [ChannelsComponent],
        providers: []
      })
        .overrideTemplate(ChannelsComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ChannelsComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ChannelsService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Channels(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.channels[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
