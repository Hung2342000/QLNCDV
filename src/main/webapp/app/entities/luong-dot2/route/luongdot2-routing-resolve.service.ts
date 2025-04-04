import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { LuongService } from '../../luong/service/luong.service';
import { ILuong, Luong } from '../../luong/luong.model';

@Injectable({ providedIn: 'root' })
export class Luongdot2RoutingResolveService implements Resolve<ILuong> {
  constructor(protected service: LuongService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILuong> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((luong: HttpResponse<Luong>) => {
          if (luong.body) {
            return of(luong.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Luong());
  }
}
