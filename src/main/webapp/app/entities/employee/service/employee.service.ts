import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEmployee, getEmployeeIdentifier } from '../employee.model';
import { IDepartment } from '../department.model';
import { ICountEmployee } from '../count-employee.model';
import { ISalaryDetail } from '../../salary/salaryDetail.model';
import { IServiceType } from '../service-type.model';
import { ITransfer } from '../transfer.model';

export type EntityResponseType = HttpResponse<IEmployee>;
export type EntityArrayResponseType = HttpResponse<IEmployee[]>;
export type EntityArrayResponseDepartmentType = HttpResponse<IDepartment[]>;
export type EntityArrayResponseServiceType = HttpResponse<IServiceType[]>;
export type EntityArrayResponseCountEmployeeType = HttpResponse<ICountEmployee[]>;

export type EntityResponseTypeTransfer = HttpResponse<ITransfer>;

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/employees');
  protected resourceTransferUrl = this.applicationConfigService.getEndpointFor('api/transfer');
  protected resourceUrlDepartment = this.applicationConfigService.getEndpointFor('api/department/all');
  protected resourceUrlServiceType = this.applicationConfigService.getEndpointFor('api/serviceType/all');
  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(employee: IEmployee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employee);
    return this.http
      .post<IEmployee>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
  createTransfer(transfer: ITransfer): Observable<EntityResponseTypeTransfer> {
    const copy = this.convertDateFromClientTransfer(transfer);
    return this.http
      .post<ITransfer>(this.resourceTransferUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseTypeTransfer) => this.convertDateFromServer(res)));
  }

  update(employee: IEmployee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employee);
    return this.http
      .put<IEmployee>(`${this.resourceUrl}/${getEmployeeIdentifier(employee) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  exportToExcel(req?: any): Observable<any> {
    return this.http.get(`${this.resourceUrl}/export`, {
      params: req,
      responseType: 'blob',
      headers: new HttpHeaders().append('Content-Type', 'application/json'),
    });
  }
  partialUpdate(employee: IEmployee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(employee);
    return this.http
      .patch<IEmployee>(`${this.resourceUrl}/${getEmployeeIdentifier(employee) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  queryDepartment(): Observable<EntityArrayResponseDepartmentType> {
    return this.http.get<IDepartment[]>(this.resourceUrlDepartment, { observe: 'response' });
  }
  queryServiceType(): Observable<EntityArrayResponseServiceType> {
    return this.http.get<IDepartment[]>(this.resourceUrlServiceType, { observe: 'response' });
  }
  createAll(employees: IEmployee[]): Observable<any> {
    const employeesCopy = this.convertDateArrayFromServeExcel(employees);
    return this.http.post<any>(this.resourceUrl + '/all', employeesCopy);
  }
  queryServiceTypeCustom(): Observable<EntityArrayResponseServiceType> {
    return this.http.get<IDepartment[]>(this.resourceUrlServiceType + '/custom', { observe: 'response' });
  }

  queryCountEmployee(): Observable<EntityArrayResponseCountEmployeeType> {
    return this.http.get<ICountEmployee[]>(this.resourceUrl + '/count', { observe: 'response' });
  }
  queryCountEmployeeByGroup(req?: any): Observable<EntityArrayResponseCountEmployeeType> {
    return this.http.get<ICountEmployee[]>(this.resourceUrl + '/count-group', { params: req, observe: 'response' });
  }
  queryCountEmployeeByStatus(req?: any): Observable<EntityArrayResponseCountEmployeeType> {
    return this.http.get<ICountEmployee[]>(this.resourceUrl + '/count-status', { params: req, observe: 'response' });
  }
  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEmployee>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEmployee[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }
  queryBox(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEmployee[]>(this.resourceUrl + '/box', { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryAll(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEmployee[]>(this.resourceUrl + '/all', { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEmployeeToCollectionIfMissing(employeeCollection: IEmployee[], ...employeesToCheck: (IEmployee | null | undefined)[]): IEmployee[] {
    const employees: IEmployee[] = employeesToCheck.filter(isPresent);
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

  protected convertDateFromClient(employee: IEmployee): IEmployee {
    return Object.assign({}, employee, {
      birthday:
        employee.birthday === undefined
          ? undefined
          : typeof employee.birthday === 'string'
          ? dayjs(employee.birthday, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(employee.birthday).format(DATE_FORMAT),
      startDate:
        employee.startDate === undefined
          ? undefined
          : typeof employee.startDate === 'string'
          ? dayjs(employee.startDate, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(employee.startDate).format(DATE_FORMAT),
      closeDate:
        employee.closeDate === undefined
          ? undefined
          : typeof employee.closeDate === 'string'
          ? dayjs(employee.closeDate, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(employee.closeDate).format(DATE_FORMAT),
      ngayNghiSinh:
        employee.ngayNghiSinh === undefined
          ? undefined
          : typeof employee.ngayNghiSinh === 'string'
          ? dayjs(employee.ngayNghiSinh, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(employee.ngayNghiSinh).format(DATE_FORMAT),
      ngayDieuChuyen:
        employee.ngayDieuChuyen === undefined
          ? undefined
          : typeof employee.ngayDieuChuyen === 'string'
          ? dayjs(employee.ngayDieuChuyen, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(employee.ngayDieuChuyen).format(DATE_FORMAT),
    });
  }

  protected convertDateFromClientTransfer(transfer: ITransfer): ITransfer {
    return Object.assign({}, transfer, {
      startDate:
        transfer.startDate === undefined
          ? undefined
          : typeof transfer.startDate === 'string'
          ? dayjs(transfer.startDate, 'DD-MM-YYYY').format(DATE_FORMAT)
          : dayjs(transfer.startDate).format(DATE_FORMAT),
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.birthday = res.body.birthday ? dayjs(res.body.birthday) : undefined;
      res.body.startDate = res.body.startDate ? dayjs(res.body.startDate) : undefined;
      res.body.closeDate = res.body.closeDate ? dayjs(res.body.closeDate) : undefined;
      res.body.ngayNghiSinh = res.body.ngayNghiSinh ? dayjs(res.body.ngayNghiSinh) : undefined;
      res.body.ngayDieuChuyen = res.body.ngayDieuChuyen ? dayjs(res.body.ngayDieuChuyen) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((employee: IEmployee) => {
        employee.birthday = employee.birthday ? dayjs(employee.birthday) : undefined;
        employee.startDate = employee.startDate ? dayjs(employee.startDate) : undefined;
        employee.closeDate = employee.closeDate ? dayjs(employee.closeDate) : undefined;
        employee.ngayNghiSinh = employee.ngayNghiSinh ? dayjs(employee.ngayNghiSinh) : undefined;
        employee.ngayDieuChuyen = employee.ngayDieuChuyen ? dayjs(employee.ngayDieuChuyen) : undefined;
      });
    }
    return res;
  }

  protected convertDateArrayFromServeExcel(res: IEmployee[]): IEmployee[] {
    res.forEach((employee: IEmployee) => {
      employee.birthday = employee.birthday ? dayjs(employee.birthday, 'DD/MM/YYYY').add(1, 'day') : undefined;
      employee.startDate = employee.startDate ? dayjs(employee.startDate, 'DD/MM/YYYY').add(1, 'day') : undefined;
      employee.closeDate = employee.closeDate ? dayjs(employee.closeDate, 'DD/MM/YYYY').add(1, 'day') : undefined;
      employee.ngayNghiSinh = employee.ngayNghiSinh ? dayjs(employee.ngayNghiSinh, 'DD/MM/YYYY').add(1, 'day') : undefined;
    });
    return res;
  }
}
