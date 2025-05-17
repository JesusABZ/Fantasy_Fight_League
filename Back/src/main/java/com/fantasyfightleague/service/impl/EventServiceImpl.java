package com.fantasyfightleague.service.impl;

import com.fantasyfightleague.model.Event;
import com.fantasyfightleague.repository.EventRepository;
import com.fantasyfightleague.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    
    private final EventRepository eventRepository;
    
    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
    
    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }
    
    @Override
    public List<Event> findUpcomingEvents() {
        return eventRepository.findByDateAfter(new Date());
    }
    
    @Override
    public List<Event> findPastEvents() {
        return eventRepository.findByDateBefore(new Date());
    }
    
    @Override
    public List<Event> findByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public List<Event> findByStatus(String status) {
        return eventRepository.findByStatus(status);
    }
    
    @Override
    public Optional<Event> findNextEvent() {
        List<Event> upcomingEvents = eventRepository.findByDateAfterOrderByDateAsc(new Date());
        if (upcomingEvents.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(upcomingEvents.get(0));
    }
    
    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }
    
    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
    
    @Override
    public Event updateStatus(Long id, String status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        event.setStatus(status);
        return eventRepository.save(event);
    }
}