package com.ionixx.eventticketbooking.service;

import java.time.LocalDate;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ionixx.eventticketbooking.dto.CreateEventRequest;
import com.ionixx.eventticketbooking.dto.EventResponse;
import com.ionixx.eventticketbooking.entity.Event;
import com.ionixx.eventticketbooking.exception.DuplicateEventException;
import com.ionixx.eventticketbooking.exception.EventNotFoundException;
import com.ionixx.eventticketbooking.repository.EventRepository;

@Service
public class EventService {

    private static final String UNIQUE_CONSTRAINT = "uk_event_name_event_date";

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        // Keep this business rule in the service as well as DTO validation.
        if (request.eventDate() == null || !request.eventDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }

        if (eventRepository.existsByNameAndEventDate(request.name(), request.eventDate())) {
            throw new DuplicateEventException();
        }

        Event event = new Event(
            request.name(),
            request.venue(),
            request.category(),
            request.eventDate(),
            request.totalSeats(),
            request.basePrice()
        );

        try {
            Event savedEvent = eventRepository.saveAndFlush(event);
            return toResponse(savedEvent);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateNameDateConstraint(exception)) {
                throw new DuplicateEventException();
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Integer id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new EventNotFoundException(id));
        return toResponse(event);
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
            event.getId(),
            event.getName(),
            event.getVenue(),
            event.getCategory(),
            event.getEventDate(),
            event.getTotalSeats(),
            event.getBasePrice()
        );
    }

    private boolean isDuplicateNameDateConstraint(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof ConstraintViolationException violation) {
                return UNIQUE_CONSTRAINT.equals(violation.getConstraintName());
            }
            current = current.getCause();
        }
        return false;
    }
}
