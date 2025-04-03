import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LuongComponent } from './list/luong.component';
import { LuongDeleteDialogComponent } from './delete/luong-delete-dialog.component';
import { LuongRoutingModule } from './route/luong-routing.module';
import { FlatpickrModule } from 'angularx-flatpickr';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { LuongUpdateComponent } from './update/luong-update.component';

@NgModule({
  imports: [SharedModule, LuongRoutingModule, FlatpickrModule, NgMultiSelectDropDownModule],
  declarations: [LuongComponent, LuongDeleteDialogComponent, LuongUpdateComponent],
  entryComponents: [LuongDeleteDialogComponent],
})
export class LuongModule {}
