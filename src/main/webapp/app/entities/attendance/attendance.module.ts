import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AttendanceComponent } from './list/attendance.component';
import { AttendanceDetailComponent } from './detail/attendance-detail.component';
import { AttendanceUpdateComponent } from './update/attendance-update.component';
import { AttendanceDeleteDialogComponent } from './delete/attendance-delete-dialog.component';
import { AttendanceRoutingModule } from './route/attendance-routing.module';
import { DateTimePickerModule } from 'ngx-datetime-picker';

@NgModule({
  imports: [SharedModule, AttendanceRoutingModule, DateTimePickerModule],
  declarations: [AttendanceComponent, AttendanceDetailComponent, AttendanceUpdateComponent, AttendanceDeleteDialogComponent],
  entryComponents: [AttendanceDeleteDialogComponent],
})
export class AttendanceModule {}
