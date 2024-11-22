import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAttendance, getAttendanceIdentifier } from '../attendance.model';

export type EntityResponseType = HttpResponse<IAttendance>;
export type EntityArrayResponseType = HttpResponse<IAttendance[]>;

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/attendances');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(attendance: IAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(attendance);
    return this.http
      .post<IAttendance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(attendance: IAttendance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(attendance);
    return this.http
      .put<IAttendance>(`${this.resourceUrl}/${getAttendanceIdentifier(attendance) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
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

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAttendance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
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
      inTime: attendance.inTime?.isValid() ? attendance.inTime.format(DATE_FORMAT) : undefined,
      outTime: attendance.outTime?.isValid() ? attendance.outTime.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.inTime = res.body.inTime ? dayjs(res.body.inTime) : undefined;
      res.body.outTime = res.body.outTime ? dayjs(res.body.outTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((attendance: IAttendance) => {
        attendance.inTime = attendance.inTime ? dayjs(attendance.inTime) : undefined;
        attendance.outTime = attendance.outTime ? dayjs(attendance.outTime) : undefined;
      });
    }
    return res;
  }
}
