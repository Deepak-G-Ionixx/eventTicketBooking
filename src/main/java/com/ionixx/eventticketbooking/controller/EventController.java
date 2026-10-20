package com.ionixx.eventticketbooking.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ionixx.eventticketbooking.dto.CreateEventRequest;
import com.ionixx.eventticketbooking.dto.EventResponse;
import com.ionixx.eventticketbooking.exception.InvalidEventIdException;
import com.ionixx.eventticketbooking.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
        @Valid @RequestBody CreateEventRequest request
    ) {
        EventResponse response = eventService.createEvent(request);
        URI location = URI.create("/api/events/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable String id) {
        if (!id.matches("[0-9]+")) {
            throw new InvalidEventIdException();
        }

        final int eventId;
        try {
            eventId = Integer.parseInt(id);
        } catch (NumberFormatException exception) {
            throw new InvalidEventIdException();
        }

        if (eventId <= 0) {
            throw new InvalidEventIdException();
        }

        return ResponseEntity.ok(eventService.getEventById(eventId));
    }
}
