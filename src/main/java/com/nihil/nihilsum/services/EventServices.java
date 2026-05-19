package com.nihil.nihilsum.services;

import com.nihil.nihilsum.dtos.EventDTO;
import com.nihil.nihilsum.dtos.EventPostDTO;
import com.nihil.nihilsum.models.Event;
import com.nihil.nihilsum.models.TicketTier;
import com.nihil.nihilsum.models.Venue;
import com.nihil.nihilsum.repositories.EventRepository;
import com.nihil.nihilsum.repositories.TicketRepository;
import com.nihil.nihilsum.repositories.TicketTierRepository;
import com.nihil.nihilsum.repositories.VenueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.nihil.nihilsum.services.TicketService;


@RequiredArgsConstructor
@Service
public class EventServices {
    private final EventRepository _eventRepository;
    private final VenueRepository _venueRepository;
    private final TicketTierRepository _ticketTierRepository;
    private final TicketService _ticketService;

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
        newEvent.setName(eventDTO.getName());
        newEvent.setStartDate(eventDTO.getStartDate());
        newEvent.setEndDate(eventDTO.getEndDate());
        newEvent.setVenue(venue.get());
        newEvent.setEventDescription(eventDTO.getEventDescription());

        List<TicketTier> ticketTiers = eventDTO.getTicketTiers()
                .stream()
                .map(dto -> {
                    TicketTier tier = new TicketTier();
                    tier.setTicketPrice(dto.getTicketPrice());
                    tier.setTotal(dto.getTotal());
                    tier.setTierDescription(dto.getTierDescription());

                    tier.setEvent(newEvent);

                    return tier;
                })
                .toList();

        newEvent.setTicketTiers(ticketTiers);

        _eventRepository.save(newEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventDTO(newEvent));
    }

    public ResponseEntity<List<EventDTO>> getEventsActive(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(_eventRepository
                        .findAll()
                        .stream()
                        .filter(event -> event.getStartDate().isAfter(LocalDateTime.now()))
                        .map(EventDTO::new)
                        .toList()
                );
    }

    public ResponseEntity<?> getEventById(Long id){
        Optional<Event> event = _eventRepository.findById(id);
        if (event.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        EventDTO dto = new EventDTO(event.get());

        /// Data obfuscation so people dont see it (no need to see it)
        dto.getVenue().setId(null);
        dto.getVenue().setOwnerId(null);
        dto.getVenue().setCapacity(null);
        // Calculate remaining tickets for each tier
        dto.getTicketTiers().forEach(tier -> {
            long remaining = _ticketService.getRemainingTickets(tier);
        });

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }




    @Transactional
    public ResponseEntity<?> updateEvent(Long eventId, EventPostDTO dto) {

        Optional<Event> existingEventOpt = _eventRepository.findById(eventId);

        if (existingEventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Event not found");
        }

        Optional<Venue> venueOpt = _venueRepository.findById(dto.getVenueId());

        if (venueOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Venue not found");
        }

        Event event = existingEventOpt.get();

        event.setName(dto.getName());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setVenue(venueOpt.get());
        event.setEventDescription(dto.getEventDescription());

        event.getTicketTiers().clear();
        // this deletes the tiers and replaces them with new ones
        List<TicketTier> newTiers = dto.getTicketTiers()
                .stream()
                .map(tierDto -> {

                    TicketTier tier = new TicketTier();

                    tier.setTicketPrice(tierDto.getTicketPrice());
                    tier.setTotal(tierDto.getTotal());
                    tier.setTierDescription(tierDto.getTierDescription());

                    tier.setEvent(event);

                    return tier;
                })
                .toList();

        event.getTicketTiers().addAll(newTiers);

        _eventRepository.save(event);

        return ResponseEntity.ok(new EventDTO(event));
    }

    @Transactional
    public ResponseEntity<?> deleteEvent(Long eventId) {

        Optional<Event> eventOpt = _eventRepository.findById(eventId);

        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Event not found");
        }

        _eventRepository.delete(eventOpt.get());

        return ResponseEntity.ok("Event deleted successfully");
    }
}
