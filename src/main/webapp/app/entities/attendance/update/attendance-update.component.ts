import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { IAttendance } from '../attendance.model';
import { AttendanceService } from '../service/attendance.service';
import { IAttendanceDetail } from '../attendanceDetail.model';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastComponent } from '../../../layouts/toast/toast.component';
import { IDepartment } from '../../employee/department.model';

@Component({
  selector: 'jhi-attendance-update',
  templateUrl: './attendance-update.component.html',
  styleUrls: ['./attendance-update.component.scss'],
})
export class AttendanceUpdateComponent implements OnInit {
  @ViewChild('addDetail') addDetail: TemplateRef<any> | undefined;
  @ViewChild('deleteDetail') deleteDetail: TemplateRef<any> | undefined;
  @ViewChild('contentImportExcel') contentImportExcel: TemplateRef<any> | undefined;
  @ViewChild('toast') toast!: ToastComponent;
  isSaving = false;
  isEdit = true;
  attendanceDetails?: IAttendanceDetail[] | any;
  attendance?: IAttendance | any;
  employeeList?: IEmployee[] | any;
  form!: FormGroup;
  checkMonth?: number;
  content?: string;
  searchName?: string = '';
  searchCode?: string = '';
  searchDepartment?: string = '';
  searchNhom?: string = '';
  private updatedData: any;

  constructor(
    protected attendanceService: AttendanceService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected employeeService: EmployeeService,
    protected modalService: NgbModal,
    protected router: Router
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ attendance }) => {
      this.attendance = attendance;
      this.checkMonth = this.getDaysInMonth(attendance.month, attendance.year);
    });
    this.attendanceService
      .queryAll(this.attendance.id, {
        searchCode: this.searchCode,
        searchName: this.searchName,
        searchDepartment: this.searchDepartment,
      })
      .subscribe({
        next: (res: HttpResponse<IAttendanceDetail[]>) => {
          this.attendanceDetails = res.body;
          this.form = this.fb.group({
            details: this.fb.array(this.attendanceDetails.map((item: IAttendanceDetail) => this.createRowForm(item))),
          });
        },
      });

    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeeList = res.body;
      },
    });
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

  downloadPDF(): void {
    this.attendanceService
      .exportToPdf({
        attendanceId: this.attendance.id,
      })
      .subscribe(response => {
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'bangchamcong.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      });
  }
  edit(): void {
    this.isEdit = true;
  }

  close(): void {
    this.router.navigate([`/attendance`]);
  }

  trackId(_index: number, item: IAttendanceDetail): number {
    return item.id!;
  }
  getDaysInMonth(month: number, year: number): number {
    // Tháng là 1-based (1: Tháng 1, 2: Tháng 2, ...)
    // Kiểm tra nếu tháng không hợp lệ
    if (month < 1 || month > 12) {
      throw new Error('Tháng không hợp lệ. Tháng phải từ 1 đến 12.');
    }

    // Kiểm tra số ngày của tháng
    switch (month) {
      case 4:
      case 6:
      case 9:
      case 11:
        return 30; // Các tháng 4, 6, 9, 11 có 30 ngày
      case 2:
        // Kiểm tra năm nhuận cho tháng 2
        return year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0) ? 29 : 28;
      default:
        return 31; // Các tháng còn lại có 31 ngày
    }
  }

  createRowForm(item: any): FormGroup {
    return this.fb.group({
      id: [item.id],
      attendanceId: [item.attendanceId],
      employeeId: [item.employeeId],
      employeeCode: [item.employeeCode],
      employeeName: [item.employeeName],
      serviceTypeName: [item.serviceTypeName],
      department: [item.department],
      nhom: [item.nhom],
      day1: [item.day1],
      day2: [item.day2],
      day3: [item.day3],
      day4: [item.day4],
      day5: [item.day5],
      day6: [item.day6],
      day7: [item.day7],
      day8: [item.day8],
      day9: [item.day9],
      day10: [item.day10],
      day11: [item.day11],
      day12: [item.day12],
      day13: [item.day13],
      day14: [item.day14],
      day15: [item.day15],
      day16: [item.day16],
      day17: [item.day17],
      day18: [item.day18],
      day19: [item.day19],
      day20: [item.day20],
      day21: [item.day21],
      day22: [item.day22],
      day23: [item.day23],
      day24: [item.day24],
      day25: [item.day25],
      day26: [item.day26],
      day27: [item.day27],
      day28: [item.day28],
      day29: [item.day29],
      day30: [item.day30],
      day31: [item.day31],
      paidWorking: [item.paidWorking],
      numberWork: [item.numberWork],
    });
  }

  onSubmit(): void {
    this.updatedData = this.form.value.details; // Lấy toàn bộ dữ liệu từ FormArray
    if (this.updatedData.length > 0) {
      this.attendanceService.createAllDetail(this.updatedData).subscribe(
        data => {
          this.content = 'Lưu thành công';
          this.toast.showToast(this.content);
          this.reLoad();
          setTimeout(() => {
            this.isEdit = false;
          }, 500);
        },
        error => {
          this.content = 'Có lỗi xảy ra';
          this.toast.showToast(this.content);
        }
      );
    } else {
      this.content = 'Lỗi dữ liệu';
      this.toast.showToast(this.content);
    }
  }
  closeModal(): void {
    this.modalService.dismissAll();
  }
  reLoad(): void {
    this.attendanceService
      .queryAll(this.attendance.id, {
        searchCode: this.searchCode,
        searchName: this.searchName,
        searchDepartment: this.searchDepartment,
      })
      .subscribe({
        next: (res: HttpResponse<IAttendanceDetail[]>) => {
          this.attendanceDetails = res.body;
          this.form = this.fb.group({
            details: this.fb.array(this.attendanceDetails.map((item: IAttendanceDetail) => this.createRowForm(item))),
          });
        },
      });
  }
  closeModalDetail(): void {
    this.modalService.dismissAll();
  }
  previousState(): void {
    window.history.back();
  }

  saveDetail(): void {
    this.isSaving = true;
  }
  import(): void {
    this.modalService.open(this.contentImportExcel, { size: 'md', backdrop: 'static' });
  }
  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }

  isWeekend(date: Date): boolean {
    const day = date.getDay(); // Lấy chỉ số ngày trong tuần (0: Chủ Nhật, 6: Thứ 7)
    return day === 0 || day === 6; // Kiểm tra nếu là Chủ Nhật (0) hoặc Thứ 7 (6)
  }

  get details(): FormArray {
    return this.form.get('details') as FormArray;
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
}
