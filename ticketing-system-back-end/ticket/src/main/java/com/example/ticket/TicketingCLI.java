package com.example.ticket;

import com.example.ticket.config.AppConfig;
import com.example.ticket.controllers.TicketingController;
import com.example.ticket.services.TicketPoolService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketingCLI {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TicketPoolService ticketPoolService = context.getBean(TicketPoolService.class);
        TicketingController ticketingController = new TicketingController(ticketPoolService);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);

        // Remove all handlers to ensure no logs are printed
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }


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

            int choice = getValidIntegerInput(scanner, "Invalid choice. Please enter a number between 1 and 7.");

            switch (choice) {
                case 1:
                    System.out.print("Enter max event tickets: ");
                    int maxEventTickets = getValidIntegerInput(scanner, "Enter max event tickets (greater than 0): ", true);
                    System.out.println(ticketingController.setMaxEventTickets(maxEventTickets).getBody());
                    break;
                case 2:
                    System.out.print("Enter max pool tickets: ");
                    int maxPoolTickets = getValidIntegerInput(scanner, "Enter max pool tickets (greater than 0): ", true);
                    System.out.println(ticketingController.setMaxPoolTickets(maxPoolTickets).getBody());
                    break;
                case 3:
                    System.out.print("Enter Vendor ID: ");
                    int vendorId = getValidIntegerInput(scanner, "Enter Vendor ID (greater than 0): ", true);
                    System.out.print("Enter Ticket Release Rate: ");
                    int ticketReleaseRate = getValidIntegerInput(scanner, "Enter Ticket Release Rate (greater than 0): ", true);
                    System.out.println(ticketingController.startVendor(vendorId, ticketReleaseRate).getBody());
                    break;
                case 4:
                    System.out.print("Enter Customer ID: ");
                    int customerId = getValidIntegerInput(scanner, "Enter Customer ID (greater than 0): ", true);
                    System.out.print("Enter Customer Retrieval Rate: ");
                    int customerRetrievalRate = getValidIntegerInput(scanner, "Enter Customer Retrieval Rate (greater than 0): ", true);
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

    /**
     * Helper method to get a valid integer input.
     *
     * @param scanner      the Scanner object for input
     * @param errorMessage the error message to display for invalid input
     * @return a valid integer
     */
    private static int getValidIntegerInput(Scanner scanner, String errorMessage) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(errorMessage);
                scanner.next(); // Clear the invalid input
            }
        }
    }

    /**
     * Overloaded helper method to get a valid integer input with a positive value constraint.
     *
     * @param scanner      the Scanner object for input
     * @param errorMessage the error message to display for invalid input
     * @param positiveOnly whether the input must be greater than 0
     * @return a valid integer
     */
    private static int getValidIntegerInput(Scanner scanner, String errorMessage, boolean positiveOnly) {
        while (true) {
            int value = getValidIntegerInput(scanner, errorMessage);
            if (!positiveOnly || value > 0) {
                return value;
            }
            System.out.println("Value must be greater than 0. Please try again.");
        }
    }
}
