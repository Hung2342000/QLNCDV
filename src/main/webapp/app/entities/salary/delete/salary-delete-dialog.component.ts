import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISalary } from '../salary.model';
import { SalaryService } from '../service/salary.service';

@Component({
  templateUrl: './salary-delete-dialog.component.html',
})
export class SalaryDeleteDialogComponent {
  salary?: ISalary;

  constructor(protected employeeService: SalaryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.employeeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
