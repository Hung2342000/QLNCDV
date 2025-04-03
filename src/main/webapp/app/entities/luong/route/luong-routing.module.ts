import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LuongComponent } from '../list/luong.component';
import { LuongRoutingResolveService } from './luong-routing-resolve.service';
import { LuongUpdateComponent } from '../update/luong-update.component';

const luongRoute: Routes = [
  {
    path: '',
    component: LuongComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },

  {
    path: ':id/edit',
    component: LuongUpdateComponent,
    resolve: {
      luong: LuongRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(luongRoute)],
  exports: [RouterModule],
})
export class LuongRoutingModule {}
