package com.example.ticket;

import com.example.ticket.config.AppConfig;
import com.example.ticket.controllers.TicketingController;
import com.example.ticket.services.TicketPoolService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

public class TicketingCLI {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TicketPoolService ticketPoolService = context.getBean(TicketPoolService.class);
        TicketingController ticketingController = new TicketingController(ticketPoolService);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Ticketing System CLI!");

        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Set Max Event Tickets");
            System.out.println("2. Set Max Pool Tickets");
            System.out.println("3. Start Vendor");
            System.out.println("4. Start Customer");
            System.out.println("5. Stop All");
            System.out.println("6. Show Status");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter max event tickets: ");
                    int maxEventTickets = scanner.nextInt();
                    System.out.println(ticketingController.setMaxEventTickets(maxEventTickets).getBody());
                    break;
                case 2:
                    System.out.print("Enter max pool tickets: ");
                    int maxPoolTickets = scanner.nextInt();
                    System.out.println(ticketingController.setMaxPoolTickets(maxPoolTickets).getBody());
                    break;
                case 3:
                    System.out.print("Enter Vendor ID: ");
                    int vendorId = scanner.nextInt();
                    System.out.print("Enter Ticket Release Rate: ");
                    int ticketReleaseRate = scanner.nextInt();
                    System.out.println(ticketingController.startVendor(vendorId, ticketReleaseRate).getBody());
                    break;
                case 4:
                    System.out.print("Enter Customer ID: ");
                    int customerId = scanner.nextInt();
                    System.out.print("Enter Customer Retrieval Rate: ");
                    int customerRetrievalRate = scanner.nextInt();
                    System.out.println(ticketingController.startCustomer(customerId, customerRetrievalRate).getBody());
                    break;
                case 5:
                    System.out.println(ticketingController.stopAll().getBody());
                    break;
                case 6:
                    System.out.println(ticketingController.status().getBody());
                    break;
                case 7:
                    running = false;
                    System.out.println("Exiting... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        context.close();
        scanner.close();
    }
}
