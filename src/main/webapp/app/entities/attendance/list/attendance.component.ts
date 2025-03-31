import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Attendance, IAttendance } from '../attendance.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { AttendanceService } from '../service/attendance.service';
import { AttendanceDeleteDialogComponent } from '../delete/attendance-delete-dialog.component';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { FormBuilder, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { IAttendanceDetail } from '../attendanceDetail.model';
import { ToastComponent } from '../../../layouts/toast/toast.component';
import { IDepartment } from '../../employee/department.model';

@Component({
  selector: 'jhi-attendance',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.scss'],
})
export class AttendanceComponent implements OnInit {
  @ViewChild('add') add: TemplateRef<any> | undefined;
  @ViewChild('ngayNghiLeList') ngayNghiLeList: TemplateRef<any> | undefined;
  @ViewChild('toast') toast!: ToastComponent;

  attendances?: IAttendance[] | any;
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  employeeList?: IEmployee[] | any;
  isSaving = false;
  employeesList: any;
  dropdownSettings: any;
  departments?: IDepartment[] | any;
  editForm = this.fb.group({
    id: [null, [Validators.required]],
    createDate: [],
    name: [],
    employees: [],
    numberWork: [],
    month: [],
    year: [],
    note: [],
    ngayNghi: [],
  });

  constructor(
    protected fb: FormBuilder,
    protected employeeService: EmployeeService,
    protected attendanceService: AttendanceService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.attendanceService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IAttendance[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });

    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeeList = res.body;
      },
    });
  }
  receiveMessage($event: any): void {
    this.employeesList = $event;
  }
  addAtt(attendance: IAttendance): void {
    this.updateForm(attendance);
    this.modalService.open(this.add, { size: 'lg', backdrop: 'static' });
  }

  success(): void {
    this.isSaving = false;
    this.closeModal();
    this.editForm.reset();
    this.toast.showToast('Thành công');
  }
  closeModal(): void {
    this.loadPage();
    this.modalService.dismissAll();
  }
  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }

  save(): void {
    this.isSaving = true;
    const attendance = this.createFrom();
    attendance.searchName = this.employeesList.searchName;
    attendance.searchNhom = this.employeesList.searchNhom;
    attendance.searchDepartment = this.employeesList.searchDepartment;
    if (attendance.id && typeof attendance.id === 'number') {
      this.subscribeToSaveResponse(this.attendanceService.update(attendance));
    } else {
      this.subscribeToSaveResponse(this.attendanceService.create(attendance));
    }
  }

  ngOnInit(): void {
    this.dropdownSettings = {
      singleSelection: false,
      idField: 'id',
      textField: 'name',
      itemsShowLimit: 5,
      allowSearchFilter: true,
    };
    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeesList = res.body;
      },
    });
    this.employeeService.queryDepartment().subscribe({
      next: (res: HttpResponse<IDepartment[]>) => {
        this.departments = res.body;
      },
    });
    this.handleNavigation();
  }

  create(): void {
    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeesList = res.body;
      },
    });
    const modalRef = this.modalService.open(this.add, { size: 'lg', backdrop: 'static' });
  }

  ngayNghiLeListShow(): void {
    const modalRef = this.modalService.open(this.ngayNghiLeList, { size: 'lg', backdrop: 'static' });
  }
  trackId(_index: number, item: IAttendance): number {
    return item.id!;
  }

  delete(attendance: IAttendance): void {
    const modalRef = this.modalService.open(AttendanceDeleteDialogComponent, { size: 'sm', backdrop: 'static' });
    modalRef.componentInstance.attendance = attendance;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
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

  formatMonthYear(attendance: IAttendance): string {
    let text = '';
    if (attendance.month === null && attendance.year !== null && attendance.year) {
      text = attendance.year.toString();
    } else if (attendance.year === null && attendance.month !== null && attendance.month) {
      text = attendance.month.toString();
    } else if (typeof attendance.month === 'number' && typeof attendance.year === 'number') {
      text = 'tháng ' + String(attendance.month) + ' năm ' + String(attendance.year);
    }
    return text;
  }

  previousState(): void {
    window.history.back();
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

  protected updateForm(attendance: IAttendance): void {
    this.editForm.patchValue({
      id: attendance.id,
      createDate: attendance.createDate,
      name: attendance.name,
      employees: attendance.employees,
      numberWork: attendance.numberWork,
      month: attendance.month,
      year: attendance.year,
      note: attendance.note,
      ngayNghi: attendance.note,
    });
  }

  protected onSuccess(data: IAttendance[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/attendance'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.attendances = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAttendance>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.success(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
  protected onSaveError(): void {
    this.isSaving = false;
    // Api for inheritance.
  }
  protected createFrom(): IAttendance {
    return {
      ...new Attendance(),
      id: this.editForm.get(['id'])!.value,
      createDate: this.editForm.get(['createDate'])!.value,
      name: this.editForm.get(['name'])!.value,
      employees: this.editForm.get(['employees'])!.value,
      numberWork: this.editForm.get(['numberWork'])!.value,
      month: this.editForm.get(['month'])!.value,
      year: this.editForm.get(['year'])!.value,
      note: this.editForm.get(['note'])!.value,
      ngayNghi: this.editForm.get(['ngayNghi'])!.value,
    };
  }
}
