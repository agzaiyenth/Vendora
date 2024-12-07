package com.example.ticket.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.ticket.models.Ticket;

public class VendorService implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);
    private TicketPoolService ticketPoolService;
    private int vendorId;
    private int ticketReleaseRate;
    private boolean running = true;

    public VendorService(TicketPoolService ticketPoolService, int vendorId, int ticketReleaseRate) {
        if (!ticketPoolService.registerVendor(vendorId)) {
            throw new IllegalArgumentException("Vendor ID " + vendorId + " is already in use.");
        }
        this.ticketPoolService = ticketPoolService;
        this.vendorId = vendorId;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public void setTicketPoolService(TicketPoolService ticketPoolService) {
        this.ticketPoolService = ticketPoolService;
    }

    @Override
    public void run() {
        int ticketId = 1;
        while (running) {
            try {
                Thread.sleep(ticketReleaseRate);
                if (ticketPoolService != null) {
                    if (ticketPoolService.getAvailableTickets()
                            + ticketPoolService.getTicketsSold() >= ticketPoolService.getMaxEventTickets()) {
                        logger.warn("Vendor {} cannot add ticket. Maximum event ticket limit reached.", vendorId);
                        return;
                    }
                    if (ticketPoolService.getAvailableTickets() >= ticketPoolService.getMaxPoolTickets()) {
                        logger.info("Vendor {} waiting, max pool limit reached.", vendorId);
                        continue;
                    }
                    Ticket ticket = new Ticket(ticketId++);
                    ticketPoolService.addTicket(vendorId, ticket);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Vendor {} interrupted.", vendorId);
                break;
            }
        }
    }
}
