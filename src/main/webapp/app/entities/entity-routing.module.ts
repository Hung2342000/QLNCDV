import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'employee',
        data: { pageTitle: 'qldvApp.employee.home.title' },
        loadChildren: () => import('./employee/employee.module').then(m => m.EmployeeModule),
      },
      {
        path: 'attendance',
        data: { pageTitle: 'qldvApp.attendance.home.title' },
        loadChildren: () => import('./attendance/attendance.module').then(m => m.AttendanceModule),
      },
      {
        path: 'salary',
        data: { pageTitle: 'qldvApp.salary.home.title' },
        loadChildren: () => import('./salary/salary.module').then(m => m.SalaryModule),
      },
      {
        path: 'baocao',
        data: { pageTitle: 'qldvApp.baocao.home.title' },
        loadChildren: () => import('./baocao/baocao.module').then(m => m.BaocaoModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
