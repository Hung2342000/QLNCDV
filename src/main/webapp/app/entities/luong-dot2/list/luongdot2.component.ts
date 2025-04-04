import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { Luongdot2DeleteDialogComponent } from '../delete/luongdot2-delete-dialog.component';
import { FormBuilder, Validators } from '@angular/forms';

import { EmployeeService } from '../../employee/service/employee.service';
import * as XLSX from 'xlsx';
import dayjs from 'dayjs/esm';
import { ILuong } from '../../luong/luong.model';
import { ILuongDetail } from '../../luong/luongDetail.model';
import { LuongService } from '../../luong/service/luong.service';
import { ILuongDto } from '../../luong/luongDto.model';

@Component({
  selector: 'jhi-employee',
  templateUrl: './luongdot2.component.html',
})
export class Luongdot2Component implements OnInit {
  @ViewChild('addSalary') addSalary: TemplateRef<any> | undefined;
  @ViewChild('importExcelModal') importExcelModal: TemplateRef<any> | undefined;
  luongs?: ILuong[] | any;
  luongDetailsImportCheckNVBH?: ILuongDetail[] | any;
  luongDetailsImportCheckKAM?: ILuongDetail[] | any;
  luongDetailsImportCheckAM?: ILuongDetail[] | any;
  luongDetailsImportCheckGDV?: ILuongDetail[] | any;
  luongDetailsImport?: ILuongDetail[] | any;

  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  thang!: string;
  nam!: string;
  name!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  isSaving = false;
  checkUpload = false;
  employeeCheck: any;
  importClicked = false;

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
    protected luongService: LuongService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected fb: FormBuilder,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.luongService
      .querydot2({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ILuong[]>) => {
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
  }
  importExcel(): void {
    this.modalService.open(this.importExcelModal, { size: 'md', backdrop: 'static' });
  }
  loadPageSearch(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;

    this.luongService
      .query({
        page: 0,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ILuong[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, 1, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }
  trackId(_index: number, item: ILuong): number {
    return item.id!;
  }

  delete(luong: ILuong): void {
    const modalRef = this.modalService.open(Luongdot2DeleteDialogComponent, { size: 'sm', backdrop: 'static' });
    modalRef.componentInstance.luong = luong;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  import(): void {
    this.importClicked = true;
    if (this.checkUpload) {
      this.luongDetailsImport = [];

      if (this.luongDetailsImportCheckNVBH && this.luongDetailsImportCheckNVBH.length > 0) {
        for (let i = 0; i < this.luongDetailsImportCheckNVBH.length; i++) {
          const resultObject: ILuongDetail = {};
          if (
            this.luongDetailsImportCheckNVBH[i][1] !== undefined &&
            this.luongDetailsImportCheckNVBH[i][2] !== 2 &&
            typeof this.luongDetailsImportCheckNVBH[i][1] === 'number' &&
            this.luongDetailsImportCheckNVBH[i][2] !== '2' &&
            this.luongDetailsImportCheckNVBH[i][2] !== 2 &&
            this.luongDetailsImportCheckNVBH[i][2] !== ''
          ) {
            resultObject.employeeCode = this.luongDetailsImportCheckNVBH[i][2];
            resultObject.employeeName = this.luongDetailsImportCheckNVBH[i][3];
            resultObject.mst = this.luongDetailsImportCheckNVBH[i][4];
            resultObject.cccd = this.luongDetailsImportCheckNVBH[i][5];
            resultObject.diaban = this.luongDetailsImportCheckNVBH[i][6];
            resultObject.vung = this.luongDetailsImportCheckNVBH[i][7];
            resultObject.kpi = this.luongDetailsImportCheckNVBH[i][8];
            resultObject.hct = this.luongDetailsImportCheckNVBH[i][9];
            resultObject.numberWorkInMonth = Number(this.luongDetailsImportCheckNVBH[i][10]);
            resultObject.numberWorking = Number(this.luongDetailsImportCheckNVBH[i][11]);
            resultObject.soNgayKhongThucHien = Number(this.luongDetailsImportCheckNVBH[i][12]);
            resultObject.mucDongBHXH = Number(this.luongDetailsImportCheckNVBH[i][13]);
            resultObject.bhxhdn = Number(this.luongDetailsImportCheckNVBH[i][14]);
            resultObject.bhtnlddn = Number(this.luongDetailsImportCheckNVBH[i][15]);
            resultObject.bhytdn = Number(this.luongDetailsImportCheckNVBH[i][16]);
            resultObject.bhttdn = Number(this.luongDetailsImportCheckNVBH[i][27]);
            resultObject.tongBHdn = Number(this.luongDetailsImportCheckNVBH[i][18]);
            resultObject.cdcp2 = Number(this.luongDetailsImportCheckNVBH[i][19]);
            resultObject.luongCoDinh = Number(this.luongDetailsImportCheckNVBH[i][20]);
            resultObject.hoaHongKhoanSanPham = Number(this.luongDetailsImportCheckNVBH[i][21]);
            resultObject.chiPhiKKDaChi = Number(this.luongDetailsImportCheckNVBH[i][22]);
            resultObject.chiPhiKK = Number(this.luongDetailsImportCheckNVBH[i][23]);
            resultObject.hoaHongKhoanConLai = Number(this.luongDetailsImportCheckNVBH[i][24]);
            resultObject.chiPhiBoSungToiThieuVung = Number(this.luongDetailsImportCheckNVBH[i][15]);
            resultObject.tongCong = Number(this.luongDetailsImportCheckNVBH[i][26]);
            resultObject.bhxh = Number(this.luongDetailsImportCheckNVBH[i][27]);
            resultObject.bhyt = Number(this.luongDetailsImportCheckNVBH[i][28]);
            resultObject.bhtt = Number(this.luongDetailsImportCheckNVBH[i][29]);
            resultObject.tongBH = Number(this.luongDetailsImportCheckNVBH[i][30]);
            resultObject.tongLuongChiuThue = Number(this.luongDetailsImportCheckNVBH[i][31]);
            resultObject.npt = Number(this.luongDetailsImportCheckNVBH[i][32]);
            resultObject.giamTNCN = Number(this.luongDetailsImportCheckNVBH[i][33]);
            resultObject.thuNhapChiuThueThuNhap = Number(this.luongDetailsImportCheckNVBH[i][34]);
            resultObject.tncnThangTruoc = Number(this.luongDetailsImportCheckNVBH[i][35]);
            resultObject.truyThuHoanThue = Number(this.luongDetailsImportCheckNVBH[i][36]);
            resultObject.cdcp = Number(this.luongDetailsImportCheckNVBH[i][37]);
            resultObject.bhytBaoMuon = Number(this.luongDetailsImportCheckNVBH[i][38]);
            resultObject.truyThuCPPTTB = Number(this.luongDetailsImportCheckNVBH[i][38]);
            resultObject.truyThuLuongDot1 = Number(this.luongDetailsImportCheckNVBH[i][40]);
            resultObject.tongLinh = Number(this.luongDetailsImportCheckNVBH[i][41]);
            resultObject.luongTamUngLan1 = Number(this.luongDetailsImportCheckNVBH[i][42]);
            resultObject.luongLan2 = Number(this.luongDetailsImportCheckNVBH[i][43]);
            resultObject.nhom = 'NVBH';
            this.luongDetailsImport.push(resultObject);
          }
        }
      }

      if (this.luongDetailsImportCheckGDV && this.luongDetailsImportCheckGDV.length > 0) {
        for (let i = 0; i < this.luongDetailsImportCheckGDV.length; i++) {
          const resultObject: ILuongDetail = {};
          if (
            this.luongDetailsImportCheckGDV[i][1] !== undefined &&
            this.luongDetailsImportCheckGDV[i][2] !== 2 &&
            typeof this.luongDetailsImportCheckGDV[i][1] === 'number' &&
            this.luongDetailsImportCheckGDV[i][2] !== '2' &&
            this.luongDetailsImportCheckGDV[i][2] !== 2 &&
            this.luongDetailsImportCheckGDV[i][2] !== ''
          ) {
            resultObject.employeeCode = this.luongDetailsImportCheckGDV[i][2];
            resultObject.employeeName = this.luongDetailsImportCheckGDV[i][3];
            resultObject.mst = this.luongDetailsImportCheckGDV[i][4];
            resultObject.cccd = this.luongDetailsImportCheckGDV[i][5];
            resultObject.diaban = this.luongDetailsImportCheckGDV[i][6];
            resultObject.vung = this.luongDetailsImportCheckGDV[i][7];
            resultObject.cap = this.luongDetailsImportCheckGDV[i][8];
            resultObject.heSoChucVu = this.luongDetailsImportCheckGDV[i][9];
            resultObject.kpi = this.luongDetailsImportCheckGDV[i][10];
            resultObject.hct = this.luongDetailsImportCheckGDV[i][11];
            resultObject.numberWorkInMonth = Number(this.luongDetailsImportCheckGDV[i][12]);
            resultObject.numberWorking = Number(this.luongDetailsImportCheckGDV[i][13]);
            resultObject.soNgayKhongThucHien = Number(this.luongDetailsImportCheckGDV[i][14]);
            resultObject.mucDongBHXH = Number(this.luongDetailsImportCheckGDV[i][15]);
            resultObject.bhxhdn = Number(this.luongDetailsImportCheckGDV[i][16]);
            resultObject.bhtnlddn = Number(this.luongDetailsImportCheckGDV[i][17]);
            resultObject.bhytdn = Number(this.luongDetailsImportCheckGDV[i][18]);
            resultObject.bhttdn = Number(this.luongDetailsImportCheckGDV[i][19]);
            resultObject.tongBHdn = Number(this.luongDetailsImportCheckGDV[i][20]);
            resultObject.cdcp2 = Number(this.luongDetailsImportCheckGDV[i][21]);
            resultObject.luongCoDinh = Number(this.luongDetailsImportCheckGDV[i][22]);
            resultObject.hoaHongKhoanSanPham = Number(this.luongDetailsImportCheckGDV[i][23]);
            resultObject.chiPhiKKDaChi = Number(this.luongDetailsImportCheckGDV[i][24]);
            resultObject.chiPhiKK = Number(this.luongDetailsImportCheckGDV[i][25]);
            resultObject.hoaHongKhoanConLai = Number(this.luongDetailsImportCheckGDV[i][26]);
            resultObject.chiPhiTrucLe = Number(this.luongDetailsImportCheckGDV[i][27]);
            resultObject.chiPhiBoSungToiThieuVung = Number(this.luongDetailsImportCheckGDV[i][28]);
            resultObject.tongCong = Number(this.luongDetailsImportCheckGDV[i][29]);
            resultObject.bhxh = Number(this.luongDetailsImportCheckGDV[i][30]);
            resultObject.bhyt = Number(this.luongDetailsImportCheckGDV[i][31]);
            resultObject.bhtt = Number(this.luongDetailsImportCheckGDV[i][32]);
            resultObject.tongBH = Number(this.luongDetailsImportCheckGDV[i][33]);
            resultObject.tongLuongChiuThue = Number(this.luongDetailsImportCheckGDV[i][24]);
            resultObject.npt = Number(this.luongDetailsImportCheckGDV[i][35]);
            resultObject.giamTNCN = Number(this.luongDetailsImportCheckGDV[i][36]);
            resultObject.thuNhapChiuThueThuNhap = Number(this.luongDetailsImportCheckGDV[i][37]);
            resultObject.tncnThangTruoc = Number(this.luongDetailsImportCheckGDV[i][38]);
            resultObject.truyThuHoanThue = Number(this.luongDetailsImportCheckGDV[i][39]);
            resultObject.cdcp = Number(this.luongDetailsImportCheckGDV[i][40]);
            resultObject.bhytBaoMuon = Number(this.luongDetailsImportCheckGDV[i][41]);
            resultObject.truyThuCPPTTB = Number(this.luongDetailsImportCheckGDV[i][42]);
            resultObject.tongLinh = Number(this.luongDetailsImportCheckGDV[i][43]);
            resultObject.luongTamUngLan1 = Number(this.luongDetailsImportCheckGDV[i][44]);
            resultObject.luongLan2 = Number(this.luongDetailsImportCheckGDV[i][46]);
            resultObject.nhom = 'GDV';
            this.luongDetailsImport.push(resultObject);
          }
        }
      }
      if (this.luongDetailsImportCheckKAM && this.luongDetailsImportCheckKAM.length > 0) {
        for (let i = 0; i < this.luongDetailsImportCheckKAM.length; i++) {
          const resultObject: ILuongDetail = {};
          if (
            this.luongDetailsImportCheckKAM[i].length === 53 &&
            this.luongDetailsImportCheckKAM[i][0] !== 'STT' &&
            this.luongDetailsImportCheckKAM[i][0] !== '(1)' &&
            this.luongDetailsImportCheckKAM[i][0] !== -1 &&
            this.luongDetailsImportCheckKAM[i][0] !== 'Tổng cộng'
          ) {
            resultObject.employeeCode = this.luongDetailsImportCheckKAM[i][2];
            resultObject.employeeName = this.luongDetailsImportCheckKAM[i][3];
            resultObject.mst = this.luongDetailsImportCheckKAM[i][4];
            resultObject.cccd = this.luongDetailsImportCheckKAM[i][5];
            resultObject.chucDanh = this.luongDetailsImportCheckKAM[i][6];
            resultObject.diaban = this.luongDetailsImportCheckKAM[i][7];
            resultObject.soLuongHopDongThucTe = this.luongDetailsImportCheckKAM[i][8];
            resultObject.apDungMucLuongCoDinh = this.luongDetailsImportCheckKAM[i][9];
            resultObject.vung = this.luongDetailsImportCheckKAM[i][10];
            resultObject.numberWorkInMonth = Number(this.luongDetailsImportCheckKAM[i][22]);
            resultObject.numberWorking = Number(this.luongDetailsImportCheckKAM[i][23]);
            resultObject.phuCapAnCa = Number(this.luongDetailsImportCheckKAM[i][24]);
            resultObject.hoTroDiChuyen = Number(this.luongDetailsImportCheckKAM[i][25]);
            resultObject.hoTroDienThoai = Number(this.luongDetailsImportCheckKAM[i][26]);
            resultObject.mucDongBHXH = Number(this.luongDetailsImportCheckKAM[i][27]);
            resultObject.bhxhdn = Number(this.luongDetailsImportCheckKAM[i][28]);
            resultObject.bhtnlddn = Number(this.luongDetailsImportCheckKAM[i][29]);
            resultObject.bhytdn = Number(this.luongDetailsImportCheckKAM[i][30]);
            resultObject.bhttdn = Number(this.luongDetailsImportCheckKAM[i][31]);
            resultObject.tongBHdn = Number(this.luongDetailsImportCheckKAM[i][32]);
            resultObject.cdcp2 = Number(this.luongDetailsImportCheckKAM[i][33]);
            resultObject.luongCoDinh = Number(this.luongDetailsImportCheckKAM[i][34]);
            resultObject.hoaHongKhoanSanPham = Number(this.luongDetailsImportCheckKAM[i][35]);
            resultObject.tongCong = Number(this.luongDetailsImportCheckKAM[i][37]);
            resultObject.bhxh = Number(this.luongDetailsImportCheckKAM[i][38]);
            resultObject.bhyt = Number(this.luongDetailsImportCheckKAM[i][39]);
            resultObject.bhtt = Number(this.luongDetailsImportCheckKAM[i][40]);
            resultObject.tongBH = Number(this.luongDetailsImportCheckKAM[i][41]);
            resultObject.tongLuongChiuThue = Number(this.luongDetailsImportCheckKAM[i][42]);
            resultObject.soNguoiPhuThuoc = Number(this.luongDetailsImportCheckKAM[i][43]);
            resultObject.giamTNCN = Number(this.luongDetailsImportCheckKAM[i][44]);
            resultObject.thuNhapChiuThueThuNhap = Number(this.luongDetailsImportCheckKAM[i][45]);
            resultObject.tncnThangTruoc = Number(this.luongDetailsImportCheckKAM[i][46]);
            resultObject.truyThuHoanThue = Number(this.luongDetailsImportCheckKAM[i][47]);
            resultObject.cdcp = Number(this.luongDetailsImportCheckKAM[i][48]);
            resultObject.truyThuChiBoSung = Number(this.luongDetailsImportCheckKAM[i][49]);
            resultObject.tongLinh = Number(this.luongDetailsImportCheckKAM[i][50]);
            resultObject.luongTamUngLan1 = Number(this.luongDetailsImportCheckKAM[i][51]);
            resultObject.luongLan2 = Number(this.luongDetailsImportCheckKAM[i][52]);
            resultObject.nhom = 'KAM';
            this.luongDetailsImport.push(resultObject);
          }
        }
      }

      if (this.luongDetailsImportCheckAM && this.luongDetailsImportCheckAM.length > 0) {
        for (let i = 0; i < this.luongDetailsImportCheckAM.length; i++) {
          const resultObject: ILuongDetail = {};
          if (
            this.luongDetailsImportCheckAM[i][1] !== undefined &&
            this.luongDetailsImportCheckAM[i][2] !== 2 &&
            typeof this.luongDetailsImportCheckAM[i][1] === 'number' &&
            this.luongDetailsImportCheckAM[i][2] !== '2' &&
            this.luongDetailsImportCheckAM[i][2] !== 2 &&
            this.luongDetailsImportCheckAM[i][2] !== ''
          ) {
            resultObject.employeeCode = this.luongDetailsImportCheckAM[i][2];
            resultObject.employeeName = this.luongDetailsImportCheckAM[i][3];
            resultObject.mst = this.luongDetailsImportCheckAM[i][4];
            resultObject.cccd = this.luongDetailsImportCheckAM[i][5];
            resultObject.diaban = this.luongDetailsImportCheckAM[i][6];
            resultObject.vung = this.luongDetailsImportCheckAM[i][7];
            resultObject.heSoChucVu = this.luongDetailsImportCheckAM[i][8];
            resultObject.kpi = this.luongDetailsImportCheckAM[i][9];
            resultObject.numberWorkInMonth = this.luongDetailsImportCheckAM[i][10];
            resultObject.numberWorking = this.luongDetailsImportCheckAM[i][11];
            resultObject.soNgayKhongThucHien = this.luongDetailsImportCheckAM[i][12];
            resultObject.mucDongBHXH = this.luongDetailsImportCheckAM[i][13];
            resultObject.bhxhdn = Number(this.luongDetailsImportCheckAM[i][14]);
            resultObject.bhtnlddn = Number(this.luongDetailsImportCheckAM[i][15]);
            resultObject.bhytdn = Number(this.luongDetailsImportCheckAM[i][16]);
            resultObject.bhttdn = Number(this.luongDetailsImportCheckAM[i][17]);
            resultObject.tongBHdn = Number(this.luongDetailsImportCheckAM[i][18]);
            resultObject.cdcp2 = Number(this.luongDetailsImportCheckAM[i][19]);
            resultObject.luongCoDinh = Number(this.luongDetailsImportCheckAM[i][20]);
            resultObject.hoaHongKhoanSanPham = Number(this.luongDetailsImportCheckAM[i][21]);
            resultObject.chiPhiKKDaChi = Number(this.luongDetailsImportCheckAM[i][22]);
            resultObject.chiPhiKK = Number(this.luongDetailsImportCheckAM[i][23]);
            resultObject.hoaHongKhoanConLai = Number(this.luongDetailsImportCheckAM[i][24]);
            resultObject.tongCong = Number(this.luongDetailsImportCheckAM[i][25]);
            resultObject.bhxh = Number(this.luongDetailsImportCheckAM[i][26]);
            resultObject.bhyt = Number(this.luongDetailsImportCheckAM[i][27]);
            resultObject.bhtt = Number(this.luongDetailsImportCheckAM[i][28]);
            resultObject.tongBH = Number(this.luongDetailsImportCheckAM[i][29]);
            resultObject.tongLuongChiuThue = Number(this.luongDetailsImportCheckAM[i][30]);
            resultObject.soNguoiPhuThuoc = Number(this.luongDetailsImportCheckAM[i][31]);
            resultObject.giamTNCN = Number(this.luongDetailsImportCheckAM[i][32]);
            resultObject.thuNhapChiuThueThuNhap = Number(this.luongDetailsImportCheckAM[i][33]);
            resultObject.tncnThangTruoc = Number(this.luongDetailsImportCheckAM[i][34]);
            resultObject.truyThuHoanThue = Number(this.luongDetailsImportCheckAM[i][35]);
            resultObject.truyThuCPPTTB = Number(this.luongDetailsImportCheckAM[i][36]);
            resultObject.cdcp = Number(this.luongDetailsImportCheckAM[i][37]);
            resultObject.bhytBaoMuon = Number(this.luongDetailsImportCheckAM[i][38]);
            resultObject.tongLinh = Number(this.luongDetailsImportCheckAM[i][40]);
            resultObject.luongTamUngLan1 = Number(this.luongDetailsImportCheckAM[i][41]);
            resultObject.luongLan2 = Number(this.luongDetailsImportCheckAM[i][42]);
            resultObject.nhom = 'AM';
            this.luongDetailsImport.push(resultObject);
          }
        }
      }
      if (this.luongDetailsImport.length > 0) {
        const luongDto: ILuongDto = {
          id: undefined,
          createDate: dayjs(),
          nameSalary: this.name,
          dot: '2',
          month: this.thang,
          year: this.nam,
          luongDetails: this.luongDetailsImport,
        };
        this.luongService.createAllDetailImportLuong(luongDto).subscribe(
          data => {
            this.closeModal();
            this.loadPage();
          },
          error => {
            alert('có lỗi sảy ra');
          }
        );
      } else {
        alert('Dữ liệu không hợp lệ');
      }
    }
  }

  closeModal(): void {
    this.loadPage();
    this.modalService.dismissAll();
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
      const ws: XLSX.WorkSheet = wb.Sheets['NVBH'];
      const wsKAM: XLSX.WorkSheet = wb.Sheets['KAM'];
      const wsAM: XLSX.WorkSheet = wb.Sheets['AM'];
      const wsGDV: XLSX.WorkSheet = wb.Sheets['GDV'];

      this.luongDetailsImportCheckKAM = XLSX.utils.sheet_to_json(wsKAM, { header: 1 });
      this.luongDetailsImportCheckAM = XLSX.utils.sheet_to_json(wsAM, { header: 1 });

      this.luongDetailsImportCheckNVBH = XLSX.utils.sheet_to_json(ws, { header: 1 });
      this.luongDetailsImportCheckGDV = XLSX.utils.sheet_to_json(wsGDV, { header: 1 });
    };
    reader.readAsBinaryString(target.files[0]);
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

  protected onSuccess(data: ILuong[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/luongdot2'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.luongs = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
