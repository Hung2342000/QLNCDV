import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { NgChartsModule } from 'ng2-charts';
import { FlatpickrModule } from 'angularx-flatpickr';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE]), NgChartsModule, FlatpickrModule],
  declarations: [HomeComponent],
})
export class HomeModule {}
