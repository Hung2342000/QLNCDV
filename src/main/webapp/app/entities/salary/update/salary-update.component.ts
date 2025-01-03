import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ISalary, Salary } from '../salary.model';
import { SalaryService } from '../service/salary.service';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from '../../../config/pagination.constants';
import { ISalaryDetail, SalaryDetail } from '../salaryDetail.model';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastComponent } from '../../../layouts/toast/toast.component';

@Component({
  selector: 'jhi-employee-update',
  templateUrl: './salary-update.component.html',
})
export class SalaryUpdateComponent implements OnInit {
  @ViewChild('addDetailSalary') addDetailSalary: TemplateRef<any> | undefined;
  @ViewChild('toastSalary') toastSalary!: ToastComponent;
  salary?: ISalary | any;
  salaryDetails?: ISalaryDetail[] | any;
  salaryDetailsAm?: ISalaryDetail[] | any;
  salaryDetailsHTVP?: ISalaryDetail[] | any;
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  isSaving = false;
  employeeList?: IEmployee[] | any;
  isEdit = true;
  isEditAm = true;
  content?: string = '';

  editForm = this.fb.group({
    id: [null, [Validators.required]],
    nameSalary: [],
    month: [],
    year: [],
    numberWork: [],
  });

  form = new FormGroup({
    details: new FormArray([new FormControl()]),
  });

  formAm = new FormGroup({
    detailsAm: new FormArray([new FormControl()]),
  });

  private updatedData: any;
  private updatedDataAm: any;
  private mergedData: any;

  constructor(
    protected modalService: NgbModal,
    protected employeeService: EmployeeService,
    protected router: Router,
    protected salaryService: SalaryService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}
  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.salaryService
      .queryDetail({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: ['id,asc'],
        idSalary: this.salary.id,
      })
      .subscribe({
        next: (res: HttpResponse<ISalaryDetail[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  createRowForm(item: any): FormGroup {
    return this.fb.group({
      id: [item.id],
      salaryId: [item.salaryId],
      employeeId: [item.employeeId],
      diemCungCapDV: [item.diemCungCapDV],
      chucDanh: [item.chucDanh],
      vung: [item.vung],
      donGiaDichVu: [item.donGiaDichVu],
      numberWorkInMonth: [item.numberWorkInMonth],
      numberWorking: [item.numberWorking],
      donGiaDichVuThucNhan: [item.donGiaDichVuThucNhan],
      mucChiToiThieu: [item.mucChiToiThieu],
      xepLoai: [item.xepLoai],
      htc: [item.htc],
      chiPhiGiamTru: [item.chiPhiGiamTru],
      chiPhiThueDichVu: [item.chiPhiThueDichVu],
      nhom: [item.nhom],
      note: [item.note],
    });
  }
  createRowFormAm(itemAm: any): FormGroup {
    return this.fb.group({
      id: [itemAm.id],
      salaryId: [itemAm.salaryId],
      employeeId: [itemAm.employeeId],
      diaBan: [itemAm.diaBan],
      vung: [itemAm.vung],
      mucChiToiThieu: [itemAm.mucChiToiThieu],
      kpis: [itemAm.kpis],
      luongCoDinhThucTe: [itemAm.luongCoDinhThucTe],
      donGiaDichVu: [itemAm.donGiaDichVu],
      numberWorking: [itemAm.numberWorking],
      numberWorkInMonth: [itemAm.numberWorkInMonth],
      chiPhiGiamTru: [itemAm.chiPhiGiamTru],
      phiCoDinhDaThucHien: [itemAm.phiCoDinhDaThucHien],
      phiCoDinhThanhToanThucTe: [itemAm.phiCoDinhThanhToanThucTe],
      chiPhiDichVuKhoanVaKK: [itemAm.chiPhiDichVuKhoanVaKK],
      chiPhiKKKhac: [itemAm.chiPhiKKKhac],
      tongChiPhiKVKK: [itemAm.tongChiPhiKVKK],
      chiPhiThueDichVu: [itemAm.chiPhiThueDichVu],
      nhom: [itemAm.nhom],
      note: [itemAm.note],
    });
  }
  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ salary }) => {
      this.updateForm(salary);
      this.salary = salary;
    });
    this.salaryService.queryAll(this.salary.id).subscribe({
      next: (res: HttpResponse<ISalaryDetail[]>) => {
        this.salaryDetails = res.body;
        this.salaryDetailsHTVP = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'HTVP');
        this.salaryDetailsAm = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'AM');
        this.form = this.fb.group({
          details: this.fb.array(this.salaryDetailsHTVP.map((item: ISalaryDetail) => this.createRowForm(item))),
        });
        this.formAm = this.fb.group({
          detailsAm: this.fb.array(this.salaryDetailsAm.map((item: ISalaryDetail) => this.createRowFormAm(item))),
        });
      },
    });
    // this.loadPage();
    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeeList = res.body;
      },
    });
  }

  onSubmit(): void {
    this.updatedData = this.form.value.details;
    this.updatedDataAm = this.formAm.value.detailsAm;
    this.mergedData = this.updatedData.concat(this.updatedDataAm);
    if (this.updatedData.length > 0) {
      this.salaryService.createAllDetail(this.mergedData).subscribe(
        data => {
          this.content = 'Lưu thành công';
          this.toastSalary.showToast(this.content);
          this.reload();
          setTimeout(() => {
            this.isEdit = false;
          }, 500);
        },
        error => {
          this.content = 'Có lỗi xảy ra';
          this.toastSalary.showToast(this.content);
        }
      );
    } else {
      this.content = 'Lỗi dữ liệu';
      this.toastSalary.showToast(this.content);
    }
  }
  previousState(): void {
    window.history.back();
  }

  edit(): void {
    this.isEdit = true;
  }

  reload(): void {
    this.salaryService.queryAll(this.salary.id).subscribe({
      next: (res: HttpResponse<ISalaryDetail[]>) => {
        this.salaryDetails = res.body;
        this.salaryDetailsHTVP = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'HTVP');
        this.salaryDetailsAm = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'AM');
        this.form = this.fb.group({
          details: this.fb.array(this.salaryDetailsHTVP.map((item: ISalaryDetail) => this.createRowForm(item))),
        });
        this.formAm = this.fb.group({
          detailsAm: this.fb.array(this.salaryDetailsAm.map((item: ISalaryDetail) => this.createRowFormAm(item))),
        });
      },
    });
  }

  exportToExcel(): void {
    this.salaryService
      .exportToExcel({
        salaryId: this.salary.id,
      })
      .subscribe(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'bangluong.xlsx';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      });
  }
  employeeName(idEmployee: number | null | undefined): any {
    let name = '';
    for (let i = 0; i < this.employeeList.length; i++) {
      if (idEmployee === this.employeeList[i].id) {
        name = this.employeeList[i].name;
      }
    }
    return name;
  }
  trackId(_index: number, item: ISalaryDetail): number {
    return item.id!;
  }

  trackIdAm(_index: number, item: ISalaryDetail): number {
    return item.id!;
  }
  save(): void {
    this.isSaving = true;
    const salary = this.createFrom();
    if (salary.id !== undefined) {
      this.subscribeToSaveResponse(this.salaryService.update(salary));
    } else {
      this.subscribeToSaveResponse(this.salaryService.create(salary));
    }
  }
  close(): void {
    this.router.navigate([`/salary`]);
  }

  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }

  closeModalDetail(): void {
    this.loadPage();
    this.modalService.dismissAll();
  }
  closeModal(): void {
    this.loadPage();
    this.modalService.dismissAll();
  }

  get details(): FormArray {
    return this.form.get('details') as FormArray;
  }
  get detailsAm(): FormArray {
    return this.formAm.get('detailsAm') as FormArray;
  }
  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISalary>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected subscribeToSaveDetailResponse(result: Observable<HttpResponse<ISalary>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.closeModalDetail(),
      error: () => this.onSaveError(),
    });
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
  protected onSaveSuccess(): void {
    this.previousState();
  }
  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected onSuccess(data: ISalary[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.salaryDetails = data ?? [];
    this.ngbPaginationPage = this.page;
  }
  protected createFrom(): ISalary {
    return {
      ...new Salary(),
      id: this.editForm.get(['id'])!.value,
      nameSalary: this.editForm.get(['nameSalary'])!.value,
      month: this.editForm.get(['month'])!.value,
      year: this.editForm.get(['year'])!.value,
      numberWork: this.editForm.get(['numberWork'])!.value,
    };
  }

  protected updateForm(salary: ISalary): void {
    this.editForm.patchValue({
      id: salary.id,
      nameSalary: salary.nameSalary,
      month: salary.month,
      year: salary.year,
      numberWork: salary.numberWork,
    });
  }
}
