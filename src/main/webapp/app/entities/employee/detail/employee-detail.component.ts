import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { IEmployee } from '../employee.model';
import { IDepartment } from '../department.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { IServiceType } from '../service-type.model';

@Component({
  selector: 'jhi-employee-detail',
  templateUrl: './employee-detail.component.html',
})
export class EmployeeDetailComponent {
  employee: IEmployee | null = null;
  departments?: IDepartment[] | any;
  serviceTypesCustom?: IServiceType[] | any;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected activeModal: NgbActiveModal) {}

  previousState(): void {
    this.activeModal.dismiss();
  }
  departmentName(code: string | null | undefined): any {
    let name = code;
    for (let i = 0; i < this.departments.length; i++) {
      if (code?.includes(this.departments[i].code)) {
        name = this.departments[i].name;
      }
    }
    return name;
  }
  serviceTypeName(id: number | null | undefined): any {
    let name = id;
    for (let i = 0; i < this.serviceTypesCustom.length; i++) {
      if (id === this.serviceTypesCustom[i].id) {
        name = this.serviceTypesCustom[i].serviceName;
      }
    }
    return name;
  }
  edit(employee: IEmployee): void {
    this.router.navigate(['/employee', employee.id, 'edit']);
    this.activeModal.dismiss();
  }
}
