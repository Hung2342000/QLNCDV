import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
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
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastComponent } from '../../../layouts/toast/toast.component';
import { ITransfer, Transfer } from '../transfer.model';

@Component({
  selector: 'jhi-employee-update',
  templateUrl: './employee-update.component.html',
  styleUrls: ['./employee-update.component.scss'],
})
export class EmployeeUpdateComponent implements OnInit {
  @ViewChild('dieuchuyen') dieuchuyen: TemplateRef<any> | undefined;
  @ViewChild('toast') toast!: ToastComponent;
  isSaving = false;
  departments?: IDepartment[] | any;
  transfer?: ITransfer;
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
    closeDate: [],
    basicSalary: [],
    serviceType: [],
    region: [],
    rank: [],
    status: [],
    diaBan: [],
    ngayNghiSinh: [],
    ngayDieuChuyen: [],
  });

  // editFormTransfer = this.fb.group({
  //   id: [null, [Validators.required]],
  //   employeeTd: [null, [Validators.required]],
  //   startDate: [],
  //   serviceType: [],
  //   department: [],
  //   status: [],
  //
  // });
  constructor(
    protected employeeService: EmployeeService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ employee }) => {
      this.updateForm(employee);
    });
    this.employeeService.queryDepartment().subscribe({
      next: (res: HttpResponse<IDepartment[]>) => {
        this.departments = res.body;
      },
    });

    this.employeeService.queryServiceType().subscribe({
      next: (res: HttpResponse<IServiceType[]>) => {
        this.serviceTypesCustom = res.body;
      },
    });
  }
  previousState(): void {
    window.history.back();
  }
  closeModal(): void {
    this.modalService.dismissAll();
  }
  dieuChuyenSave(): void {
    const modalRef = this.modalService.open(this.dieuchuyen, { size: 'lg', backdrop: 'static' });
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

  saveDieuChuyen(): void {
    const transfer = this.createFromTransfer();
    const employee = this.createFromForm();
    transfer.employeeId = employee.id;
    this.subscribeToSaveResponseDieuChuyen(this.employeeService.createTransfer(transfer));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmployee>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected subscribeToSaveResponseDieuChuyen(result: Observable<HttpResponse<ITransfer>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => {
        this.toast.showToast('Lưu thành công');
        this.closeModal();
      },
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.toast.showToast('Lưu thành công');
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
      closeDate: employee.closeDate?.format(DATE_FORMAT_CUSTOM),
      serviceType: employee.serviceType,
      region: employee.region,
      rank: employee.rank,
      status: employee.status,
      diaBan: employee.diaBan,
      ngayNghiSinh: employee.ngayNghiSinh?.format(DATE_FORMAT_CUSTOM),
      ngayDieuChuyen: employee.ngayDieuChuyen?.format(DATE_FORMAT_CUSTOM),
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
      closeDate: this.editForm.get(['closeDate'])!.value,
      serviceType: this.editForm.get(['serviceType'])!.value,
      region: this.editForm.get(['region'])!.value,
      rank: this.editForm.get(['rank'])!.value,
      status: this.editForm.get(['status'])!.value,
      diaBan: this.editForm.get(['diaBan'])!.value,
      ngayNghiSinh: this.editForm.get(['ngayNghiSinh'])!.value,
      ngayDieuChuyen: this.editForm.get(['ngayDieuChuyen'])!.value,
    };
  }
  protected createFromTransfer(): ITransfer {
    return {
      ...new Transfer(),
      startDate: this.editForm.get(['ngayDieuChuyen'])!.value,
      serviceType: this.editForm.get(['serviceType'])!.value,
      status: this.editForm.get(['status'])!.value,
      department: this.editForm.get(['department'])!.value,
    };
  }
}
