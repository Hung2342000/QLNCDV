import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-navbar-vertical',
  templateUrl: './navbar-vertical.component.html',
  styleUrls: ['./navbar-vertical.component.scss'],
})
export class NavbarVerticalComponent {
  isNavbarCollapsed = true;
  isNavbarCollapsedLuong = true;
  isDropdownOpen = false;
  isDropdownOpenLuong = false;
  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  collapseNavbarLuong(): void {
    this.isNavbarCollapsedLuong = true;
  }

  toggleDropdownLuong(): void {
    this.isDropdownOpenLuong = !this.isDropdownOpenLuong;
  }
  closeDropdown(): void {
    this.isDropdownOpen = false;
  }
}
