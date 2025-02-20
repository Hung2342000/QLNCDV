import { Component, OnInit, OnDestroy, ViewChild, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { FormBuilder } from '@angular/forms';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EmployeeService } from '../entities/employee/service/employee.service';
import { ICountEmployee } from '../entities/employee/count-employee.model';
import { IDepartment } from '../entities/employee/department.model';
import { ChartConfiguration, ChartType } from 'chart.js';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  @ViewChild('content') content: TemplateRef<any> | undefined;
  account: Account | null = null;
  isSaving = false;
  countEmployee?: ICountEmployee[] | any;
  count = 0;
  countDLV = 0;
  departments: string[] = [];
  counts: number[] = [];
  countsGroup: number[] = [];
  groups: string[] = [];
  lineChartData?: ChartConfiguration['data'];
  lineChartDataByGroup?: ChartConfiguration['data'];

  // Cấu hình chung
  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: false },
      tooltip: { enabled: true },
    },
  };
  public lineChartOptionsGroup: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: true, position: 'right' },
      tooltip: { enabled: true },
    },
  };

  public lineChartType: ChartType = 'line';

  private readonly destroy$ = new Subject<void>();
  constructor(
    protected modalService: NgbModal,
    protected employeeService: EmployeeService,
    private accountService: AccountService,
    protected fb: FormBuilder,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.employeeService.queryCountEmployee().subscribe({
      next: (res: HttpResponse<ICountEmployee[]>) => {
        this.countEmployee = res.body;
        if (this.countEmployee && this.countEmployee.length > 0) {
          for (let i = 0; i < this.countEmployee.length; i++) {
            if (this.countEmployee[i].countEmployee && this.countEmployee[i].countEmployee > 0) {
              this.departments.push(this.countEmployee[i].code);
              this.counts.push(this.countEmployee[i].countEmployee);
            }
          }
        }
        this.lineChartData = {
          datasets: [
            {
              data: this.counts,
              label: 'Số nhân viên',
              fill: true,
              tension: 0.5,
              borderColor: '#4251f5',
              backgroundColor: '#010b3a',
              hoverBackgroundColor: '#e51e42',
            },
          ],
          labels: this.departments,
        };
      },
    });
    this.employeeService.queryCountEmployeeByGroup().subscribe({
      next: (res: HttpResponse<ICountEmployee[]>) => {
        this.countEmployee = res.body;
        if (this.countEmployee && this.countEmployee.length > 0) {
          for (let i = 0; i < this.countEmployee.length; i++) {
            if (this.countEmployee[i].code && this.countEmployee[i].code !== 'Đang làm việc') {
              this.groups.push(this.countEmployee[i].code);
              this.countsGroup.push(this.countEmployee[i].countEmployee);
            } else if (this.countEmployee[i].code && this.countEmployee[i].code === 'Đang làm việc') {
              this.countDLV = this.countEmployee[i].countEmployee;
            }
            if (this.countEmployee[i].countEmployee && this.countEmployee[i].countEmployee > 0) {
              this.count = this.count + Number(this.countEmployee[i].countEmployee);
            }
          }
        }
        this.lineChartDataByGroup = {
          datasets: [
            {
              data: this.countsGroup,
              label: 'Số nhân viên',
              fill: true,
              tension: 0.5,
              borderColor: '#4251f5',
              backgroundColor: ['#f5e642', '#42f59c', '#f54242', '#4251f5', '#4251f5'],
            },
          ],
          labels: this.groups,
        };
      },
    });
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
