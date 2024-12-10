package com.example.ticket.controllers;

import com.example.ticket.services.CustomerService;
import com.example.ticket.services.TicketPoolService;
import com.example.ticket.services.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * REST Controller for managing the ticketing system.
 * Provides endpoints for vendors, customers, and ticket pool management.
 */
@RestController
@RequestMapping("/api/ticketing")
@CrossOrigin(origins = "http://localhost:4200")
public class TicketingController {
    /**
     * Executor service to manage concurrent tasks for vendors and customers.
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private boolean isStopped = false;
    private TicketPoolService ticketPoolService;


    /**
     * Constructor to inject the ticket pool service.
     *
     * @param ticketPoolService the ticket pool service to manage tickets.
     */
    @Autowired
    public TicketingController(TicketPoolService ticketPoolService) {
        this.ticketPoolService = ticketPoolService;
    }

    /**
     * Sets the maximum number of tickets for the event.
     *
     * @param maxEventTickets the maximum number of event tickets.
     * @return ResponseEntity with the success message.
     */
    @PostMapping("/set-max-event-tickets")
    public ResponseEntity<String> setMaxEventTickets(@RequestParam int maxEventTickets) {
        ticketPoolService.setMaxEventTickets(maxEventTickets);
        return ResponseEntity.ok("Max event tickets set to " + maxEventTickets);
    }

    /**
     * Sets the maximum number of tickets allowed in the ticket pool.
     *
     * @param maxPoolTickets the maximum number of pool tickets.
     * @return ResponseEntity with the success message.
     */
    @PostMapping("/set-max-pool-tickets")
    public ResponseEntity<String> setMaxPoolTickets(@RequestParam int maxPoolTickets) {
        ticketPoolService.setMaxPoolTickets(maxPoolTickets);
        return ResponseEntity.ok("Max pool tickets set to " + maxPoolTickets);
    }

    /**
     * Stops the system, resets the ticket pool, and restarts the executor service.
     *
     * @return ResponseEntity with the success message.
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopAll() {
        isStopped = true;
        executorService.shutdownNow();
        ticketPoolService.resetTicketPool();
        isStopped = false;
        this.executorService = Executors.newFixedThreadPool(10);
        return ResponseEntity.ok("System stopped and reset.");
    }

    /**
     * Starts a vendor simulation to add tickets at a given release rate.
     *
     * @param vendorId          the ID of the vendor.
     * @param ticketReleaseRate the rate at which the vendor adds tickets.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/start-vendor")
    public ResponseEntity<String> startVendor(@RequestParam int vendorId, @RequestParam int ticketReleaseRate) {
        if (vendorId <= 0 || ticketReleaseRate <= 0) {
            return ResponseEntity.badRequest().body("Vendor ID and release rate must be positive integers.");
        }
        VendorService vendor = new VendorService(ticketPoolService, vendorId, ticketReleaseRate);
        vendor.setTicketPoolService(ticketPoolService);
        executorService.submit(vendor);
        return ResponseEntity.ok("Vendor " + vendorId + " started.");
    }

    /**
     * Starts a customer simulation to retrieve tickets at a given retrieval rate.
     *
     * @param customerId           the ID of the customer.
     * @param customerRetrievalRate the rate at which the customer retrieves tickets.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/start-customer")
    public ResponseEntity<String> startCustomer(@RequestParam int customerId, @RequestParam int customerRetrievalRate) {
        if (customerId <= 0 || customerRetrievalRate <= 0) {
            return ResponseEntity.badRequest().body("Customer ID and retrieval rate must be positive integers.");
        }
        CustomerService customer = new CustomerService(ticketPoolService, customerId, customerRetrievalRate);
        customer.setTicketPoolService(ticketPoolService);
        executorService.submit(customer);
        return ResponseEntity.ok("Customer " + customerId + " started.");
    }

    /**
     * Retrieves the current status of the ticket pool.
     *
     * @return ResponseEntity containing the number of available tickets and tickets sold.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("availableTickets", ticketPoolService.getAvailableTickets());
        response.put("ticketsSold", ticketPoolService.getTicketsSold());
        return ResponseEntity.ok(response);
    }
}