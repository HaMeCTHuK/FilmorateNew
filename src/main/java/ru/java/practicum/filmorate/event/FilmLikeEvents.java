package ru.java.practicum.filmorate.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Event;
import ru.java.practicum.filmorate.storage.EventsStorage;

import java.util.List;

// регистрация событий лайков пользователя
@Slf4j
@Component
public class FilmLikeEvents implements Events {
    private final EventsStorage eventsStorage;

    @Autowired
    public FilmLikeEvents(EventsStorage eventsStorage) {
        this.eventsStorage = eventsStorage;
    }

    @Override
    public void add(Long userId, Long filmId) {
        log.info("Событие добавление лайка фильма {} пользователям добавлено в БД", filmId, userId);
        eventsStorage.addEvent(userId, filmId, EventType.LIKE, EventOperation.ADD);
    }

    @Override
    public void remove(Long userId, Long filmId) {
        log.info("Событие удаление лайка фильма {} пользователям добавлено в БД", filmId, userId);
        eventsStorage.addEvent(userId, filmId, EventType.LIKE, EventOperation.REMOVE);
    }

    @Override
    public void update(Long userId, Long entityId) {
    }

    @Override
    public List<Event> getFeed(Long userId) {
        return eventsStorage.getUserEvents(userId);
    }
}
