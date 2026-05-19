package com.nihil.nihilsum.controllers;

import com.nihil.nihilsum.dtos.EventDTO;
import com.nihil.nihilsum.dtos.EventPostDTO;
import com.nihil.nihilsum.services.EventServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class EventController {
    private final EventServices _eventServices;

    @GetMapping("api/event")
    public ResponseEntity<List<EventDTO>> getEvents(){
        return _eventServices.getEvents();
    }

    @GetMapping("api/eventActive")
    public ResponseEntity<List<EventDTO>> getEventsActive() {
        ResponseEntity<List<EventDTO>> response = _eventServices.getEvents();
        List<EventDTO> activeEvents = response.getBody().stream()
                .filter(event -> event.getStartDate().isAfter(LocalDateTime.now()))
                .toList();
        return ResponseEntity.ok(activeEvents);
    }

    @GetMapping("api/event/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id){
        return _eventServices.getEventById(id);
    }


    @PostMapping("api/event")
    public ResponseEntity<?> postEvent(@Valid @RequestBody EventPostDTO eventPostDTO){
        return _eventServices.addEvent(eventPostDTO);
    }

    @PutMapping("api/event/{eventId}")
    public ResponseEntity<?> putEvent(@PathVariable Long eventId, @Valid @RequestBody EventPostDTO eventPostDTO){
        return _eventServices.updateEvent(eventId, eventPostDTO);
    }

    @DeleteMapping("api/event/{eventId}")
    public ResponseEntity<?> putEvent(@PathVariable Long eventId){
        return _eventServices.deleteEvent(eventId);
    }
}
