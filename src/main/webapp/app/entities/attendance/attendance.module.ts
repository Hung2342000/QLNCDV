import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AttendanceComponent } from './list/attendance.component';
import { AttendanceDetailComponent } from './detail/attendance-detail.component';
import { AttendanceUpdateComponent } from './update/attendance-update.component';
import { AttendanceDeleteDialogComponent } from './delete/attendance-delete-dialog.component';
import { AttendanceRoutingModule } from './route/attendance-routing.module';
import { DateTimePickerModule } from 'ngx-datetime-picker';
import { FlatpickrModule } from 'angularx-flatpickr';
import { AttendanceDeleteDetailDialogComponent } from './deleteDetail/attendanceDetail-delete-dialog.component';

@NgModule({
  imports: [SharedModule, AttendanceRoutingModule, DateTimePickerModule, FlatpickrModule],
  declarations: [
    AttendanceComponent,
    AttendanceDetailComponent,
    AttendanceUpdateComponent,
    AttendanceDeleteDialogComponent,
    AttendanceDeleteDetailDialogComponent,
  ],
  entryComponents: [AttendanceDeleteDialogComponent],
})
export class AttendanceModule {}
