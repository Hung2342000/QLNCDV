import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IEmployee } from '../../employee/employee.model';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { EmployeeService } from '../../employee/service/employee.service';
import { AttendanceService } from '../../attendance/service/attendance.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from '../../../config/pagination.constants';
import { IDepartment } from '../../employee/department.model';
import { IServiceType } from '../../employee/service-type.model';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'jhi-employee-box',
  templateUrl: './employee-box.component.html',
  styleUrls: ['./employee-box.component.scss'],
})
export class EmployeeBoxComponent implements OnInit {
  employees?: IEmployee[] | any;
  @Input() departments?: IDepartment[] | any;
  @Output() employeesListOutput = new EventEmitter<{ searchName: string; searchDepartment: string; searchNhom: string }>();
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  searchName?: string = '';
  searchDepartment?: string = '';
  searchNhom?: string = '';
  serviceTypesCustom?: IServiceType[] | any;
  constructor(
    protected fb: FormBuilder,
    protected employeeService: EmployeeService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}
  isAllSelected(): boolean {
    return this.employees.every((emp: IEmployee) => emp.selected) ? true : false; // Kiểm tra tất cả checkbox đã chọn chưa
  }
  ngOnInit(): void {
    this.employeeService.queryDepartment().subscribe({
      next: (res: HttpResponse<IDepartment[]>) => {
        this.departments = res.body;
      },
    });
    this.handleNavigation();
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.employeeService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
        searchName: this.searchName,
        searchCode: '',
        searchDepartment: this.searchDepartment,
        searchNhom: this.searchNhom,
      })
      .subscribe({
        next: (res: HttpResponse<IEmployee[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  loadPageSearch(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;

    this.employeeService
      .query({
        page: 0,
        size: this.itemsPerPage,
        searchName: this.searchName,
        searchCode: '',
        searchDepartment: this.searchDepartment,
        searchNhom: this.searchNhom,
      })
      .subscribe({
        next: (res: HttpResponse<IEmployee[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, 1, !dontNavigate);
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

  departmentName(code: string | null | undefined): any {
    let name = code;
    for (let i = 0; i < this.departments.length; i++) {
      if (code?.includes(this.departments[i].code)) {
        name = this.departments[i].name;
      }
    }
    return name;
  }
  toggleSelectAll(): void {
    const allSelected = this.isAllSelected(); // Kiểm tra xem có tất cả checkbox đã được chọn không
    this.employees.forEach((emp: IEmployee) => (emp.selected = !allSelected)); // Nếu đã chọn hết thì bỏ chọn, ngược lại thì chọn tất cả
  }
  trackId(_index: number, item: IEmployee): number {
    return item.id!;
  }
  protected onSuccess(data: IEmployee[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.employees = data ?? [];
    this.employeesListOutput.emit({
      searchName: this.searchName ? this.searchName : '',
      searchDepartment: this.searchDepartment ? this.searchDepartment : '',
      searchNhom: this.searchNhom ? this.searchNhom : '',
    });
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
}
