import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILuong } from '../luong.model';
import { LuongService } from '../service/luong.service';

@Component({
  templateUrl: './luong-delete-dialog.component.html',
})
export class LuongDeleteDialogComponent {
  luong?: ILuong;

  constructor(protected employeeService: LuongService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.employeeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
