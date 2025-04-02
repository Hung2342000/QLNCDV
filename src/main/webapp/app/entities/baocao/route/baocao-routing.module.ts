import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BaocaoComponent } from '../list/baocao.component';

const salaryRoute: Routes = [
  {
    path: '',
    component: BaocaoComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(salaryRoute)],
  exports: [RouterModule],
})
export class BaocaoRoutingModule {}
