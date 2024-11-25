import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
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

export type EntityResponseType = HttpResponse<IAttendance>;
export type EntityResponseDetailType = HttpResponse<IAttendanceDetail>;
export type EntityArrayResponseType = HttpResponse<IAttendance[]>;
export type EntityArrayResponseDetailType = HttpResponse<IAttendanceDetail[]>;

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/attendances');
  protected resourceUrlDetail = this.applicationConfigService.getEndpointFor('api/attendanceDetail');
  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(attendance: IAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(attendance);
    return this.http
      .post<IAttendance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  createDetail(attendanceDetail: IAttendanceDetail): Observable<EntityResponseType> {
    const copy = this.convertDateDetailFromClient(attendanceDetail);
    return this.http
      .post<IAttendanceDetail>(this.resourceUrlDetail, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateDetailFromServer(res)));
  }

  update(attendance: IAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(attendance);
    return this.http
      .put<IAttendance>(`${this.resourceUrl}/${getAttendanceIdentifier(attendance) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  updateDetail(attendanceDetail: IAttendanceDetail): Observable<EntityResponseType> {
    const copy = this.convertDateDetailFromClient(attendanceDetail);
    return this.http
      .put<IAttendanceDetail>(`${this.resourceUrlDetail}/${getAttendanceDetailIdentifier(attendanceDetail) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateDetailFromServer(res)));
  }
  partialUpdate(attendance: IAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(attendance);
    return this.http
      .patch<IAttendance>(`${this.resourceUrl}/${getAttendanceIdentifier(attendance) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAttendance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  queryAttendanceDetail(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<IAttendanceDetail[]>(`${this.resourceUrlDetail}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityArrayResponseDetailType) => this.convertDateArrayDetailFromServer(res)));
  }
  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAttendance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
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

  protected convertDateFromClient(attendance: IAttendance): IAttendance {
    return Object.assign({}, attendance, {
      inTime: attendance.month?.isValid() ? attendance.month.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateDetailFromClient(attendanceDetail: IAttendanceDetail): IAttendance {
    return Object.assign({}, attendanceDetail, {
      time: attendanceDetail.time ? dayjs(attendanceDetail.time).format(DATE_FORMAT) : undefined,
      inTime: typeof attendanceDetail.inTime !== 'string' ? dayjs(attendanceDetail.inTime).format(TIME_FORMAT) : attendanceDetail.inTime,
      outTime:
        typeof attendanceDetail.outTime !== 'string' ? dayjs(attendanceDetail.outTime).format(TIME_FORMAT) : attendanceDetail.outTime,
    });
  }
  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.month = res.body.month ? dayjs(res.body.month) : undefined;
    }
    return res;
  }
  protected convertDateDetailFromServer(res: EntityResponseDetailType): EntityResponseDetailType {
    if (res.body) {
      res.body.time = res.body.time ? dayjs(res.body.inTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((attendance: IAttendance) => {
        attendance.month = attendance.month ? dayjs(attendance.month) : undefined;
      });
    }
    return res;
  }

  protected convertDateArrayDetailFromServer(res: EntityArrayResponseDetailType): EntityArrayResponseDetailType {
    if (res.body) {
      res.body.forEach((attendance: IAttendanceDetail) => {
        attendance.time = attendance.time ? dayjs(attendance.time) : undefined;
      });
    }
    return res;
  }
}
