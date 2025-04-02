import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BaocaoComponent } from './list/baocao.component';
import { BaocaoRoutingModule } from './route/baocao-routing.module';
import { FlatpickrModule } from 'angularx-flatpickr';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';

@NgModule({
  imports: [SharedModule, BaocaoRoutingModule, FlatpickrModule, NgMultiSelectDropDownModule],
  declarations: [BaocaoComponent],
  entryComponents: [],
})
export class BaocaoModule {}
