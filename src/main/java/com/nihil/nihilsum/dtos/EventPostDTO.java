package com.nihil.nihilsum.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventPostDTO {
    private Long venueId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<TicketTierDTO> ticketTiers;
}
