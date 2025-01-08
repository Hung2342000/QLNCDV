import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEmployee } from '../employee.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { EmployeeService } from '../service/employee.service';
import { EmployeeDeleteDialogComponent } from '../delete/employee-delete-dialog.component';
import { IDepartment } from '../department.model';
import { EmployeeDetailComponent } from '../detail/employee-detail.component';
import { IServiceType } from '../service-type.model';
import * as XLSX from 'xlsx';

@Component({
  selector: 'jhi-employee',
  templateUrl: './employee.component.html',
})
export class EmployeeComponent implements OnInit {
  @ViewChild('content') content: TemplateRef<any> | undefined;
  employees?: IEmployee[] | any;
  employeesImport?: IEmployee[] | any;
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  departments?: IDepartment[] | any;
  searchName?: string = '';
  searchCode?: string = '';
  searchDepartment?: string = '';
  searchNhom?: string = '';
  serviceTypesCustom?: IServiceType[] | any;
  checkUpload = false;
  importClicked = false;

  constructor(
    protected employeeService: EmployeeService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.employeeService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
        searchCode: this.searchCode,
        searchName: this.searchName,
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

  ngOnInit(): void {
    this.handleNavigation();
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

  loadPageSearch(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;

    this.employeeService
      .query({
        page: 0,
        size: this.itemsPerPage,
        sort: this.sort(),
        searchCode: this.searchCode,
        searchName: this.searchName,
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
  trackId(_index: number, item: IEmployee): number {
    return item.id!;
  }

  onFileChange(evt: any): void {
    if (evt.target.files && evt.target.files.length > 0) {
      this.checkUpload = true;
    }
    /* wire up file reader */
    const target: DataTransfer = <DataTransfer>evt.target;
    const reader: FileReader = new FileReader();
    reader.onload = (e: any) => {
      /* read workbook */
      const bstr: string = e.target.result;
      const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' });

      /* grab first sheet */
      const wsname: string = wb.SheetNames[0];
      const ws: XLSX.WorkSheet = wb.Sheets[wsname];

      /* save data */
      this.employeesImport = XLSX.utils.sheet_to_json(ws, { header: 1 });
    };
    reader.readAsBinaryString(target.files[0]);
  }
  delete(employee: IEmployee): void {
    const modalRef = this.modalService.open(EmployeeDeleteDialogComponent, { size: 'sm', backdrop: 'static' });
    modalRef.componentInstance.employee = employee;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
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

  importExcel(): void {
    this.importClicked = true;
  }
  closeModal(): void {
    this.modalService.dismissAll();
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

  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }
  view(employee: IEmployee): void {
    const modalRef = this.modalService.open(EmployeeDetailComponent, { size: 'xl', backdrop: 'static' });
    modalRef.componentInstance.employee = employee;
    modalRef.componentInstance.departments = this.departments;
    modalRef.componentInstance.serviceTypesCustom = this.serviceTypesCustom;
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

  protected onSuccess(data: IEmployee[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/employee'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.employees = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
