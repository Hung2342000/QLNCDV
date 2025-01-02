import { Component } from '@angular/core';
@Component({
  selector: 'jhi-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss'],
})
export class ToastComponent {
  isVisible = false; // Trạng thái hiển thị của thông báo
  message = ''; // Nội dung thông báo

  // Hiển thị thông báo
  public showToast(message: string): void {
    this.message = message;
    this.isVisible = true;

    // Ẩn thông báo sau 3 giây
    setTimeout(() => {
      this.isVisible = false;
    }, 2000);
  }
}
