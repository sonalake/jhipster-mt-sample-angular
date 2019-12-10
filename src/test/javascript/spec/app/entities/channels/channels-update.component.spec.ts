import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { SampleMultitenancyAppAngularTestModule } from '../../../test.module';
import { ChannelsUpdateComponent } from 'app/entities/channels/channels-update.component';
import { ChannelsService } from 'app/entities/channels/channels.service';
import { Channels } from 'app/shared/model/channels.model';

describe('Component Tests', () => {
  describe('Channels Management Update Component', () => {
    let comp: ChannelsUpdateComponent;
    let fixture: ComponentFixture<ChannelsUpdateComponent>;
    let service: ChannelsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SampleMultitenancyAppAngularTestModule],
        declarations: [ChannelsUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ChannelsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ChannelsUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ChannelsService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Channels(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Channels();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
