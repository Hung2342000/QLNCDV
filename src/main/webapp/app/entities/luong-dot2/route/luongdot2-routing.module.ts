import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Luongdot2Component } from '../list/luongdot2.component';
import { Luongdot2RoutingResolveService } from './luongdot2-routing-resolve.service';
import { Luongdot2UpdateComponent } from '../update/luongdot2-update.component';

const luongdot2Route: Routes = [
  {
    path: '',
    component: Luongdot2Component,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },

  {
    path: ':id/edit',
    component: Luongdot2UpdateComponent,
    resolve: {
      luongdot2: Luongdot2RoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(luongdot2Route)],
  exports: [RouterModule],
})
export class Luongdot2RoutingModule {}
