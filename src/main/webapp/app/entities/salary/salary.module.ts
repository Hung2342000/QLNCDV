import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SalaryComponent } from './list/salary.component';
import { SalaryDeleteDialogComponent } from './delete/salary-delete-dialog.component';
import { SalaryRoutingModule } from './route/salary-routing.module';
import { FlatpickrModule } from 'angularx-flatpickr';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { SalaryUpdateComponent } from './update/salary-update.component';

@NgModule({
  imports: [SharedModule, SalaryRoutingModule, FlatpickrModule, NgMultiSelectDropDownModule],
  declarations: [SalaryComponent, SalaryDeleteDialogComponent, SalaryUpdateComponent],
  entryComponents: [SalaryDeleteDialogComponent],
})
export class SalaryModule {}
