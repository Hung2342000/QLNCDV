import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { SalaryService } from '../service/salary.service';
import { ISalary, Salary } from '../salary.model';

@Injectable({ providedIn: 'root' })
export class SalaryRoutingResolveService implements Resolve<ISalary> {
  constructor(protected service: SalaryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISalary> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((salary: HttpResponse<Salary>) => {
          if (salary.body) {
            return of(salary.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Salary());
  }
}
