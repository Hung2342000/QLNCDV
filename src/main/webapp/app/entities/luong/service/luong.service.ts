import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getSalaryIdentifier, ILuong } from '../luong.model';
import { getEmployeeIdentifier } from '../../employee/employee.model';
import { ILuongDetail } from '../luongDetail.model';
import { IAttendanceDetail } from '../../attendance/attendanceDetail.model';
import { EntityArrayResponseDetailType } from '../../attendance/service/attendance.service';
import { ISalary } from '../../salary/salary.model';
import { ISalaryDetail } from '../../salary/salaryDetail.model';
import { ILuongDto } from '../luongDto.model';

export type EntityResponseType = HttpResponse<ILuong>;
export type EntityDetailResponseType = HttpResponse<ILuongDetail>;
export type EntityArrayResponseType = HttpResponse<ILuong[]>;
export type EntityDetailArrayResponseType = HttpResponse<ILuongDetail[]>;

@Injectable({ providedIn: 'root' })
export class LuongService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/luong');
  protected resourceUrlDetail = this.applicationConfigService.getEndpointFor('api/luọng-detail');
  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}
  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILuong>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
  queryAll(id: number): Observable<EntityDetailArrayResponseType> {
    return this.http.get<ILuongDetail[]>(`${this.resourceUrlDetail}/all/${id}`, { observe: 'response' });
  }
  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  createAllDetailImportLuong(luongDto: ILuongDto): Observable<any> {
    return this.http.post<any>(this.resourceUrl + '/all/import', luongDto);
  }
  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILuong[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  querydot2(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILuong[]>(this.resourceUrl + '/dot2', { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addEmployeeToCollectionIfMissing(employeeCollection: ILuong[], ...employeesToCheck: (ILuong | null | undefined)[]): ILuong[] {
    const employees: ILuong[] = employeesToCheck.filter(isPresent);
    if (employees.length > 0) {
      const employeeCollectionIdentifiers = employeeCollection.map(employeeItem => getEmployeeIdentifier(employeeItem)!);
      const employeesToAdd = employees.filter(employeeItem => {
        const employeeIdentifier = getEmployeeIdentifier(employeeItem);
        if (employeeIdentifier == null || employeeCollectionIdentifiers.includes(employeeIdentifier)) {
          return false;
        }
        employeeCollectionIdentifiers.push(employeeIdentifier);
        return true;
      });
      return [...employeesToAdd, ...employeeCollection];
    }
    return employeeCollection;
  }

  protected convertDateFromClient(salary: ILuong): ILuong {
    return Object.assign({}, salary, {
      createDate: salary.createDate ? dayjs(salary.createDate).format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createDate = res.body.createDate ? dayjs(res.body.createDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((salary: ILuong) => {
        salary.createDate = salary.createDate ? dayjs(salary.createDate) : undefined;
      });
    }
    return res;
  }
}
