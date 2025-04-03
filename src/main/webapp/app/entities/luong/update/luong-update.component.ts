import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ILuong } from '../luong.model';
import { LuongService } from '../service/luong.service';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from '../../../config/pagination.constants';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastComponent } from '../../../layouts/toast/toast.component';
import { ILuongDetail } from '../luongDetail.model';

@Component({
  selector: 'jhi-employee-update',
  templateUrl: './luong-update.component.html',
  styleUrls: ['./luong-update.component.scss'],
})
export class LuongUpdateComponent implements OnInit {
  @ViewChild('toastSalary') toastSalary!: ToastComponent;

  luongDetailNVBH?: ILuongDetail | any;
  luongDetailAM?: ILuongDetail | any;
  luongDetailKAM?: ILuongDetail | any;
  luongDetailGDV?: ILuongDetail | any;
  luongDetails?: ILuongDetail | any;
  luong?: ILuong | any;

  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  isSaving = false;
  employeeList?: IEmployee[] | any;
  isEdit = false;
  checkKK = false;
  isEditAm = true;
  content?: string = '';
  checkUpload = false;
  importClicked = false;
  selectedTabIndex = 0; // Tab mặc định chọn là Tab 1

  tabs = [{ label: 'KAM' }, { label: 'NVBH' }, { label: 'AM' }, { label: 'GDV' }];

  constructor(
    protected modalService: NgbModal,
    protected employeeService: EmployeeService,
    protected router: Router,
    protected luongService: LuongService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ luong }) => {
      this.luong = luong;
    });
    this.luongService.queryAll(this.luong.id).subscribe({
      next: (res: HttpResponse<ILuongDetail[]>) => {
        this.luongDetails = res.body;
        this.luongDetailNVBH = this.luongDetails.filter((item: ILuongDetail) => item.nhom === 'NVBH');
        this.luongDetailKAM = this.luongDetails.filter((item: ILuongDetail) => item.nhom === 'KAM');
        this.luongDetailAM = this.luongDetails.filter((item: ILuongDetail) => item.nhom === 'AM');
        this.luongDetailGDV = this.luongDetails.filter((item: ILuongDetail) => item.nhom === 'GDV');
      },
    });
  }

  selectTab(index: number): void {
    this.selectedTabIndex = index;
  }
  previousState(): void {
    window.history.back();
  }

  edit(): void {
    this.isEdit = true;
    this.checkKK = true;
  }

  close(): void {
    this.router.navigate([`/luong`]);
  }

  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }

  closeModalDetail(): void {
    this.modalService.dismissAll();
  }
  closeModal(): void {
    this.modalService.dismissAll();
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
  protected onSaveError(): void {
    // Api for inheritance.
  }
  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
}
