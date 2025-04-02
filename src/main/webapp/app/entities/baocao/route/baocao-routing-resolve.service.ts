import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { BaocaoService } from '../service/baocao.service';
import { BaoCao, IBaoCao } from '../baocao.model';

@Injectable({ providedIn: 'root' })
export class BaocaoRoutingResolveService implements Resolve<IBaoCao> {
  constructor(protected service: BaocaoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBaoCao> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((salary: HttpResponse<BaoCao>) => {
          if (salary.body) {
            return of(salary.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BaoCao());
  }
}
