import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FlatpickrModule } from 'angularx-flatpickr';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { Luongdot2DeleteDialogComponent } from './delete/luongdot2-delete-dialog.component';
import { Luongdot2UpdateComponent } from './update/luongdot2-update.component';
import { Luongdot2RoutingModule } from './route/luongdot2-routing.module';
import { Luongdot2Component } from './list/luongdot2.component';

@NgModule({
  imports: [SharedModule, Luongdot2RoutingModule, FlatpickrModule, NgMultiSelectDropDownModule],
  declarations: [Luongdot2Component, Luongdot2DeleteDialogComponent, Luongdot2UpdateComponent],
  entryComponents: [Luongdot2DeleteDialogComponent],
})
export class Luongdot2Module {}
