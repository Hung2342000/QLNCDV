import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBaoCao, BaoCao } from '../baocao.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { BaocaoService } from '../service/baocao.service';
import { FormBuilder, Validators } from '@angular/forms';
import { EmployeeService } from '../../employee/service/employee.service';
import { AttendanceService } from '../../attendance/service/attendance.service';

@Component({
  selector: 'jhi-employee',
  templateUrl: './baocao.component.html',
})
export class BaocaoComponent implements OnInit {
  @ViewChild('addSalary') addSalary: TemplateRef<any> | undefined;

  baocaos?: IBaoCao[] | any;
  dataShow?: IBaoCao[] | any;
  isLoading = false;
  totalItems = 0;
  itemsPerPage = 10;
  page = 1;
  predicate!: string;

  ngbPaginationPage = 1;
  isSaving = false;
  dropdownSettings: any;

  searchNam?: string = '';
  searchNhom?: string = '';

  constructor(
    protected employeeService: EmployeeService,
    protected salaryService: BaocaoService,
    protected activatedRoute: ActivatedRoute,
    protected attendanceService: AttendanceService,
    protected router: Router,
    protected fb: FormBuilder,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;

    this.salaryService
      .query({
        searchNam: this.searchNam,
        searchNhom: this.searchNhom,
      })
      .subscribe({
        next: (res: HttpResponse<IBaoCao[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, 1, !dontNavigate);
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
    this.loadPage();
  }

  pageable(): void {
    this.dataShow = [];
    for (let i = (this.page - 1) * this.itemsPerPage; i < this.baocaos.length; i++) {
      if (i < this.page * this.itemsPerPage) {
        this.dataShow.push(this.baocaos[i]);
      }
    }
    this.totalItems = this.baocaos.length;
  }
  trackId(_index: number, item: IBaoCao): number {
    return item.id!;
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

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected onSuccess(data: IBaoCao[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.baocaos = data ?? [];
    this.pageable();
  }

  protected onError(): void {
    this.ngbPaginationPage = 1;
  }
}
