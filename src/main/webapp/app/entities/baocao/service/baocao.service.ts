import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getEmployeeIdentifier } from '../../employee/employee.model';
import { IBaoCao } from '../baocao.model';
import { ISalary } from '../../salary/salary.model';

export type EntityResponseType = HttpResponse<IBaoCao>;
export type EntityArrayResponseType = HttpResponse<IBaoCao[]>;

@Injectable({ providedIn: 'root' })
export class BaocaoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/baocao');
  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBaoCao[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => res));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISalary>(`${this.resourceUrl}/${id}`, { observe: 'response' }).pipe(map((res: EntityResponseType) => res));
  }
}
