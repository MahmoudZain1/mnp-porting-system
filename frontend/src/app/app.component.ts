import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // مهم للـ [(ngModel)]
import { PortingService } from './services/porting.service';
import { PortingRequest, MobileNumberStatus } from './models/porting.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  selectedOrg: string = 'orange';

  phoneNumberToCreate: string = '';
  createMessage: string = '';
  createError: string = '';
  isCreating: boolean = false;

  requestsList: PortingRequest[] = [];
  isLoadingRequests: boolean = false;

  phoneToCheck: string = '';
  numberStatusResult: MobileNumberStatus | null = null;
  statusError: string = '';

  constructor(private portingService: PortingService) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.isLoadingRequests = true;
    this.portingService.getRequests(this.selectedOrg).subscribe({
      next: (data) => {
        this.requestsList = data;
        this.isLoadingRequests = false;
      },
      error: (err) => {
        console.error('Error loading requests:', err);
        this.isLoadingRequests = false;
      }
    });
  }

  onCreateOrder(): void {
    if (!this.phoneNumberToCreate) {
      this.createError = 'Please enter a valid phone number';
      return;
    }

    this.isCreating = true;
    this.createMessage = '';
    this.createError = '';

    this.portingService.createRequest({ phoneNumber: this.phoneNumberToCreate }, this.selectedOrg).subscribe({
      next: (res) => {
        this.createMessage = `Porting request #${res.id} created successfully for ${res.phoneNumber}!`;
        this.phoneNumberToCreate = '';
        this.isCreating = false;
        this.loadRequests();
      },
      error: (err) => {
        this.createError = err.error?.message || 'Failed to create porting request';
        this.isCreating = false;
      }
    });
  }

  onCheckStatus(): void {
    if (!this.phoneToCheck) return;

    this.statusError = '';
    this.numberStatusResult = null;

    this.portingService.getNumberStatus(this.phoneToCheck, this.selectedOrg).subscribe({
      next: (res) => {
        this.numberStatusResult = res;
      },
      error: (err) => {
        this.statusError = err.error?.message || 'Phone number not found';
      }
    });
  }

  onOrgChange(): void {
    this.createMessage = '';
    this.createError = '';
    this.numberStatusResult = null;
    this.loadRequests();
  }
}
