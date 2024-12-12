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
  barChartData = [
    {
      data: [],
      label: 'Nhân viên',
      backgroundColor: '#2d3e4f',
      borderColor: 'rgb(54,111,235)',
      borderWidth: 1,
      hoverBackgroundColor: 'rgb(54,111,235)',
      hoverBorderColor: 'rgba(54,78,235,0.2)',
    },
  ];

  // Nhãn trục X
  barChartLabels: string[] | any;

  // Tùy chọn biểu đồ
  barChartOptions = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'none',
      },
    },
    scales: {
      x: {
        beginAtZero: true,
      },
      y: {
        beginAtZero: true,
      },
    },
  };

  private readonly destroy$ = new Subject<void>();
  constructor(
    protected modalService: NgbModal,
    protected employeeService: EmployeeService,
    private accountService: AccountService,
    private router: Router,
    protected fb: FormBuilder,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.barChartLabels = [];
    this.employeeService.queryCountEmployee().subscribe({
      next: (res: HttpResponse<ICountEmployee[]>) => {
        this.countEmployee = res.body;
        if (this.countEmployee && this.countEmployee.length > 0) {
          for (let i = 0; i < this.countEmployee.length; i++) {
            this.barChartLabels.push(this.countEmployee[i].code);
          }
        }
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
