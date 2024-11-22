import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAttendance } from '../attendance.model';
import { AttendanceService } from '../service/attendance.service';

@Component({
  templateUrl: './attendance-delete-dialog.component.html',
})
export class AttendanceDeleteDialogComponent {
  attendance?: IAttendance;

  constructor(protected attendanceService: AttendanceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.attendanceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
