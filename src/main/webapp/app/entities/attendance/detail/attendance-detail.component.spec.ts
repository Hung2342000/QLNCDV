import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AttendanceDetailComponent } from './attendance-detail.component';

describe('Attendance Management Detail Component', () => {
  let comp: AttendanceDetailComponent;
  let fixture: ComponentFixture<AttendanceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AttendanceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ attendance: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AttendanceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AttendanceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load attendance on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.attendance).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
