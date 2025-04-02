import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-navbar-vertical',
  templateUrl: './navbar-vertical.component.html',
  styleUrls: ['./navbar-vertical.component.scss'],
})
export class NavbarVerticalComponent {
  isNavbarCollapsed = true;
  isDropdownOpen = false;
  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  closeDropdown(): void {
    this.isDropdownOpen = false;
  }
}
