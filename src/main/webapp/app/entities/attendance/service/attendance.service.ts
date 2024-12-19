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
    return this.http.post<IAttendance>(this.resourceUrl, attendance, { observe: 'response' }).pipe(map((res: EntityResponseType) => res));
  }

  createAllDetail(attendanceDetails: IAttendanceDetail[]): Observable<any> {
    return this.http.post<any>(this.resourceUrlDetail + '/all', attendanceDetails);
  }

  update(attendance: IAttendance): Observable<EntityResponseType> {
    return this.http
      .put<IAttendance>(`${this.resourceUrl}/${getAttendanceIdentifier(attendance) as number}`, attendance, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => res));
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
  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAttendance>(`${this.resourceUrl}/${id}`, { observe: 'response' }).pipe(map((res: EntityResponseType) => res));
  }
  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAttendance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => res));
  }

  queryAll(id: number): Observable<EntityArrayResponseDetailType> {
    return this.http.get<IAttendanceDetail[]>(`${this.resourceUrlDetail}/all/${id}`, { observe: 'response' });
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

  createAll(attendanceDetails: IAttendanceDetail[]): Observable<any> {
    return this.http.post<any>(this.resourceUrlDetail + '/all', attendanceDetails);
  }

  protected convertDateFromClient(attendance: IAttendance): ISalary {
    return Object.assign({}, attendance, {
      createDate: attendance.createDate ? dayjs(attendance.createDate).format(DATE_FORMAT) : undefined,
    });
  }
}
