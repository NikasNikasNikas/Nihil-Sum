package com.nihil.nihilsum.dtos;

import com.nihil.nihilsum.models.TicketTier;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketTierDTO {
    private Long id;
    private Long eventId;
    private BigDecimal ticketPrice;
    private Long total;
    private String tierDescription;

    TicketTierDTO(TicketTier ticketTier){
        this.id = ticketTier.getId();
        this.eventId = ticketTier.getEvent().getId();
        this.ticketPrice = ticketTier.getTicketPrice();
        this.total = ticketTier.getTotal();
        this.tierDescription = ticketTier.getTierDescription();
    }
}
