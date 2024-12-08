import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { interval, Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { CommonModule } from '@angular/common';
import {
  ApexChart,
  ApexAxisChartSeries,
  ApexXAxis,
  ApexTitleSubtitle,
  ChartComponent,
} from 'ng-apexcharts';
import { NgApexchartsModule } from 'ng-apexcharts';
import ApexCharts from 'apexcharts';

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  title: ApexTitleSubtitle;
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [FormsModule, CommonModule, NgApexchartsModule],
})
export class DashboardComponent implements OnInit, OnDestroy {
  availableTickets: number = 0;
  ticketsSold: number = 0;
  vendorId?: number;
  ticketReleaseRate?: number;
  customerId?: number;
  customerRetrievalRate?: number;
  maxEventTickets?: number;
  maxPoolTickets?: number;
  message?: string;
  private pollingSubscription?: Subscription;
  private isStopped: boolean = false;
  logs: string[] = [];

  public chartOptions: Partial<ChartOptions> | any;

  constructor(private http: HttpClient) {
    
  }

  ngOnInit(): void {
    this.startPolling();
    this.pollingSubscription = interval(10).subscribe(() => {
      this.getTicketCount();
    });
    this.addLog('Dashboard initialized.');
  }

  startPolling() {
    this.pollingSubscription = interval(500).subscribe(() => {
      this.getTicketCount();
    
    });
  }

  getTicketCount() {
    this.http
      .get<{ availableTickets: number; ticketsSold: number }>(
        'http://localhost:8080/api/ticketing/status'
      )
      .subscribe(
        (response) => {
          this.availableTickets = response.availableTickets;
          this.ticketsSold = response.ticketsSold;
          this.isStopped = false;
    
        },
        (error) => {
          console.error('Error fetching ticket count', error);

          if (!this.isStopped) {
            this.message =
              'Application is stopped. Ticket count not available.';
            this.isStopped = true;
          }
        }
      );
  }
  
  startVendor() {
    if (
      !this.vendorId ||
      !this.ticketReleaseRate ||
      this.vendorId <= 0 ||
      this.ticketReleaseRate <= 0
    ) {
      this.message =
        'Please enter valid positive values for Vendor ID and Ticket  Release Rate.';
        this.addLog('Invalid vendor input detected.');
      return;
    }
    this.addLog(`Vendor ${this.vendorId} started with rate ${this.ticketReleaseRate} ms.`);
    this.http
      .post('http://localhost:8080/api/ticketing/start-vendor', null, {
        params: {
          vendorId: this.vendorId.toString(),
          ticketReleaseRate: this.ticketReleaseRate.toString(),
        },
        responseType: 'text',
      })
      .subscribe(
        (response) => {
          this.message = response;
          this.getTicketCount();
        },
        (error) => {
          console.error('Error starting vendor', error);
          this.message = 'Failed to start vendor';
        }
      );
  }

  startCustomer() {
    if (
      !this.customerId ||
      !this.customerRetrievalRate ||
      this.customerId <= 0 ||
      this.customerRetrievalRate <= 0
    ) {
      this.message =
        'Please enter valid positive values for Customer ID and Customer Retrieval Rate.';
        this.addLog('Invalid customer input detected.');
      return;
    }
    this.addLog(`Customer ${this.customerId} started with rate ${this.customerRetrievalRate} ms.`);
    this.http
      .post('http://localhost:8080/api/ticketing/start-customer', null, {
        params: {
          customerId: this.customerId.toString(),
          customerRetrievalRate: this.customerRetrievalRate.toString(),
        },
        responseType: 'text',
      })
      .subscribe(
        (response) => {
          this.message = response;
          this.getTicketCount();
        },
        (error) => {
          console.error('Error starting customer', error);
          this.message = 'Failed to start customer';
        }
      );
  }

  setMaxEventTickets() {
    this.http
      .post(
        `http://localhost:8080/api/ticketing/set-max-event-tickets?maxEventTickets=${this.maxEventTickets}`,
        null
      )
      .subscribe((response) => {
        this.message = `Max event tickets set to ${this.maxEventTickets}`;
      });
  }

  setMaxPoolTickets() {
    this.http
      .post(
        `http://localhost:8080/api/ticketing/set-max-pool-tickets?maxPoolTickets=${this.maxPoolTickets}`,
        null
      )
      .subscribe((response) => {
        this.message = `Max pool tickets set to ${this.maxPoolTickets}`;
      });
  }
  addLog(message: string): void {
    const timestamp = new Date().toISOString();
    this.logs.push(`[${timestamp}] ${message}`);
  }

  clearLogs(): void {
    this.logs = [];
    this.addLog('Logs cleared.');
  }
  stopAll() {
    this.http
      .post('http://localhost:8080/api/ticketing/stop', null, {
        responseType: 'text',
      })
      .subscribe(
        (response) => {
          this.message = 'System stopped and reset.';
          this.isStopped = true;

          // Reset frontend values
          this.availableTickets = 0;
          this.ticketsSold = 0;
          this.vendorId = 0;
          this.ticketReleaseRate = 0;
          this.customerId = 0;
          this.customerRetrievalRate = 0;
          this.maxEventTickets = 1000;
          this.maxPoolTickets = 200;

          // Unsubscribe from polling
          if (this.pollingSubscription) {
            this.pollingSubscription.unsubscribe();
          }
          this.addLog('Emergency stop triggered.');
        },
        (error) => {
          console.error('Error stopping and resetting system', error);
          this.message = 'Failed to stop and reset system.';
        }
        
      );
  }
  ngOnDestroy(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }
}
