package com.example.ticket.config;

import com.example.ticket.services.TicketPoolService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean(name="customTicketPoolService")
    public TicketPoolService ticketPoolService() {
        return new TicketPoolService();
    }
}
