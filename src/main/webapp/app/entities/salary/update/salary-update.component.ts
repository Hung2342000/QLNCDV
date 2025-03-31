import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ISalary, Salary } from '../salary.model';
import { SalaryService } from '../service/salary.service';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from '../../../config/pagination.constants';
import { ISalaryDetail } from '../salaryDetail.model';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastComponent } from '../../../layouts/toast/toast.component';
import * as XLSX from 'xlsx';

@Component({
  selector: 'jhi-employee-update',
  templateUrl: './salary-update.component.html',
})
export class SalaryUpdateComponent implements OnInit {
  @ViewChild('addDetailSalary') addDetailSalary: TemplateRef<any> | undefined;
  @ViewChild('toastSalary') toastSalary!: ToastComponent;
  @ViewChild('importExcelModal') importExcelModal: TemplateRef<any> | undefined;

  salary?: ISalary | any;
  salaryDetails?: ISalaryDetail[] | any;
  salaryDetailsAm?: ISalaryDetail[] | any;
  salaryDetailsNVBH?: ISalaryDetail[] | any;
  salaryDetailsGDV?: ISalaryDetail[] | any;
  salaryDetailsKAM?: ISalaryDetail[] | any;
  salaryDetailsHTVP?: ISalaryDetail[] | any;
  salaryDetailsImportCheckGDV?: ISalaryDetail[] | any;
  salaryDetailsImportCheckAM?: ISalaryDetail[] | any;
  salaryDetailsImportCheckKAM?: ISalaryDetail[] | any;
  salaryDetailsImportCheckHTVP?: ISalaryDetail[] | any;
  salaryDetailsImportCheckNVBH?: ISalaryDetail[] | any;
  salaryDetailsImport?: ISalaryDetail[] | any;
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

  editForm = this.fb.group({
    id: [null, [Validators.required]],
    nameSalary: [],
    month: [],
    year: [],
    numberWork: [],
  });

  form: FormGroup = new FormGroup({
    details: new FormArray([new FormControl()]),
  });

  formAm: FormGroup = new FormGroup({
    detailsAm: new FormArray([new FormControl()]),
  });

  formGDV: FormGroup = new FormGroup({
    detailsGDV: new FormArray([new FormControl()]),
  });

  formKAM: FormGroup = new FormGroup({
    detailsKAM: new FormArray([new FormControl()]),
  });

  formNVBH: FormGroup = new FormGroup({
    detailsNBVH: new FormArray([new FormControl()]),
  });

  private updatedData: any;
  private updatedDataAm: any;
  private updatedDataGDV: any;
  private updatedDataKAM: any;
  private updatedDataNVBH: any;
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
      tenDonVi: [item.tenDonVi],
      dichVu: [item.dichVu],
    });
  }
  createRowFormAm(itemAm: any): FormGroup {
    return this.fb.group({
      id: [itemAm.id],
      salaryId: [itemAm.salaryId],
      employeeId: [itemAm.employeeId],
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
      tenDonVi: [itemAm.tenDonVi],
      dichVu: [itemAm.dichVu],
    });
  }

  createRowFormGDV(itemGDV: any): FormGroup {
    return this.fb.group({
      id: [itemGDV.id],
      salaryId: [itemGDV.salaryId],
      employeeId: [itemGDV.employeeId],
      diaBan: [itemGDV.diaBan],
      vung: [itemGDV.vung],
      cap: [itemGDV.cap],
      donGiaDichVu: [itemGDV.donGiaDichVu],
      mucChiToiThieu: [itemGDV.mucChiToiThieu],
      kpis: [itemGDV.kpis],
      htc: [itemGDV.htc],
      numberWorking: [itemGDV.numberWorking],
      numberWorkInMonth: [itemGDV.numberWorkInMonth],
      phiCoDinhDaThucHien: [itemGDV.phiCoDinhDaThucHien],
      luongCoDinhThucTe: [itemGDV.luongCoDinhThucTe],
      chiPhiGiamTru: [itemGDV.chiPhiGiamTru],
      mucBSLuongToiThieuVung: [itemGDV.mucBSLuongToiThieuVung],
      phiCoDinhThanhToanThucTe: [itemGDV.phiCoDinhThanhToanThucTe],
      chiPhiDichVuKhoanVaKK: [itemGDV.chiPhiDichVuKhoanVaKK],
      chiPhiKKKhac: [itemGDV.chiPhiKKKhac],
      tongChiPhiKVKK: [itemGDV.tongChiPhiKVKK],
      chiPhiThueDichVu: [itemGDV.chiPhiThueDichVu],
      nhom: [itemGDV.nhom],
      note: [itemGDV.note],
      tenDonVi: [itemGDV.tenDonVi],
      dichVu: [itemGDV.dichVu],
    });
  }
  createRowFormKAM(itemKAM: any): FormGroup {
    return this.fb.group({
      id: [itemKAM.id],
      salaryId: [itemKAM.salaryId],
      employeeId: [itemKAM.employeeId],
      diaBan: [itemKAM.diaBan],
      vung: [itemKAM.vung],
      chucDanh: [itemKAM.chucDanh],
      heSoChucVu: [itemKAM.heSoChucVu],
      soLuongHopDong: [itemKAM.soLuongHopDong],
      donGiaDichVu: [itemKAM.donGiaDichVu],
      mucChiToiThieu: [itemKAM.mucChiToiThieu],
      htc: [itemKAM.htc],
      numberWorking: [itemKAM.numberWorking],
      numberWorkInMonth: [itemKAM.numberWorkInMonth],
      phiCoDinhDaThucHien: [itemKAM.phiCoDinhDaThucHien],
      luongCoDinhThucTe: [itemKAM.luongCoDinhThucTe],
      chiPhiGiamTru: [itemKAM.chiPhiGiamTru],
      phiCoDinhThanhToanThucTe: [itemKAM.phiCoDinhThanhToanThucTe],
      chiPhiDichVuKhoanVaKK: [itemKAM.chiPhiDichVuKhoanVaKK],
      chiPhiThueDichVu: [itemKAM.chiPhiThueDichVu],
      nhom: [itemKAM.nhom],
      note: [itemKAM.note],
      tenDonVi: [itemKAM.tenDonVi],
      dichVu: [itemKAM.dichVu],
      apDungMucLuongCoDinh: [itemKAM.apDungMucLuongCoDinh],
    });
  }
  createRowFormNVBH(itemNVBH: any): FormGroup {
    return this.fb.group({
      id: [itemNVBH.id],
      salaryId: [itemNVBH.salaryId],
      employeeId: [itemNVBH.employeeId],
      diaBan: [itemNVBH.diaBan],
      vung: [itemNVBH.vung],
      donGiaDichVu: [itemNVBH.donGiaDichVu],
      mucChiToiThieu: [itemNVBH.mucChiToiThieu],
      kpis: [itemNVBH.kpis],
      htc: [itemNVBH.htc],
      numberWorking: [itemNVBH.numberWorking],
      numberWorkInMonth: [itemNVBH.numberWorkInMonth],
      phiCoDinhThanhToanThucTe: [itemNVBH.phiCoDinhThanhToanThucTe],
      mucChiPhiCoDinhThucTe: [itemNVBH.mucChiPhiCoDinhThucTe],
      chiPhiDichVuKhoanVaKK: [itemNVBH.chiPhiDichVuKhoanVaKK],
      chiPhiKKKhac: [itemNVBH.chiPhiKKKhac],
      tongChiPhiKVKK: [itemNVBH.tongChiPhiKVKK],
      chiPhiThueDichVu: [itemNVBH.chiPhiThueDichVu],
      nhom: [itemNVBH.nhom],
      chiPhiBoSungCPTTV: [itemNVBH.chiPhiBoSungCPTTV],
      tenDonVi: [itemNVBH.tenDonVi],
      dichVu: [itemNVBH.dichVu],
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
        this.salaryDetailsGDV = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'GDV');
        this.salaryDetailsKAM = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'KAM');
        this.salaryDetailsNVBH = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'NVBH');
        this.form = this.fb.group({
          details: this.fb.array(this.salaryDetailsHTVP.map((item: ISalaryDetail) => this.createRowForm(item))),
        });
        this.formAm = this.fb.group({
          detailsAm: this.fb.array(this.salaryDetailsAm.map((item: ISalaryDetail) => this.createRowFormAm(item))),
        });
        this.formGDV = this.fb.group({
          detailsGDV: this.fb.array(this.salaryDetailsGDV.map((item: ISalaryDetail) => this.createRowFormGDV(item))),
        });
        this.formKAM = this.fb.group({
          detailsKAM: this.fb.array(this.salaryDetailsKAM.map((item: ISalaryDetail) => this.createRowFormKAM(item))),
        });
        this.formNVBH = this.fb.group({
          detailsNVBH: this.fb.array(this.salaryDetailsNVBH.map((item: ISalaryDetail) => this.createRowFormNVBH(item))),
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
    this.updatedDataGDV = this.formGDV.value.detailsGDV;
    this.updatedDataKAM = this.formKAM.value.detailsKAM;
    this.updatedDataNVBH = this.formNVBH.value.detailsNVBH;
    this.mergedData = this.updatedData.concat(this.updatedDataAm);
    this.mergedData = this.mergedData.concat(this.updatedDataGDV);
    this.mergedData = this.mergedData.concat(this.updatedDataKAM);
    this.mergedData = this.mergedData.concat(this.updatedDataNVBH);
    if (this.mergedData.length > 0) {
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
    this.checkKK = true;
  }

  importExcel(): void {
    this.modalService.open(this.importExcelModal, { size: 'md', backdrop: 'static' });
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
      if (wsname === 'GDV') {
        this.salaryDetailsImportCheckGDV = XLSX.utils.sheet_to_json(ws, { header: 1 });
      }
      if (wsname === 'AM') {
        this.salaryDetailsImportCheckAM = XLSX.utils.sheet_to_json(ws, { header: 1 });
      }
      if (wsname === 'KAM') {
        this.salaryDetailsImportCheckKAM = XLSX.utils.sheet_to_json(ws, { header: 1 });
      }
      if (wsname === 'HTVP') {
        this.salaryDetailsImportCheckHTVP = XLSX.utils.sheet_to_json(ws, { header: 1 });
      }
      if (wsname === 'NVBH') {
        this.salaryDetailsImportCheckNVBH = XLSX.utils.sheet_to_json(ws, { header: 1 });
      }
    };
    reader.readAsBinaryString(target.files[0]);
  }

  import(): void {
    this.importClicked = true;
    if (this.checkUpload) {
      this.salaryDetailsImport = [];
      if (this.salaryDetailsImportCheckGDV && this.salaryDetailsImportCheckGDV.length > 0) {
        for (let i = 0; i < this.salaryDetailsImportCheckGDV.length; i++) {
          const resultObject: ISalaryDetail = {};
          if (
            this.salaryDetailsImportCheckGDV[i].length === 23 &&
            this.salaryDetailsImportCheckGDV[i][0] !== 'STT' &&
            this.salaryDetailsImportCheckGDV[i][0] !== '(1)' &&
            this.salaryDetailsImportCheckGDV[i][0] !== -1 &&
            this.salaryDetailsImportCheckGDV[i][0] !== 'Tổng cộng'
          ) {
            resultObject.employeeCode = this.salaryDetailsImportCheckGDV[i][1];
            resultObject.salaryId = this.salary.id;
            resultObject.tenDonVi = this.salaryDetailsImportCheckGDV[i][3];
            resultObject.diaBan = this.salaryDetailsImportCheckGDV[i][3];
            resultObject.vung = this.salaryDetailsImportCheckGDV[i][4];
            resultObject.cap = this.salaryDetailsImportCheckGDV[i][5];
            resultObject.donGiaDichVu = Number(this.salaryDetailsImportCheckGDV[i][6]);
            resultObject.mucChiToiThieu = Number(this.salaryDetailsImportCheckGDV[i][7]);
            resultObject.heSoChucVu = '1';
            resultObject.kpis = this.salaryDetailsImportCheckGDV[i][9];
            resultObject.htc = this.salaryDetailsImportCheckGDV[i][10];
            resultObject.numberWorkInMonth = Number(this.salaryDetailsImportCheckGDV[i][11]);
            resultObject.numberWorking = Number(this.salaryDetailsImportCheckGDV[i][12]);
            resultObject.phiCoDinhDaThucHien = Number(this.salaryDetailsImportCheckGDV[i][14]);
            resultObject.luongCoDinhThucTe = Number(this.salaryDetailsImportCheckGDV[i][15]);
            resultObject.chiPhiGiamTru = Number(this.salaryDetailsImportCheckGDV[i][16]);
            resultObject.mucBSLuongToiThieuVung = Number(this.salaryDetailsImportCheckGDV[i][17]);
            resultObject.phiCoDinhThanhToanThucTe = Number(this.salaryDetailsImportCheckGDV[i][18]);
            resultObject.chiPhiDichVuKhoanVaKK = Number(this.salaryDetailsImportCheckGDV[i][19]);
            resultObject.chiPhiKKKhac = Number(this.salaryDetailsImportCheckGDV[i][20]);
            resultObject.tongChiPhiKVKK = Number(this.salaryDetailsImportCheckGDV[i][21]);
            resultObject.chiPhiThueDichVu = Number(this.salaryDetailsImportCheckGDV[i][22]);
            this.salaryDetailsImport.push(resultObject);
          }
        }
      }
      if (this.salaryDetailsImportCheckAM && this.salaryDetailsImportCheckAM.length > 0) {
        for (let i = 0; i < this.salaryDetailsImportCheckAM.length; i++) {
          const resultObject: ISalaryDetail = {};
          if (
            this.salaryDetailsImportCheckAM[i].length === 18 &&
            this.salaryDetailsImportCheckAM[i][0] !== 'STT' &&
            this.salaryDetailsImportCheckAM[i][0] !== '(1)' &&
            this.salaryDetailsImportCheckAM[i][0] !== -1 &&
            this.salaryDetailsImportCheckAM[i][0] !== 'Tổng cộng'
          ) {
            resultObject.employeeCode = this.salaryDetailsImportCheckAM[i][1];
            resultObject.salaryId = this.salary.id;
            resultObject.vung = this.salaryDetailsImportCheckAM[i][3];
            resultObject.mucChiToiThieu = Number(this.salaryDetailsImportCheckAM[i][4]);
            resultObject.kpis = this.salaryDetailsImportCheckAM[i][6];
            resultObject.heSoChucVu = '1';
            resultObject.donGiaDichVu = Number(this.salaryDetailsImportCheckAM[i][8]);
            resultObject.luongCoDinhThucTe = Number(this.salaryDetailsImportCheckAM[i][7]);
            resultObject.numberWorkInMonth = Number(this.salaryDetailsImportCheckAM[i][9]);
            resultObject.numberWorking = Number(this.salaryDetailsImportCheckAM[i][10]);
            resultObject.phiCoDinhDaThucHien = Number(this.salaryDetailsImportCheckAM[i][11]);
            resultObject.chiPhiGiamTru = Number(this.salaryDetailsImportCheckAM[i][12]);
            resultObject.phiCoDinhThanhToanThucTe = Number(this.salaryDetailsImportCheckAM[i][13]);
            resultObject.chiPhiDichVuKhoanVaKK = Number(this.salaryDetailsImportCheckAM[i][14]);
            resultObject.chiPhiKKKhac = Number(this.salaryDetailsImportCheckAM[i][15]);
            resultObject.tongChiPhiKVKK = Number(this.salaryDetailsImportCheckAM[i][16]);
            resultObject.chiPhiThueDichVu = Number(this.salaryDetailsImportCheckAM[i][17]);
            this.salaryDetailsImport.push(resultObject);
          }
        }
      }
      if (this.salaryDetailsImportCheckKAM && this.salaryDetailsImportCheckKAM.length > 0) {
        for (let i = 0; i < this.salaryDetailsImportCheckKAM.length; i++) {
          const resultObject: ISalaryDetail = {};
          if (
            this.salaryDetailsImportCheckKAM[i].length === 20 &&
            this.salaryDetailsImportCheckKAM[i][0] !== 'STT' &&
            this.salaryDetailsImportCheckKAM[i][0] !== '(1)' &&
            this.salaryDetailsImportCheckKAM[i][0] !== -1 &&
            this.salaryDetailsImportCheckKAM[i][0] !== 'Tổng cộng'
          ) {
            resultObject.employeeCode = this.salaryDetailsImportCheckKAM[i][1];
            resultObject.salaryId = this.salary.id;
            resultObject.chucDanh = this.salaryDetailsImportCheckKAM[i][3];
            resultObject.diaBan = this.salaryDetailsImportCheckKAM[i][4];
            resultObject.soLuongHopDong = this.salaryDetailsImportCheckKAM[i][5];
            resultObject.apDungMucLuongCoDinh = this.salaryDetailsImportCheckKAM[i][6];
            resultObject.vung = this.salaryDetailsImportCheckKAM[i][7];
            resultObject.mucChiToiThieu = Number(this.salaryDetailsImportCheckKAM[i][8]);
            resultObject.heSoChucVu = this.salaryDetailsImportCheckKAM[i][9];
            resultObject.htc = this.salaryDetailsImportCheckKAM[i][10];
            resultObject.luongCoDinhThucTe = Number(this.salaryDetailsImportCheckKAM[i][11]);
            resultObject.donGiaDichVu = Number(this.salaryDetailsImportCheckKAM[i][12]);
            resultObject.numberWorkInMonth = Number(this.salaryDetailsImportCheckKAM[i][13]);
            resultObject.numberWorking = Number(this.salaryDetailsImportCheckKAM[i][14]);
            resultObject.phiCoDinhDaThucHien = Number(this.salaryDetailsImportCheckKAM[i][15]);
            resultObject.chiPhiGiamTru = Number(this.salaryDetailsImportCheckKAM[i][16]);
            resultObject.phiCoDinhThanhToanThucTe = Number(this.salaryDetailsImportCheckKAM[i][17]);
            resultObject.chiPhiDichVuKhoanVaKK = Number(this.salaryDetailsImportCheckKAM[i][18]);
            resultObject.chiPhiThueDichVu = Number(this.salaryDetailsImportCheckKAM[i][19]);
            this.salaryDetailsImport.push(resultObject);
          }
        }
      }
      if (this.salaryDetailsImportCheckHTVP && this.salaryDetailsImportCheckHTVP.length > 0) {
        for (let i = 0; i < this.salaryDetailsImportCheckHTVP.length; i++) {
          const resultObject: ISalaryDetail = {};
          if (
            this.salaryDetailsImportCheckHTVP[i].length === 15 &&
            this.salaryDetailsImportCheckHTVP[i][0] !== 'STT' &&
            this.salaryDetailsImportCheckHTVP[i][0] !== 'Tổng cộng'
          ) {
            resultObject.employeeCode = this.salaryDetailsImportCheckHTVP[i][1];
            resultObject.salaryId = this.salary.id;
            resultObject.diemCungCapDV = this.salaryDetailsImportCheckHTVP[i][3];
            resultObject.chucDanh = this.salaryDetailsImportCheckHTVP[i][4];
            resultObject.vung = this.salaryDetailsImportCheckHTVP[i][5];
            resultObject.donGiaDichVu = Number(this.salaryDetailsImportCheckHTVP[i][6]);
            resultObject.numberWorkInMonth = Number(this.salaryDetailsImportCheckHTVP[i][7]);
            resultObject.numberWorking = Number(this.salaryDetailsImportCheckHTVP[i][8]);
            resultObject.donGiaDichVuThucNhan = Number(this.salaryDetailsImportCheckHTVP[i][9]);
            resultObject.mucChiToiThieu = Number(this.salaryDetailsImportCheckHTVP[i][10]);
            resultObject.xepLoai = this.salaryDetailsImportCheckHTVP[i][11];
            resultObject.htc = this.salaryDetailsImportCheckHTVP[i][12];
            resultObject.chiPhiGiamTru = Number(this.salaryDetailsImportCheckHTVP[i][13]);
            resultObject.chiPhiThueDichVu = Number(this.salaryDetailsImportCheckHTVP[i][14]);
            this.salaryDetailsImport.push(resultObject);
          }
        }
      }
      if (this.salaryDetailsImportCheckNVBH && this.salaryDetailsImportCheckNVBH.length > 0) {
        for (let i = 0; i < this.salaryDetailsImportCheckNVBH.length; i++) {
          const resultObject: ISalaryDetail = {};
          if (
            this.salaryDetailsImportCheckNVBH[i].length === 18 &&
            this.salaryDetailsImportCheckNVBH[i][0] !== 'STT' &&
            this.salaryDetailsImportCheckNVBH[i][0] !== '(1)' &&
            this.salaryDetailsImportCheckNVBH[i][0] !== -1 &&
            this.salaryDetailsImportCheckNVBH[i][0] !== 'Tổng cộng'
          ) {
            resultObject.employeeCode = this.salaryDetailsImportCheckNVBH[i][1];
            resultObject.salaryId = this.salary.id;
            resultObject.diaBan = this.salaryDetailsImportCheckNVBH[i][3];
            resultObject.vung = this.salaryDetailsImportCheckNVBH[i][4];
            resultObject.donGiaDichVu = Number(this.salaryDetailsImportCheckNVBH[i][5]);
            resultObject.mucChiToiThieu = Number(this.salaryDetailsImportCheckNVBH[i][6]);
            resultObject.kpis = this.salaryDetailsImportCheckNVBH[i][7];
            resultObject.htc = this.salaryDetailsImportCheckNVBH[i][8];
            resultObject.numberWorkInMonth = Number(this.salaryDetailsImportCheckNVBH[i][9]);
            resultObject.numberWorking = Number(this.salaryDetailsImportCheckNVBH[i][10]);
            resultObject.phiCoDinhThanhToanThucTe = Number(this.salaryDetailsImportCheckNVBH[i][11]);
            resultObject.mucChiPhiCoDinhThucTe = Number(this.salaryDetailsImportCheckNVBH[i][12]);
            resultObject.chiPhiDichVuKhoanVaKK = Number(this.salaryDetailsImportCheckNVBH[i][13]);
            resultObject.chiPhiKKKhac = Number(this.salaryDetailsImportCheckNVBH[i][14]);
            resultObject.tongChiPhiKVKK = Number(this.salaryDetailsImportCheckNVBH[i][15]);
            resultObject.chiPhiBoSungCPTTV = Number(this.salaryDetailsImportCheckNVBH[i][16]);
            resultObject.chiPhiThueDichVu = Number(this.salaryDetailsImportCheckNVBH[i][17]);
            this.salaryDetailsImport.push(resultObject);
          }
        }
      }
      if (this.salaryDetailsImport.length > 0) {
        this.salaryService.createAllDetailImport(this.salaryDetailsImport).subscribe(
          data => {
            this.toastSalary.showToast('Thành công');
            this.closeModal();
            this.reload();
            setTimeout(() => {
              this.isEdit = false;
            }, 500);
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
  reload(): void {
    this.salaryService.queryAll(this.salary.id).subscribe({
      next: (res: HttpResponse<ISalaryDetail[]>) => {
        this.salaryDetails = res.body;
        this.salaryDetailsHTVP = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'HTVP');
        this.salaryDetailsAm = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'AM');
        this.salaryDetailsGDV = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'GDV');
        this.salaryDetailsKAM = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'KAM');
        this.salaryDetailsNVBH = this.salaryDetails.filter((item: ISalaryDetail) => item.nhom === 'NVBH');
        this.form = this.fb.group({
          details: this.fb.array(this.salaryDetailsHTVP.map((item: ISalaryDetail) => this.createRowForm(item))),
        });
        this.formAm = this.fb.group({
          detailsAm: this.fb.array(this.salaryDetailsAm.map((item: ISalaryDetail) => this.createRowFormAm(item))),
        });
        this.formGDV = this.fb.group({
          detailsGDV: this.fb.array(this.salaryDetailsGDV.map((item: ISalaryDetail) => this.createRowFormGDV(item))),
        });
        this.formKAM = this.fb.group({
          detailsKAM: this.fb.array(this.salaryDetailsKAM.map((item: ISalaryDetail) => this.createRowFormKAM(item))),
        });
        this.formNVBH = this.fb.group({
          detailsNVBH: this.fb.array(this.salaryDetailsNVBH.map((item: ISalaryDetail) => this.createRowFormNVBH(item))),
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
  trackIdGDV(_index: number, item: ISalaryDetail): number {
    return item.id!;
  }
  trackIdKAM(_index: number, item: ISalaryDetail): number {
    return item.id!;
  }
  trackIdNVBH(_index: number, item: ISalaryDetail): number {
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
  get detailsGDV(): FormArray {
    return this.formGDV.get('detailsGDV') as FormArray;
  }
  get detailsKAM(): FormArray {
    return this.formKAM.get('detailsKAM') as FormArray;
  }
  get details(): FormArray {
    return this.form.get('details') as FormArray;
  }
  get detailsAm(): FormArray {
    return this.formAm.get('detailsAm') as FormArray;
  }

  get detailsNVBH(): FormArray {
    return this.formNVBH.get('detailsNVBH') as FormArray;
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
