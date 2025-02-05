import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISalary, Salary } from '../salary.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { SalaryService } from '../service/salary.service';
import { SalaryDeleteDialogComponent } from '../delete/salary-delete-dialog.component';
import { IAttendanceDetail } from '../../attendance/attendanceDetail.model';
import { finalize } from 'rxjs/operators';
import { FormBuilder, Validators } from '@angular/forms';
import { Attendance, IAttendance } from '../../attendance/attendance.model';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { ISalaryDetail } from '../salaryDetail.model';
import { AttendanceService } from '../../attendance/service/attendance.service';

@Component({
  selector: 'jhi-employee',
  templateUrl: './salary.component.html',
})
export class SalaryComponent implements OnInit {
  @ViewChild('addSalary') addSalary: TemplateRef<any> | undefined;

  salarys?: ISalary[] | any;
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  isSaving = false;
  dropdownSettings: any;
  employeeCheck: any;
  employeesList: IEmployee[] | any;
  attendanceList: IAttendance[] | any;

  editForm = this.fb.group({
    id: [null, [Validators.required]],
    createDate: [],
    nameSalary: [],
    month: [],
    year: [],
    numberWork: [],
    attendanceId: [],
    employees: [],
    isAttendance: [],
  });
  constructor(
    protected employeeService: EmployeeService,
    protected salaryService: SalaryService,
    protected activatedRoute: ActivatedRoute,
    protected attendanceService: AttendanceService,
    protected router: Router,
    protected fb: FormBuilder,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.salaryService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ISalary[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
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

    this.attendanceService.queryAttendanceAll().subscribe({
      next: (res: HttpResponse<IAttendance[]>) => {
        this.attendanceList = res.body;
      },
    });
    this.handleNavigation();
  }
  receiveMessage($event: any): void {
    this.employeeCheck = $event;
  }
  loadPageSearch(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;

    this.salaryService
      .query({
        page: 0,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ISalary[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, 1, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }
  trackId(_index: number, item: ISalary): number {
    return item.id!;
  }

  delete(salary: ISalary): void {
    const modalRef = this.modalService.open(SalaryDeleteDialogComponent, { size: 'sm', backdrop: 'static' });
    modalRef.componentInstance.salary = salary;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  create(): void {
    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeesList = res.body;
      },
    });
    const modalRef = this.modalService.open(this.addSalary, { size: 'lg', backdrop: 'static' });
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

  saveSalary(): void {
    this.isSaving = true;
    const salary = this.createFrom();
    salary.searchName = this.employeeCheck.searchName;
    salary.searchNhom = this.employeeCheck.searchNhom;
    salary.searchDepartment = this.employeeCheck.searchDepartment;
    if (salary.id && typeof salary.id === 'number') {
      this.subscribeToSaveResponse(this.salaryService.update(salary));
    } else {
      this.subscribeToSaveResponse(this.salaryService.create(salary));
    }
  }
  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISalaryDetail>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.closeModal(),
      error: () => this.onSaveError(),
    });
  }
  protected onSaveError(): void {
    // Api for inheritance.
  }

  // view(employee: ISalary): void {
  //   const modalRef = this.modalService.open(EmployeeDetailComponent, { size: 'lg', backdrop: 'static' });
  //   modalRef.componentInstance.employee = employee;
  // }
  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }
  protected onSaveFinalize(): void {
    this.isSaving = false;
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

  protected onSuccess(data: ISalary[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/salary'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.salarys = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  protected createFrom(): ISalary {
    return {
      ...new Salary(),
      id: this.editForm.get(['id'])!.value,
      createDate: this.editForm.get(['createDate'])!.value,
      nameSalary: this.editForm.get(['nameSalary'])!.value,
      month: this.editForm.get(['month'])!.value,
      year: this.editForm.get(['year'])!.value,
      numberWork: this.editForm.get(['numberWork'])!.value,
      attendanceId: this.editForm.get(['attendanceId'])!.value,
      employees: this.editForm.get(['employees'])!.value,
      isAttendance: this.editForm.get(['isAttendance'])!.value,
    };
  }
}
