package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.event.EventOperation;
import ru.java.practicum.filmorate.event.EventType;
import ru.java.practicum.filmorate.model.Event;

import java.util.List;

public interface EventsStorage extends AbstractStorage<Event> {
    void addEvent(Long userId, Long entityId, EventType eventType, EventOperation eventOperation);
    List<Event> getUserEvents(Long userId);
}
