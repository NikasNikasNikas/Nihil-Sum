package com.nihil.nihilsum.dtos;

import com.nihil.nihilsum.models.Event;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDTO {
    private Long id;
    private VenueDTO venue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<TicketTierDTO> ticketTiers;

    public EventDTO(Event event){
        this.id = event.getId();
        this.venue = new VenueDTO(event.getVenue());
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.ticketTiers = event.getTicketTiers().stream().map(TicketTierDTO::new).toList();
    }
}
