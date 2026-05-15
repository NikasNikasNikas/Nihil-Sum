package com.nihil.nihilsum.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketTierPostDTO {
    private BigDecimal ticketPrice;
    private Long total;
    private String tierDescription;
}
