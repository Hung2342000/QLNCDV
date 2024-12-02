import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAttendance, Attendance } from '../attendance.model';
import { AttendanceService } from '../service/attendance.service';

@Injectable({ providedIn: 'root' })
export class AttendanceRoutingResolveService implements Resolve<IAttendance> {
  constructor(protected service: AttendanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAttendance> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((attendance: HttpResponse<Attendance>) => {
          if (attendance.body) {
            return of(attendance.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Attendance());
  }
}
