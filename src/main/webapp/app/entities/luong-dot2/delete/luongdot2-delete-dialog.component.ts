import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ILuong } from '../../luong/luong.model';
import { LuongService } from '../../luong/service/luong.service';

@Component({
  templateUrl: './luongdot2-delete-dialog.component.html',
})
export class Luongdot2DeleteDialogComponent {
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
