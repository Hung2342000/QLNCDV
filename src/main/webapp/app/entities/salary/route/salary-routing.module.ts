import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SalaryComponent } from '../list/salary.component';
import { SalaryRoutingResolveService } from './salary-routing-resolve.service';
import { SalaryUpdateComponent } from '../update/salary-update.component';

const salaryRoute: Routes = [
  {
    path: '',
    component: SalaryComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },

  {
    path: ':id/edit',
    component: SalaryUpdateComponent,
    resolve: {
      salary: SalaryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(salaryRoute)],
  exports: [RouterModule],
})
export class SalaryRoutingModule {}
