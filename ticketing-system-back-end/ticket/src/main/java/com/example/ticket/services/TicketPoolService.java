package com.example.ticket.services;

import com.example.ticket.models.Ticket;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

@Service
public
class TicketPoolService {
    private final Queue<Ticket> ticketPool = new ConcurrentLinkedQueue<>();
    private int maxPoolTickets = 200;
    private int maxEventTickets = 1000;
    private int ticketsSold = 0;
    private static final Logger logger = Logger.getLogger(TicketPoolService.class.getName());
    private Set<Integer> registeredVendors = new HashSet<>();
    private Set<Integer> registeredCustomers = new HashSet<>();

    public synchronized void addTicket(int vendorId, Ticket ticket) {
        if ((ticketPool.size() + ticketsSold) >= maxEventTickets) {
            logger.severe("Vendor "+vendorId+" cannot add ticket. Maximum event tickets reached: "+ maxEventTickets);
            return;
        }
        if (ticketPool.size() >= maxPoolTickets) {
            logger.severe("Vendor "+ vendorId+" cannot add ticket. Maximum pool tickets reached: "+maxPoolTickets);
            return;
        }
        ticketPool.add(ticket);
        logger.info("Vendor "+vendorId+" added ticket "+ticket.getTicketId());
    }

    public synchronized void resetTicketPool() {
        ticketPool.clear();
        ticketsSold = 0;
        registeredVendors.clear();
        registeredCustomers.clear();
        logger.info("Ticket pool has been reset.");
    }

    public synchronized Ticket removeTicket() {
        Ticket ticket = ticketPool.poll();
        if (ticket != null) {
            ticketsSold++;
            logger.info("Ticket Purchased: " + ticket.getTicketId());
        } else {
            logger.warning("No tickets available for purchase.");
        }
        return ticket;
    }

    public int getAvailableTickets() {
        return ticketPool.size();
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public synchronized boolean registerVendor(int vendorId) {
        if (registeredVendors.contains(vendorId)) {
            logger.severe("Vendor ID "+vendorId+" is already registered.");
            return false;
        }
        registeredVendors.add(vendorId);
        logger.info("Vendor registered with ID "+ vendorId);
        return true;
    }

    public synchronized boolean registerCustomer(int customerId) {
        if (registeredCustomers.contains(customerId)) {
            logger.severe("Customer ID "+customerId+" is already registered.");
            return false;
        }
        registeredCustomers.add(customerId);
        logger.info("Customer registered with ID "+ customerId);
        return true;
    }

    public int getMaxEventTickets() {
        return maxEventTickets;
    }

    public int getMaxPoolTickets() {
        return maxPoolTickets;
    }

    public void setMaxEventTickets(int maxEventTickets) {
        this.maxEventTickets = maxEventTickets;
    }

    public void setMaxPoolTickets(int maxPoolTickets) {
        this.maxPoolTickets = maxPoolTickets;
    }
}