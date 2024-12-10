# Real-Time Event Ticketing System

## Introduction
The Real-Time Event Ticketing System is a application for managing ticket releases and purchases in real-time. The system ensures data integrity through multi-threading and synchronization, supporting the following features:
- Setting maximum ticket limits for events and ticket pools.
- Adding vendors to release tickets and customers to purchase them at configurable rates.
- Monitoring ticket availability and sales.
- Stopping and resetting the system dynamically.

---

# Project Folder Structure

The project is organized as follows:

```plaintext
src/main/java/com/example/ticketingSystem
│
├── Controller
│   └── TicketingController.java   # Handles API requests and system controls
│
├── Config
│   └── AppConfig.java             # Handles CLI system controls
│
├── Models
│   ├── Vendor.java                # Defines the Vendor class
│   ├── Customer.java              # Defines the Customer class
│   └── Ticket.java                # Defines the Ticket class
│
├── Services
│   ├── VendorService.java         # Handles ticket release logic for vendors
│   ├── CustomerService.java       # Handles ticket purchase logic for customers
│   └── TicketPoolService.java     # Manages ticket operations and data synchronization
│
└── TicketApplication.java             # Main Spring Boot application entry point
└── TicketingCLI.java                       # Main CLI application entry point
```


# Setup Instructions

## Prerequisites

1. *Java Development Kit (JDK)*  
   Ensure you have JDK 21 (or above) installed. [Download here](https://www.oracle.com/java/technologies/javase-downloads.html).

2. *Maven*  
   Install Maven for building the application. [Download here](https://maven.apache.org/download.cgi).

3. *Node.js and Angular CLI*  
   For the frontend , install:
   - *Node.js* (v16 or later): [Download Node.js](https://nodejs.org/).
   - *Angular CLI*: [Install Angular CLI](https://angular.io/cli).

4. *IDE*  
   Use an IDE like [IntelliJ IDEA](https://www.jetbrains.com/idea/), [Eclipse](https://www.eclipse.org/), or [VS Code](https://code.visualstudio.com/) for a better development experience.

---

## How to Build and Run the Application

1. *Clone the Repository*  
   Open your terminal and execute the following commands:

   plaintext
   git clone https://github.com/agzaiyenth/Ticketing-System.git
   cd ticketing-system
   
# Build and Run Instructions

## Build the Backend

To build the backend, run the following command:

plaintext
mvn clean install
mvn spring-boot:run


## Set up the frontend:

plaintext
cd frontend
npm install
ng serve


## Access the application:

Backend: http://localhost:8080
Frontend: http://localhost:4200

# Usage Instructions
## Configuring and Starting the System
Set Ticket Limits
Use the following API endpoints to configure ticket limits:
```
Set event ticket limit: plaintext POST /api/eventticket/set-max-event-tickets 
Body: { "maxEventTickets": 100 }
Set pool ticket limit: POST /api/eventticket/set-max-pool-tickets 
Body: { "maxPoolTickets": 50 }
```
## Start Vendors and Customers 
Start vendors and customers to simulate ticket transactions:


```
Start a vendor: POST /api/eventticket/start-vendor?vendorId=1&ticketReleaseRate=5 
Start a customer:  POST /api/eventticket/start-customer?customerId=1&customerRetrievalRate=3
```
Monitor Status
```
plaintext Use GET /api/eventticket/status to view:
```

## Available tickets & Tickets sold
Stop and Reset the System
```
Use POST /api/eventticket/stop  to stop all processes and reset the ticket pool.
```

# UI Controls (Frontend) 
## Dashboard
Displays ticket statistics (available and sold).

## Controls
Buttons to start/stop the system and add vendors/customers dynamically.
