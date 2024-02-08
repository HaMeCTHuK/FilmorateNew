package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.event.Events;
import ru.java.practicum.filmorate.event.LikeEvents;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private static final LocalDate LAST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final Events event;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage")  FilmStorage filmStorage,
                                                    UserStorage userStorage,
                                                    LikesStorage likesStorage,
                                                    GenreStorage genreStorage,
                                                    EventsStorage eventsStorage) {

        this.abstractStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesStorage = likesStorage;
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;

        this.event = new LikeEvents(eventsStorage);
    }

    @Override
    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(LAST_RELEASE_DATE)) {
            log.warn("Дата выпуска меньше 1895.12.28 : {}", film.getReleaseDate());
            throw new ValidationException("Дата выпуска меньше 1895.12.28");
        }
    }

    @Override
    public void validateParameter(Long filmId) {
        if (filmId == null) {
            throw new IncorrectParameterException("Некорректные параметры поля, проверь null");
        }
        if (getData(filmId) == null) {
            throw new DataNotFoundException("Фильма с айди нет" + filmId);
        }
    }

    @Override
    public void validateParameters(Long filmId, Long userId) {
        User user = userStorage.get(userId);
        Film film = getData(filmId);
        if (film == null || user == null) {
            log.info("Ошибка валидации. Проверь null");
            log.info("Ошибка валидации. Такого айди пользователя: {} или фильма {} нет", userId, filmId);
            throw new IncorrectParameterException("Некорректные параметры полей, проверь null");
        }
    }

    public void addLike(long filmId, long userId) {
        validateParameters(filmId, userId);
        log.info("Добавляем лайк от пользователя с айди : {} для фильма {}", userId, filmId);
        likesStorage.addLike(filmId, userId);
        event.add(userId, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        validateParameters(filmId, userId);
        log.info("Удаляем лайк от пользователя с айди : {}", userId);
        likesStorage.deleteLike(filmId, userId);
        event.remove(userId, filmId);
    }

    public List<Long> getAllFilmLikes(Long filmId) {
        validateParameter(filmId);
        log.info("Получаем все лайки фильма от пользователей");
        return likesStorage.getAllFilmLikes(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получаем самые залайканые фильмы количеством: {}", count);
        return likesStorage.getPopularFilms(count);
    }

    public List<Film> getPopularWithYearForYear(int limit, Long genreId, Integer year) {

        if ((limit <= 0) || (year != null && year <= 0)) {
            throw new IncorrectParameterException("Некорректные параметры запроса");
        }
        if (genreId != null && genreStorage.get(genreId) == null) {
            throw new DataNotFoundException("Жанра с таким айди нет" + genreId);
        }

        log.info("Получение списка размером = {}," +
                " самых популярных фильмов указанного жанра с айди = {} за нужный год = {}.", limit, genreId, year);
        return filmStorage.getPopularWithYearForYear(limit, genreId, year);
    }

    public List<Film> getPopularWithGenre(int limit, Long genreId) {

        if (limit <= 0) {
            throw new IncorrectParameterException("Некорректные параметры запроса");
        }
        if (genreId != null && genreStorage.get(genreId) == null) {
            throw new DataNotFoundException("Жанра с таким айди нет" + genreId);
        }

        log.info("Получаем список" +
                " самых популярных фильмов указанного жанра с айди = {}", genreId);
        return filmStorage.getPopularWithGenre(limit, genreId);
    }

    public List<Film> getPopularWithYear(int limit, Integer year) {

        if ((limit <= 0) || (year != null && year <= 0)) {
            throw new IncorrectParameterException("Некорректные параметры запроса");
        }

        log.info("Получение списка" +
                " самых популярных фильмов за нужный год = {}.", year);
        return filmStorage.getPopularWithYear(limit, year);
    }

    public List<Film> searchFilmsByQuery(String query, String by) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }
        String searchQuery = "%" + query.toLowerCase() + "%";
        List<Film> films = filmStorage.searchFilmsByQuery(searchQuery, by);
        Collections.sort(films, (film1, film2) -> Long.compare(film2.getLikes(), film1.getLikes()));
        return films;
    }
  
    public void deleteFilmById(long filmId) {
        abstractStorage.delete(filmId);
        log.info("Удален фильм по ID: " + filmId);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("Получаем общие фильмы пользователей" + userId + " и " + friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
