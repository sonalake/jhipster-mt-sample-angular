import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SampleMultitenancyAppAngularTestModule } from '../../../test.module';
import { CompanyDeleteDialogComponent } from 'app/entities/company/company-delete-dialog.component';
import { CompanyService } from 'app/entities/company/company.service';

describe('Component Tests', () => {
  describe('Company Management Delete Component', () => {
    let comp: CompanyDeleteDialogComponent;
    let fixture: ComponentFixture<CompanyDeleteDialogComponent>;
    let service: CompanyService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SampleMultitenancyAppAngularTestModule],
        declarations: [CompanyDeleteDialogComponent]
      })
        .overrideTemplate(CompanyDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CompanyDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CompanyService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
