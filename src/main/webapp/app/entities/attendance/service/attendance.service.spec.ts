import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IAttendance, Attendance } from '../attendance.model';

import { AttendanceService } from './attendance.service';

describe('Attendance Service', () => {
  let service: AttendanceService;
  let httpMock: HttpTestingController;
  let elemDefault: IAttendance;
  let expectedResult: IAttendance | IAttendance[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AttendanceService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      employeeId: 0,
      inTime: currentDate,
      outTime: currentDate,
      note: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          inTime: currentDate.format(DATE_FORMAT),
          outTime: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Attendance', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          inTime: currentDate.format(DATE_FORMAT),
          outTime: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          inTime: currentDate,
          outTime: currentDate,
        },
        returnedFromService
      );

      service.create(new Attendance()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Attendance', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          employeeId: 1,
          inTime: currentDate.format(DATE_FORMAT),
          outTime: currentDate.format(DATE_FORMAT),
          note: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          inTime: currentDate,
          outTime: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Attendance', () => {
      const patchObject = Object.assign(
        {
          employeeId: 1,
          inTime: currentDate.format(DATE_FORMAT),
          outTime: currentDate.format(DATE_FORMAT),
        },
        new Attendance()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          inTime: currentDate,
          outTime: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Attendance', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          employeeId: 1,
          inTime: currentDate.format(DATE_FORMAT),
          outTime: currentDate.format(DATE_FORMAT),
          note: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          inTime: currentDate,
          outTime: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Attendance', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAttendanceToCollectionIfMissing', () => {
      it('should add a Attendance to an empty array', () => {
        const attendance: IAttendance = { id: 123 };
        expectedResult = service.addAttendanceToCollectionIfMissing([], attendance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(attendance);
      });

      it('should not add a Attendance to an array that contains it', () => {
        const attendance: IAttendance = { id: 123 };
        const attendanceCollection: IAttendance[] = [
          {
            ...attendance,
          },
          { id: 456 },
        ];
        expectedResult = service.addAttendanceToCollectionIfMissing(attendanceCollection, attendance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Attendance to an array that doesn't contain it", () => {
        const attendance: IAttendance = { id: 123 };
        const attendanceCollection: IAttendance[] = [{ id: 456 }];
        expectedResult = service.addAttendanceToCollectionIfMissing(attendanceCollection, attendance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(attendance);
      });

      it('should add only unique Attendance to an array', () => {
        const attendanceArray: IAttendance[] = [{ id: 123 }, { id: 456 }, { id: 32491 }];
        const attendanceCollection: IAttendance[] = [{ id: 123 }];
        expectedResult = service.addAttendanceToCollectionIfMissing(attendanceCollection, ...attendanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const attendance: IAttendance = { id: 123 };
        const attendance2: IAttendance = { id: 456 };
        expectedResult = service.addAttendanceToCollectionIfMissing([], attendance, attendance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(attendance);
        expect(expectedResult).toContain(attendance2);
      });

      it('should accept null and undefined values', () => {
        const attendance: IAttendance = { id: 123 };
        expectedResult = service.addAttendanceToCollectionIfMissing([], null, attendance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(attendance);
      });

      it('should return initial array if no Attendance is added', () => {
        const attendanceCollection: IAttendance[] = [{ id: 123 }];
        expectedResult = service.addAttendanceToCollectionIfMissing(attendanceCollection, undefined, null);
        expect(expectedResult).toEqual(attendanceCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
