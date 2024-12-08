package com.example.ticket.services;


import com.example.ticket.models.Ticket;

import java.util.logging.Logger;

public class VendorService implements Runnable {
    private static final java.util.logging.Logger logger = Logger.getLogger(VendorService.class.getName());
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
                        logger.severe("Vendor "+vendorId+" cannot add ticket. Maximum event ticket limit reached.");
                        return;
                    }
                    if (ticketPoolService.getAvailableTickets() >= ticketPoolService.getMaxPoolTickets()) {
                        logger.info("Vendor "+vendorId+" waiting, max pool limit reached.");
                        continue;
                    }
                    Ticket ticket = new Ticket(ticketId++);
                    ticketPoolService.addTicket(vendorId, ticket);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.severe("Vendor "+vendorId+" interrupted.");
                break;
            }
        }
    }
}
