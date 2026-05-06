package com.nihil.nihilsum.dtos;

import com.nihil.nihilsum.models.TicketTier;

import java.math.BigDecimal;

public class TicketTierDTO {
    private Long id;
    private Long eventId;
    private BigDecimal ticketPrice;
    private Long total;
    private Long available;

    TicketTierDTO(TicketTier ticketTier){
        this.id = ticketTier.getId();
        this.eventId = ticketTier.getEvent().getId();
        this.ticketPrice = ticketTier.getTicketPrice();
        this.total = ticketTier.getTotal();
        this.available = ticketTier.getAvailable();
    }
}
