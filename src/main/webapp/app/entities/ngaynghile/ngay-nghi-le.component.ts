import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';

import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { combineLatest, Observable } from 'rxjs';
import { INgayNghiLe, NgayNghiLe } from '../attendance/ngay-nghi-le.model';
import { AttendanceService } from '../attendance/service/attendance.service';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from '../../config/pagination.constants';
import { Attendance, IAttendance } from '../attendance/attendance.model';
import { finalize } from 'rxjs/operators';
import { ToastComponent } from '../../layouts/toast/toast.component';
import { IEmployee } from '../employee/employee.model';
import { DATE_FORMAT_CUSTOM } from '../../config/input.constants';

@Component({
  selector: 'jhi-ngay-nghi-le-box',
  templateUrl: './ngay-nghi-le.component.html',
  styleUrls: ['./ngay-nghi-le.component.scss'],
})
export class NgayNghiLeComponent implements OnInit {
  @ViewChild('toast') toast!: ToastComponent;
  @ViewChild('add') add: TemplateRef<any> | undefined;
  @Output() employeesListOutput = new EventEmitter<{ searchName: string; searchDepartment: string; searchNhom: string }>();
  isLoading = false;
  isSaving = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  ngayNghiLes?: INgayNghiLe[] | any;
  editForm = this.fb.group({
    id: [null, [Validators.required]],
    holidayDate: [],
    description: [],
  });
  private modalRefAdd!: NgbModalRef;
  constructor(
    protected fb: FormBuilder,
    protected activatedRoute: ActivatedRoute,
    protected attendanceService: AttendanceService,
    protected router: Router,
    protected modalService: NgbModal
  ) {}
  ngOnInit(): void {
    this.handleNavigation();
  }

  save(): void {
    const ngayNghiLe = this.createFrom();
    if (ngayNghiLe.id && typeof ngayNghiLe.id === 'number') {
      this.subscribeToSaveResponse(this.attendanceService.updateNgayNghiLe(ngayNghiLe));
    } else {
      this.subscribeToSaveResponse(this.attendanceService.createNgayNghiLe(ngayNghiLe));
    }
  }
  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.attendanceService
      .queryNgayNghiLe({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<INgayNghiLe[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }

  successAdd(): void {
    this.closeModalRefAdd();
    this.loadPage();
    this.editForm.reset();
    this.toast.showToast('Thành công');
  }
  closeModal(): void {
    this.modalService.dismissAll();
  }
  closeModalRefAdd(): void {
    this.modalRefAdd.close();
  }

  addNgayNghiLe(): void {
    this.modalRefAdd = this.modalService.open(this.add, { size: 'lg', backdrop: 'static' });
  }
  deleteNgayNghiLe(id: number): void {
    this.attendanceService.deleteNgayNghiLe(id).subscribe(() => {
      this.toast.showToast('Xóa thành công');
      this.loadPage();
    });
  }
  trackId(_index: number, item: INgayNghiLe): number {
    return item.id!;
  }
  protected onSuccess(data: INgayNghiLe[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.ngayNghiLes = data ?? [];
    // this.employeesListOutput.emit({
    //   searchName: this.searchName ? this.searchName : '',
    //   searchDepartment: this.searchDepartment ? this.searchDepartment : '',
    //   searchNhom: this.searchNhom ? this.searchNhom : '',
    // });
    this.ngbPaginationPage = this.page;
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAttendance>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.successAdd(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }
  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  protected createFrom(): INgayNghiLe {
    return {
      ...new NgayNghiLe(),
      id: this.editForm.get(['id'])!.value,
      holidayDate: this.editForm.get(['holidayDate'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
  protected updateForm(ngayNghiLe: INgayNghiLe): void {
    this.editForm.patchValue({
      id: ngayNghiLe.id,
      holidayDate: ngayNghiLe.holidayDate?.format(DATE_FORMAT_CUSTOM),
      description: ngayNghiLe.description,
    });
  }
}
