package com.nihil.nihilsum.services;

import com.nihil.nihilsum.dtos.EventDTO;
import com.nihil.nihilsum.dtos.EventPostDTO;
import com.nihil.nihilsum.models.Event;
import com.nihil.nihilsum.models.TicketTier;
import com.nihil.nihilsum.models.Venue;
import com.nihil.nihilsum.repositories.EventRepository;
import com.nihil.nihilsum.repositories.TicketRepository;
import com.nihil.nihilsum.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EventServices {
    private final EventRepository _eventRepository;
    private final VenueRepository _venueRepository;
    private final TicketRepository _ticketRepository;

    public ResponseEntity<List<EventDTO>> getEvents(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(_eventRepository
                        .findAll()
                        .stream().map(EventDTO::new)
                        .toList()
                );
    }

    public ResponseEntity<?> addEvent(EventPostDTO eventDTO){
        Optional<Venue> venue = _venueRepository.findById(eventDTO.getVenueId());
        if (venue.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Venue found");

        Event newEvent = new Event();
        // bro this setting bullshit is so bad, there should be a better way
        newEvent.setStartDate(eventDTO.getStartDate());
        newEvent.setEndDate(eventDTO.getEndDate());
        newEvent.setVenue(venue.get());

        List<TicketTier> ticketTiers = eventDTO.getTicketTiers()
                .stream()
                .map(dto -> {
                    TicketTier tier = new TicketTier();
                    tier.setTicketPrice(dto.getTicketPrice());
                    tier.setTotal(dto.getTotal());
                    tier.setAvailable(dto.getAvailable());

                    tier.setEvent(newEvent);

                    return tier;
                })
                .toList();

        newEvent.setTicketTiers(ticketTiers);

        _eventRepository.save(newEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventDTO(newEvent));
    }
}
