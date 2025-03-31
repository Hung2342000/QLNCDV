import { NgModule } from '@angular/core';

import { SharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { TranslateDirective } from './language/translate.directive';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { DurationPipe } from './date/duration.pipe';
import { FormatMediumDatetimePipe } from './date/format-medium-datetime.pipe';
import { FormatMediumDatePipe } from './date/format-medium-date.pipe';
import { SortByDirective } from './sort/sort-by.directive';
import { SortDirective } from './sort/sort.directive';
import { ItemCountComponent } from './pagination/item-count.component';
import { FormatDateVnPipe } from './date/format-date-vn.pipe';
import { ToastComponent } from '../layouts/toast/toast.component';
import { EmployeeBoxComponent } from '../entities/box/employee-box/employee-box.component';
import { NgayNghiLeComponent } from '../entities/ngaynghile/ngay-nghi-le.component';
import { FlatpickrModule } from 'angularx-flatpickr';
@NgModule({
  imports: [SharedLibsModule, FlatpickrModule],
  declarations: [
    FindLanguageFromKeyPipe,
    TranslateDirective,
    AlertComponent,
    AlertErrorComponent,
    EmployeeBoxComponent,
    NgayNghiLeComponent,
    ToastComponent,
    HasAnyAuthorityDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
    FormatDateVnPipe,
  ],
  exports: [
    SharedLibsModule,
    FindLanguageFromKeyPipe,
    TranslateDirective,
    AlertComponent,
    ToastComponent,
    AlertErrorComponent,
    HasAnyAuthorityDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
    FormatDateVnPipe,
    EmployeeBoxComponent,
    NgayNghiLeComponent,
  ],
})
export class SharedModule {}
