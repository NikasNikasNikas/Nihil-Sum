package com.nihil.nihilsum.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfoResponseDTO {
    private Long id;
    private String eventName;
    private String eventDescription;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String venueName;
    private String venueAddress;
    private String tierDescription;
    private BigDecimal ticketPrice;
    private LocalDateTime purchaseDate;
    private LocalDateTime lastScannedDate;
}