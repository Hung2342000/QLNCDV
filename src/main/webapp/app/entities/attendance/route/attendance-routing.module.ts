import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AttendanceComponent } from '../list/attendance.component';
import { AttendanceDetailComponent } from '../detail/attendance-detail.component';
import { AttendanceUpdateComponent } from '../update/attendance-update.component';
import { AttendanceRoutingResolveService } from './attendance-routing-resolve.service';

const attendanceRoute: Routes = [
  {
    path: '',
    component: AttendanceComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AttendanceDetailComponent,
    resolve: {
      attendance: AttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AttendanceUpdateComponent,
    resolve: {
      attendance: AttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AttendanceUpdateComponent,
    resolve: {
      attendance: AttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(attendanceRoute)],
  exports: [RouterModule],
})
export class AttendanceRoutingModule {}
