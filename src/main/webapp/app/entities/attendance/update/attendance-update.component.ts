import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAttendance, Attendance } from '../attendance.model';
import { AttendanceService } from '../service/attendance.service';
import { AttendanceDetail, IAttendanceDetail } from '../attendanceDetail.model';
import { IDepartment } from '../../employee/department.model';
import { IEmployee } from '../../employee/employee.model';
import { EmployeeService } from '../../employee/service/employee.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-attendance-update',
  templateUrl: './attendance-update.component.html',
})
export class AttendanceUpdateComponent implements OnInit {
  @ViewChild('addDetail') addDetail: TemplateRef<any> | undefined;
  isSaving = false;
  attendanceDetails?: IAttendanceDetail[] | any;
  attendance?: IAttendance[] | any;
  employeeList?: IEmployee[] | any;
  editForm = this.fb.group({
    id: [null, [Validators.required]],
    employeeId: [null, [Validators.required]],
    month: [],
    count: [],
    countNot: [],
    note: [],
  });

  editFormDetail = this.fb.group({
    id: [null, [Validators.required]],
    attendanceId: [null, [Validators.required]],
    in_time: [],
    out_time: [],
    note: [],
  });

  constructor(
    protected attendanceService: AttendanceService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected employeeService: EmployeeService,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ attendance }) => {
      this.updateForm(attendance);
      this.attendance = attendance;
    });
    this.attendanceService.queryAttendanceDetail().subscribe({
      next: (res: HttpResponse<IAttendanceDetail[]>) => {
        this.attendanceDetails = res.body;
      },
    });

    this.employeeService.queryAll().subscribe({
      next: (res: HttpResponse<IEmployee[]>) => {
        this.employeeList = res.body;
      },
    });
  }

  closeModal(): void {
    this.modalService.dismissAll();
  }

  previousState(): void {
    window.history.back();
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

  saveDetail(): void {
    this.isSaving = true;
    const attendanceDetail = this.createFromFormDetail();
    if (attendanceDetail.id !== undefined) {
      this.subscribeToSaveResponseDetail(this.attendanceService.update(attendanceDetail));
    } else {
      this.subscribeToSaveResponseDetail(this.attendanceService.create(attendanceDetail));
    }
  }
  newArr(lenght: number): any[] {
    if (lenght > 0) {
      return new Array(lenght);
    } else {
      return new Array(0);
    }
  }
  addDetails(attendance: IAttendance): void {
    this.modalService.open(this.addDetail, { size: 'lg', backdrop: 'static' });
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAttendance>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected subscribeToSaveResponseDetail(result: Observable<HttpResponse<IAttendanceDetail>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
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
      count: attendance.count,
      countNot: attendance.countNot,
      note: attendance.note,
    });
  }

  protected updateFormDetail(attendanceDetail: IAttendanceDetail): void {
    this.editForm.patchValue({
      id: attendanceDetail.id,
      attendanceId: attendanceDetail.attendanceId,
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
      count: this.editForm.get(['count'])!.value,
      countNot: this.editForm.get(['countNot'])!.value,
      note: this.editForm.get(['note'])!.value,
    };
  }

  protected createFromFormDetail(): IAttendanceDetail {
    return {
      ...new AttendanceDetail(),
      id: this.editFormDetail.get(['id'])!.value,
      attendanceId: this.editFormDetail.get(['attendanceId'])!.value,
      inTime: this.editFormDetail.get(['inTime'])!.value,
      outTime: this.editFormDetail.get(['outTime'])!.value,
      note: this.editFormDetail.get(['note'])!.value,
    };
  }
}
