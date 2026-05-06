package com.nihil.nihilsum.services;

import com.nihil.nihilsum.dtos.EventDTO;
import com.nihil.nihilsum.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventServices {
    private final EventRepository _eventRepository;

    public ResponseEntity<List<EventDTO>> getEvents(){
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(_eventRepository
                        .findAll()
                        .stream().map(EventDTO::new)
                        .toList()
                );
    }
}
