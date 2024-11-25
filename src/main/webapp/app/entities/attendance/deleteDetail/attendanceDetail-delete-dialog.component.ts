import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAttendance } from '../attendance.model';
import { AttendanceService } from '../service/attendance.service';
import { IAttendanceDetail } from '../attendanceDetail.model';

@Component({
  templateUrl: './attendanceDetail-delete-dialog.component.html',
})
export class AttendanceDeleteDetailDialogComponent {
  attendanceDetail?: IAttendanceDetail;

  constructor(protected attendanceService: AttendanceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.attendanceService.deleteDetail(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
