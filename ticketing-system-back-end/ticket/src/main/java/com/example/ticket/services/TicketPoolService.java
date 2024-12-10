package com.example.ticket.services;

import com.example.ticket.models.Ticket;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Service for managing a pool of tickets for an event.
 * This service handles ticket addition, removal, and registration
 * of vendors and customers, while ensuring constraints such as
 * maximum tickets for the event and the pool are adhered to.
 */
@Service
public class TicketPoolService {
    private final Queue<Ticket> ticketPool = new ConcurrentLinkedQueue<>();
    private int maxPoolTickets = 200;
    private int maxEventTickets = 1000;
    private int ticketsSold = 0;
    private static final Logger logger = Logger.getLogger(TicketPoolService.class.getName());
    private Set<Integer> registeredVendors = new HashSet<>();
    private Set<Integer> registeredCustomers = new HashSet<>();

    /**
     * Adds a ticket to the pool, ensuring constraints on maximum pool
     * and event tickets are respected.
     *
     * @param vendorId the ID of the vendor adding the ticket.
     * @param ticket   the ticket to be added.
     */
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

    /**
     * Resets the ticket pool, clearing all tickets, sold counts, and registrations.
     */
    public synchronized void resetTicketPool() {
        ticketPool.clear();
        ticketsSold = 0;
        registeredVendors.clear();
        registeredCustomers.clear();
        logger.info("Ticket pool has been reset.");
    }

    /**
     * Removes a ticket from the pool for purchase. Updates the sold count.
     *
     * @return the ticket removed, or null if the pool is empty.
     */
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

    /**
     * Retrieves the number of available tickets in the pool.
     *
     * @return the size of the ticket pool.
     */
    public int getAvailableTickets() {
        return ticketPool.size();
    }

    /**
     * Retrieves the number of tickets sold.
     *
     * @return the total number of tickets sold.
     */
    public int getTicketsSold() {
        return ticketsSold;
    }

    /**
     * Registers a vendor by their ID.
     *
     * @param vendorId the ID of the vendor to register.
     * @return true if the vendor was successfully registered, false otherwise.
     */
    public synchronized boolean registerVendor(int vendorId) {
        if (registeredVendors.contains(vendorId)) {
            logger.severe("Vendor ID "+vendorId+" is already registered.");
            return false;
        }
        registeredVendors.add(vendorId);
        logger.info("Vendor registered with ID "+ vendorId);
        return true;
    }

    /**
     * Registers a customer by their ID.
     *
     * @param customerId the ID of the customer to register.
     * @return true if the customer was successfully registered, false otherwise.
     */
    public synchronized boolean registerCustomer(int customerId) {
        if (registeredCustomers.contains(customerId)) {
            logger.severe("Customer ID "+customerId+" is already registered.");
            return false;
        }
        registeredCustomers.add(customerId);
        logger.info("Customer registered with ID "+ customerId);
        return true;
    }

    /**
     * Retrieves the maximum number of tickets allowed for the event.
     *
     * @return the maximum number of event tickets.
     */
    public int getMaxEventTickets() {
        return maxEventTickets;
    }

    /**
     * Retrieves the maximum number of tickets allowed in the pool.
     *
     * @return the maximum number of pool tickets.
     */
    public int getMaxPoolTickets() {
        return maxPoolTickets;
    }

    /**
     * Sets the maximum number of tickets allowed for the event.
     *
     * @param maxEventTickets the new maximum number of event tickets.
     */
    public void setMaxEventTickets(int maxEventTickets) {
        this.maxEventTickets = maxEventTickets;
    }

    /**
     * Sets the maximum number of tickets allowed in the pool.
     *
     * @param maxPoolTickets the new maximum number of pool tickets.
     */
    public void setMaxPoolTickets(int maxPoolTickets) {
        this.maxPoolTickets = maxPoolTickets;
    }
}