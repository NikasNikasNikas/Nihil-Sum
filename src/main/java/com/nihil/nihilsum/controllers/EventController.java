package com.nihil.nihilsum.controllers;

import com.nihil.nihilsum.dtos.EventDTO;
import com.nihil.nihilsum.dtos.EventPostDTO;
import com.nihil.nihilsum.services.EventServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EventController {
    private final EventServices _eventServices;
    @GetMapping("api/Event")
    public ResponseEntity<List<EventDTO>> getEvents(){
        return _eventServices.getEvents();
    }

    @PostMapping("api/Event")
    public ResponseEntity<?> postEvent(@Validated @RequestBody EventPostDTO eventPostDTO){
        return _eventServices.addEvent(eventPostDTO);
    }
}
