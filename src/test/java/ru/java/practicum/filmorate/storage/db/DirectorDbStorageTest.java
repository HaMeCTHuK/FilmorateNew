package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.DirectorStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorDbStorageTest {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getSortedDirectorListByYear() {

        LikesDbStorage likeStorage = new LikesDbStorage(jdbcTemplate);
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, directorDbStorage, likeStorage);

        //Создаем режиссера и добавляем в бд
        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorDbStorage.create(director);

        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm",
                "description",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm.getMpa().setId(2);
        newFilm.getDirectors().add(createdDirector);
        Film createdFilm = filmStorage.create(newFilm);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(2000,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm2.getMpa().setId(3);
        newFilm2.getDirectors().add(createdDirector);
        Film createdFilm2 = filmStorage.create(newFilm2);

        List<Film> sortedListByYear = directorDbStorage.getSortedDirectorListByYear(createdDirector.getId());
        //Проверяем что список не пуст
        assertThat(sortedListByYear).isNotNull();
        //Проверяем сортировку по году
        assertEquals(sortedListByYear.get(0).getReleaseDate(), LocalDate.of(1999,2,22));
    }

    @Test
    void getSortedDirectorListByLikes() {

        LikesDbStorage likeStorage = new LikesDbStorage(jdbcTemplate);
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, directorDbStorage, likeStorage);

        //Создаем режиссера и добавляем в бд
        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorDbStorage.create(director);

        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm",
                "description",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm.getMpa().setId(2);
        newFilm.getDirectors().add(createdDirector);
        Film createdFilm = filmStorage.create(newFilm);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(2000,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm2.getMpa().setId(3);
        newFilm2.getDirectors().add(createdDirector);
        Film createdFilm2 = filmStorage.create(newFilm2);

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User createdUser = userStorage.create(newUser);

        //добавляем лайки
        likeStorage.addLike(createdFilm.getId(),createdUser.getId());

        List<Film> sortedListByLikes = directorDbStorage.getSortedDirectorListByLikes(createdDirector.getId());
        //Проверяем что список не пуст
        assertThat(sortedListByLikes).isNotNull();
        //Проверяем сортировку по году
        assertEquals(sortedListByLikes.get(0).getLikes(), 1L);
    }

    @Test
    void createDirector() {

        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);

        List<Director> directors = new ArrayList<>();
        Director director = Director.builder().id(1).name("Boss").build();
        directors.add(director);

        // Записываем режиссера в базу данных
        Director createdDirector = directorStorage.create(director);

        // Проверяем, что режиссер успешно создан
        assertThat(createdDirector).isNotNull();
        assertThat(directorStorage.get(createdDirector.getId())).isNotNull();
        assertThat(createdDirector.getName()).isEqualTo(director.getName());
    }

    @Test
    void updateDirector() {
        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);

        List<Director> directors = new ArrayList<>();
        Director director = Director.builder().id(1).name("Boss").build();
        directors.add(director);
        Director createdDirector = directorStorage.create(director);
        String updatedName = "Updated DirectorName";

        createdDirector.setName(updatedName);
        Director updatedDirector = directorStorage.update(createdDirector);

        assertEquals(createdDirector.getId(), updatedDirector.getId());
        assertEquals(updatedName, updatedDirector.getName());

    }

    @Test
    void getAllDirectors() {

        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);

        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorStorage.create(director);
        List<Director> directors = directorStorage.getAll();

        assertNotNull(directors);
        assertFalse(directors.isEmpty());
    }

    @Test
    void getDirector() {

        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);

        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorStorage.create(director);
        Director recivedDirectorById = directorStorage.get(createdDirector.getId());

        assertNotNull(recivedDirectorById);
        assertEquals(createdDirector.getId(), recivedDirectorById.getId());
    }

    @Test
    void deleteDirector() {

        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);

        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorStorage.create(director);

        assertNotNull(createdDirector);

        directorStorage.delete(createdDirector.getId());

        assertThrows(DataNotFoundException.class, () -> directorStorage.get(createdDirector.getId()));
    }
}