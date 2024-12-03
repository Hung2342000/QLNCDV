import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getSalaryIdentifier, ISalary } from '../salary.model';
import { getEmployeeIdentifier } from '../../employee/employee.model';
import { getSalaryDetailIdentifier, ISalaryDetail } from '../salaryDetail.model';

export type EntityResponseType = HttpResponse<ISalary>;
export type EntityDetailResponseType = HttpResponse<ISalaryDetail>;
export type EntityArrayResponseType = HttpResponse<ISalary[]>;
export type EntityDetailArrayResponseType = HttpResponse<ISalaryDetail[]>;

@Injectable({ providedIn: 'root' })
export class SalaryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/salary');
  protected resourceUrlDetail = this.applicationConfigService.getEndpointFor('api/salary-detail');
  protected resourceUrlDepartment = this.applicationConfigService.getEndpointFor('api/department/all');
  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(employee: ISalary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employee);
    return this.http
      .post<ISalary>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(salary: ISalary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salary);
    return this.http
      .put<ISalary>(`${this.resourceUrl}/${getSalaryIdentifier(salary) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  updateDetail(salaryDetail: ISalaryDetail): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salaryDetail);
    return this.http
      .put<ISalary>(`${this.resourceUrlDetail}/${getSalaryDetailIdentifier(salaryDetail) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
  partialUpdate(employee: ISalary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employee);
    return this.http
      .patch<ISalary>(`${this.resourceUrl}/${getSalaryIdentifier(employee) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ISalary>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISalary[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryDetail(req?: any): Observable<EntityDetailArrayResponseType> {
    return this.http
      .get<ISalaryDetail[]>(this.resourceUrlDetail, { params: req, observe: 'response' })
      .pipe(map((res: EntityDetailArrayResponseType) => res));
  }

  queryAll(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISalary[]>(this.resourceUrl + '/all', { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEmployeeToCollectionIfMissing(employeeCollection: ISalary[], ...employeesToCheck: (ISalary | null | undefined)[]): ISalary[] {
    const employees: ISalary[] = employeesToCheck.filter(isPresent);
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

  protected convertDateFromClient(salary: ISalary): ISalary {
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
      res.body.forEach((salary: ISalary) => {
        salary.createDate = salary.createDate ? dayjs(salary.createDate) : undefined;
      });
    }
    return res;
  }
}
