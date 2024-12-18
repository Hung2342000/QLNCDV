import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import * as XLSX from 'xlsx';
import { IAttendance, Attendance } from '../attendance.model';
import { AttendanceService } from '../service/attendance.service';
import { AttendanceDetail, IAttendanceDetail } from '../attendanceDetail.model';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AttendanceDeleteDetailDialogComponent } from '../deleteDetail/attendanceDetail-delete-dialog.component';
import { DATE_FORMAT, DATE_FORMAT_CUSTOM, TIME_FORMAT } from '../../../config/input.constants';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-attendance-update',
  templateUrl: './attendance-update.component.html',
})
export class AttendanceUpdateComponent implements OnInit {
  @ViewChild('addDetail') addDetail: TemplateRef<any> | undefined;
  @ViewChild('deleteDetail') deleteDetail: TemplateRef<any> | undefined;
  @ViewChild('contentImportExcel') contentImportExcel: TemplateRef<any> | undefined;
  isSaving = false;
  attendanceDetails?: IAttendanceDetail[] | any;
  attendanceDetailsCheckImport?: IAttendanceDetail[] | any;
  attendanceDetailsImport?: IAttendanceDetail[] | any;
  attendance?: IAttendance | any;
  employeeList?: IEmployee[] | any;
  maxDate: Date = new Date();
  minDate: Date = new Date();
  checkUpload = false;
  importClicked = false;
  editForm = this.fb.group({
    id: [null, [Validators.required]],
    employeeId: [null, [Validators.required]],
    month: [],
    year: [],
    count: [],
    countNot: [],
    note: [],
  });

  editFormDetail = this.fb.group({
    id: [null, [Validators.required]],
    attendanceId: [],
    time: [],
    inTime: [],
    outTime: [],
    note: [],
  });

  constructor(
    protected attendanceService: AttendanceService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected employeeService: EmployeeService,
    protected modalService: NgbModal,
    protected router: Router
  ) {}

  ngOnInit(): void {
    this.attendanceDetails = [];
    this.activatedRoute.data.subscribe(({ attendance }) => {
      this.updateForm(attendance);
      this.attendance = attendance;
    });
    this.loadPage();
    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeeList = res.body;
      },
    });
  }
  closeModal(): void {
    this.loadPage();
    this.resetModalData();
    this.modalService.dismissAll();
  }
  closeModalDetail(): void {
    this.loadDetail();
    this.resetModalData();
    this.modalService.dismissAll();
    this.loadPage();
  }
  loadDetail(): void {
    this.attendanceService.find(this.attendance.id).subscribe({
      next: (res: HttpResponse<IAttendance>) => {
        this.attendance = res.body;
        this.updateForm(this.attendance);
      },
    });
  }
  resetModalData(): void {
    this.editFormDetail.reset();
  }
  previousState(): void {
    window.history.back();
  }

  saveDetail(): void {
    this.isSaving = true;
    const attendanceDetail = this.createFromDetail();
    if (attendanceDetail.outTime === null) {
      attendanceDetail.outTime = undefined;
    }
    if (attendanceDetail.inTime === null) {
      attendanceDetail.inTime = undefined;
    }
    attendanceDetail.attendanceId = this.attendance.id;
    if (attendanceDetail.id && typeof attendanceDetail.id === 'number') {
      this.subscribeToSaveResponseDetail(this.attendanceService.updateDetail(attendanceDetail));
    } else {
      this.subscribeToSaveResponseDetail(this.attendanceService.createDetail(attendanceDetail));
    }
  }
  edit(attendanceDetail: IAttendanceDetail): void {
    if (this.attendance.month && this.attendance.year) {
      const year = String(this.attendance.year);
      const month = String(this.attendance.month);
      this.minDate = new Date(`${year}-${month}-1`);
      if ([1, 3, 5, 7, 8, 10, 12].includes(this.attendance.month)) {
        this.maxDate = new Date(`${year}-${month}-31`);
      } else if ([4, 6, 9, 11].includes(this.attendance.month)) {
        this.maxDate = new Date(`${year}-${month}-30`);
      }
    }
    this.updateFormDetail(attendanceDetail);
    this.modalService.open(this.addDetail, { size: 'lg', backdrop: 'static' });
  }
  import(): void {
    this.modalService.open(this.contentImportExcel, { size: 'md', backdrop: 'static' });
  }
  save(): void {
    this.isSaving = true;
    const attendance = this.createFromForm();
    if (attendance.id !== undefined) {
      this.subscribeToSaveResponse(this.attendanceService.update(attendance));
    } else {
      this.subscribeToSaveResponse(this.attendanceService.create(attendance));
    }
  }

  exportToExcel(): void {
    this.attendanceService
      .exportToExcel({
        attendanceId: this.attendance.id,
      })
      .subscribe(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'bangchamcong.xlsx';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      });
  }
  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }
  addDetails(attendanceCheck: IAttendance): void {
    if (attendanceCheck.month && attendanceCheck.year) {
      this.minDate = new Date(attendanceCheck.year.toString() + '-' + attendanceCheck.month.toString() + '-1');
      if ([1, 3, 5, 7, 8, 10, 12].includes(attendanceCheck.month)) {
        this.maxDate = new Date(attendanceCheck.year.toString() + '-' + attendanceCheck.month.toString() + '-31');
      } else if ([4, 6, 9, 11].includes(attendanceCheck.month)) {
        this.maxDate = new Date(attendanceCheck.year.toString() + '-' + attendanceCheck.month.toString() + '-30');
      }
    }

    this.modalService.open(this.addDetail, { size: 'lg', backdrop: 'static' });
  }
  delete(attendanceDetail: AttendanceDetail): void {
    const modalRef = this.modalService.open(AttendanceDeleteDetailDialogComponent, { size: 'sm', backdrop: 'static' });
    modalRef.componentInstance.attendanceDetail = attendanceDetail;

    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
        this.loadDetail();
      }
    });
  }

  loadPage(): void {
    this.attendanceService.queryAttendanceDetail(this.attendance.id).subscribe({
      next: (res: HttpResponse<IAttendanceDetail[]>) => {
        this.attendanceDetails = res.body;
      },
    });
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
      this.attendanceDetailsCheckImport = XLSX.utils.sheet_to_json(ws, { header: 1 });
    };
    reader.readAsBinaryString(target.files[0]);
  }

  importExcel(): void {
    this.importClicked = true;
    if (this.checkUpload) {
      this.attendanceDetailsImport = [];
      if (this.attendanceDetailsCheckImport.length > 1) {
        for (let i = 1; i < this.attendanceDetailsCheckImport.length; i++) {
          const resultObject: IAttendanceDetail = {};
          resultObject.attendanceId = this.attendance.id;
          resultObject.time = dayjs(this.attendanceDetailsCheckImport[i][0], 'DD/MM/YYYY').add(1, 'day');
          resultObject.inTime =
            typeof this.attendanceDetailsCheckImport[i][1] !== 'string'
              ? this.attendanceDetailsCheckImport[i][1]
              : this.attendanceDetailsCheckImport[i][1];
          resultObject.outTime =
            typeof this.attendanceDetailsCheckImport[i][2] !== 'string'
              ? this.attendanceDetailsCheckImport[i][2]
              : this.attendanceDetailsCheckImport[i][2];
          resultObject.note = this.attendanceDetailsCheckImport[i][3];
          this.attendanceDetailsImport.push(resultObject);
        }
      }
    }
    if (this.attendanceDetailsImport.length > 0) {
      this.attendanceService.createAll(this.attendanceDetailsImport).subscribe(
        data => {
          alert('Thành công');
          this.loadDetail();
          this.resetModalData();
          this.modalService.dismissAll();
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
  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAttendance>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }
  protected subscribeToSaveResponseDetail(result: Observable<HttpResponse<IAttendanceDetail>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.closeModalDetail(),
      error: () => this.onSaveError(),
    });
  }
  protected onSaveSuccess(): void {
    // this.router.navigate([`/attendance/${employeeId}/edit`]);
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }
  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
  protected updateForm(attendance: IAttendance): void {
    this.editForm.patchValue({
      id: attendance.id,
      employeeId: attendance.employeeId,
      month: attendance.month,
      year: attendance.year,
      count: attendance.count,
      countNot: attendance.countNot,
      note: attendance.note,
    });
  }

  protected updateFormDetail(attendanceDetail: IAttendanceDetail): void {
    this.editFormDetail.patchValue({
      id: attendanceDetail.id,
      attendanceId: attendanceDetail.attendanceId,
      time: attendanceDetail.time?.format(DATE_FORMAT_CUSTOM),
      inTime: attendanceDetail.inTime,
      outTime: attendanceDetail.outTime,
      note: attendanceDetail.note,
    });
  }

  protected createFromForm(): IAttendance {
    return {
      ...new Attendance(),
      id: this.editForm.get(['id'])!.value,
      employeeId: this.editForm.get(['employeeId'])!.value,
      month: this.editForm.get(['month'])!.value,
      year: this.editForm.get(['year'])!.value,
      count: this.editForm.get(['count'])!.value,
      countNot: this.editForm.get(['countNot'])!.value,
      note: this.editForm.get(['note'])!.value,
    };
  }

  protected createFromDetail(): IAttendanceDetail {
    return {
      ...new AttendanceDetail(),
      id: this.editFormDetail.get(['id'])!.value,
      attendanceId: this.editFormDetail.get(['attendanceId'])!.value,
      time: this.editFormDetail.get(['time'])!.value,
      inTime: this.editFormDetail.get(['inTime'])!.value,
      outTime: this.editFormDetail.get(['outTime'])!.value,
      note: this.editFormDetail.get(['note'])!.value,
    };
  }
}
