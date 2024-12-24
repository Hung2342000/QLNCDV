import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IEmployee, Employee } from '../employee.model';
import { EmployeeService } from '../service/employee.service';
import { IDepartment } from '../department.model';
import { DATE_FORMAT_CUSTOM } from '../../../config/input.constants';
import { IServiceType } from '../service-type.model';

@Component({
  selector: 'jhi-employee-update',
  templateUrl: './employee-update.component.html',
})
export class EmployeeUpdateComponent implements OnInit {
  isSaving = false;
  departments?: IDepartment[] | any;
  serviceTypes?: IServiceType[] | any;
  serviceTypesCustom?: IServiceType[] | any;

  editForm = this.fb.group({
    id: [null, [Validators.required]],
    codeEmployee: [null, [Validators.required]],
    name: [],
    birthday: [],
    otherId: [],
    address: [],
    mobilePhone: [],
    workPhone: [],
    workEmail: [],
    privateEmail: [],
    department: [],
    startDate: [],
    basicSalary: [],
    serviceType: [],
    region: [],
    isTeller: [false],
    rank: [],
  });

  constructor(protected employeeService: EmployeeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ employee }) => {
      this.updateForm(employee);
    });
    this.employeeService.queryDepartment().subscribe({
      next: (res: HttpResponse<IDepartment[]>) => {
        this.departments = res.body;
      },
    });

    this.employeeService.queryServiceTypeCustom().subscribe({
      next: (res: HttpResponse<IServiceType[]>) => {
        this.serviceTypesCustom = res.body;
      },
    });
  }
  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const employee = this.createFromForm();
    if (employee.id !== undefined) {
      this.subscribeToSaveResponse(this.employeeService.update(employee));
    } else {
      this.subscribeToSaveResponse(this.employeeService.create(employee));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmployee>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(employee: IEmployee): void {
    this.editForm.patchValue({
      id: employee.id,
      codeEmployee: employee.codeEmployee,
      name: employee.name,
      birthday: employee.birthday?.format(DATE_FORMAT_CUSTOM),
      otherId: employee.otherId,
      address: employee.address,
      mobilePhone: employee.mobilePhone,
      workPhone: employee.workPhone,
      workEmail: employee.workEmail,
      privateEmail: employee.privateEmail,
      department: employee.department,
      startDate: employee.startDate?.format(DATE_FORMAT_CUSTOM),
      serviceType: employee.serviceType,
      region: employee.region,
      isTeller: employee.isTeller,
      rank: employee.rank,
    });
  }

  protected createFromForm(): IEmployee {
    return {
      ...new Employee(),
      id: this.editForm.get(['id'])!.value,
      codeEmployee: this.editForm.get(['codeEmployee'])!.value,
      name: this.editForm.get(['name'])!.value,
      birthday: this.editForm.get(['birthday'])!.value,
      otherId: this.editForm.get(['otherId'])!.value,
      address: this.editForm.get(['address'])!.value,
      mobilePhone: this.editForm.get(['mobilePhone'])!.value,
      workPhone: this.editForm.get(['workPhone'])!.value,
      workEmail: this.editForm.get(['workEmail'])!.value,
      privateEmail: this.editForm.get(['privateEmail'])!.value,
      department: this.editForm.get(['department'])!.value,
      startDate: this.editForm.get(['startDate'])!.value,
      serviceType: this.editForm.get(['serviceType'])!.value,
      region: this.editForm.get(['region'])!.value,
      isTeller: this.editForm.get(['isTeller'])!.value,
      rank: this.editForm.get(['rank'])!.value,
    };
  }
}
