package com.example.ticket.services;

import com.example.ticket.models.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerService implements Runnable {
    private TicketPoolService ticketPoolService;
    private final int customerId;
    private int customerRetrievalRate;
    private boolean running = true;
    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    public CustomerService(TicketPoolService ticketPoolService, int customerId, int CustomerRetrievalRate) {
        this.ticketPoolService = ticketPoolService;
        this.customerId = customerId;
        this.customerRetrievalRate = CustomerRetrievalRate;
        if (!ticketPoolService.registerCustomer(customerId)) {
            throw new IllegalArgumentException("Customer ID " + customerId + " is already in use.");
        }
    }

    public void setTicketPoolService(TicketPoolService ticketPoolService) {
        this.ticketPoolService = ticketPoolService;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(customerRetrievalRate);
                if (ticketPoolService != null) {
                    Ticket ticket = ticketPoolService.removeTicket();
                    if (ticket != null) {
                        logger.info("Customer " + customerId + " purchased ticket " + ticket.getTicketId());
                    } else {
                        logger.warn("Customer " + customerId + " found no tickets available.");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Customer " + customerId + " interrupted.");
                break;
            }
        }
    }
}