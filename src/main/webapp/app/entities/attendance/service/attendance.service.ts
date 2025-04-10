import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT, DATE_TIME_FORMAT, TIME_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAttendance, getAttendanceIdentifier } from '../attendance.model';
import { IDepartment } from '../../employee/department.model';
import { getAttendanceDetailIdentifier, IAttendanceDetail } from '../attendanceDetail.model';
import { ISalary } from '../../salary/salary.model';
import { INgayNghiLe } from '../ngay-nghi-le.model';
import { IEmployee } from '../../employee/employee.model';

export type EntityResponseType = HttpResponse<IAttendance>;
export type EntityResponseTypeNgayNghiLe = HttpResponse<INgayNghiLe>;
export type EntityResponseDetailType = HttpResponse<IAttendanceDetail>;
export type EntityArrayResponseType = HttpResponse<IAttendance[]>;
export type EntityArrayResponseDetailType = HttpResponse<IAttendanceDetail[]>;
export type EntityArrayResponseTypeNgayNghiLe = HttpResponse<INgayNghiLe[]>;

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/attendances');
  protected resourceUrlNgayNghiLe = this.applicationConfigService.getEndpointFor('api/ngay-nghi-le');
  protected resourceUrlDetail = this.applicationConfigService.getEndpointFor('api/attendanceDetail');
  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(attendance: IAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(attendance);
    return this.http.post<IAttendance>(this.resourceUrl, attendance, { observe: 'response' }).pipe(map((res: EntityResponseType) => res));
  }

  createNgayNghiLe(ngayNghiLe: INgayNghiLe): Observable<EntityResponseTypeNgayNghiLe> {
    const copy = this.convertDateFromClientNgayNghiLe(ngayNghiLe);
    return this.http
      .post<INgayNghiLe>(this.resourceUrlNgayNghiLe, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseTypeNgayNghiLe) => res));
  }
  createAllDetail(attendanceDetails: IAttendanceDetail[]): Observable<any> {
    return this.http.post<any>(this.resourceUrlDetail + '/all', attendanceDetails);
  }

  update(attendance: IAttendance): Observable<EntityResponseType> {
    return this.http
      .put<IAttendance>(`${this.resourceUrl}/${getAttendanceIdentifier(attendance) as number}`, attendance, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => res));
  }

  updateNgayNghiLe(ngayNghiLe: INgayNghiLe): Observable<EntityResponseTypeNgayNghiLe> {
    return this.http
      .put<INgayNghiLe>(`${this.resourceUrlNgayNghiLe}/${getAttendanceIdentifier(ngayNghiLe) as number}`, ngayNghiLe, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseTypeNgayNghiLe) => res));
  }
  partialUpdate(attendance: IAttendance): Observable<EntityResponseType> {
    return this.http
      .patch<IAttendance>(`${this.resourceUrl}/${getAttendanceIdentifier(attendance) as number}`, attendance, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => res));
  }

  exportToExcel(req?: any): Observable<any> {
    const options = createRequestOption(req);
    return this.http.get(`${this.resourceUrl}/export`, {
      params: options,
      responseType: 'blob',
      headers: new HttpHeaders().append('Content-Type', 'application/json'),
    });
  }
  exportToPdf(req?: any): Observable<any> {
    const options = createRequestOption(req);
    return this.http.get(`${this.resourceUrl}/export/pdf`, {
      params: options,
      responseType: 'blob',
      headers: new HttpHeaders().append('Content-Type', 'application/json'),
    });
  }
  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAttendance>(`${this.resourceUrl}/${id}`, { observe: 'response' }).pipe(map((res: EntityResponseType) => res));
  }
  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAttendance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => res));
  }

  queryNgayNghiLe(req?: any): Observable<EntityArrayResponseTypeNgayNghiLe> {
    const options = createRequestOption(req);
    return this.http
      .get<INgayNghiLe[]>(this.resourceUrlNgayNghiLe, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseTypeNgayNghiLe) => this.convertDateArrayFromServer(res)));
  }

  queryAll(id: number, options?: any): Observable<EntityArrayResponseDetailType> {
    return this.http.get<IAttendanceDetail[]>(`${this.resourceUrlDetail}/all/${id}`, { params: options, observe: 'response' });
  }
  queryAttendanceAll(): Observable<EntityArrayResponseType> {
    return this.http.get<IAttendance[]>(`${this.resourceUrl}/all`, { observe: 'response' });
  }
  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
  deleteNgayNghiLe(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrlNgayNghiLe}/${id}`, { observe: 'response' });
  }
  deleteDetail(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrlDetail}/${id}`, { observe: 'response' });
  }

  addAttendanceToCollectionIfMissing(
    attendanceCollection: IAttendance[],
    ...attendancesToCheck: (IAttendance | null | undefined)[]
  ): IAttendance[] {
    const attendances: IAttendance[] = attendancesToCheck.filter(isPresent);
    if (attendances.length > 0) {
      const attendanceCollectionIdentifiers = attendanceCollection.map(attendanceItem => getAttendanceIdentifier(attendanceItem)!);
      const attendancesToAdd = attendances.filter(attendanceItem => {
        const attendanceIdentifier = getAttendanceIdentifier(attendanceItem);
        if (attendanceIdentifier == null || attendanceCollectionIdentifiers.includes(attendanceIdentifier)) {
          return false;
        }
        attendanceCollectionIdentifiers.push(attendanceIdentifier);
        return true;
      });
      return [...attendancesToAdd, ...attendanceCollection];
    }
    return attendanceCollection;
  }

  createAll(attendanceDetails: IAttendanceDetail[]): Observable<any> {
    return this.http.post<any>(this.resourceUrlDetail + '/all', attendanceDetails);
  }

  protected convertDateFromClient(attendance: IAttendance): ISalary {
    return Object.assign({}, attendance, {
      createDate:
        attendance.createDate === undefined
          ? undefined
          : typeof attendance.createDate === 'string'
          ? dayjs(attendance.createDate, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(attendance.createDate).format(DATE_FORMAT),
    });
  }

  protected convertDateFromClientNgayNghiLe(ngayNghiLe: INgayNghiLe): INgayNghiLe {
    return Object.assign({}, ngayNghiLe, {
      holidayDate:
        ngayNghiLe.holidayDate === undefined
          ? undefined
          : typeof ngayNghiLe.holidayDate === 'string'
          ? dayjs(ngayNghiLe.holidayDate, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(ngayNghiLe.holidayDate).format(DATE_FORMAT),
    });
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseTypeNgayNghiLe): EntityArrayResponseTypeNgayNghiLe {
    if (res.body) {
      res.body.forEach((ngayNghiLe: INgayNghiLe) => {
        ngayNghiLe.holidayDate = ngayNghiLe.holidayDate ? dayjs(ngayNghiLe.holidayDate) : undefined;
      });
    }
    return res;
  }
}
