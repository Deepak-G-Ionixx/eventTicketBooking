package com.ionixx.eventticketbooking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "event",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_event_name_event_date",
        columnNames = {"name", "event_date"}
    )
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String venue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventCategory category;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    protected Event() {
        // Required by JPA.
    }

    public Event(
        String name,
        String venue,
        EventCategory category,
        LocalDate eventDate,
        Integer totalSeats,
        BigDecimal basePrice
    ) {
        this.name = name;
        this.venue = venue;
        this.category = category;
        this.eventDate = eventDate;
        this.totalSeats = totalSeats;
        this.basePrice = basePrice;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public EventCategory getCategory() {
        return category;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
