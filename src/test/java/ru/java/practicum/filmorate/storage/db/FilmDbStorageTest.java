package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testCreateFilm() {
        Film newFilm = new Film(
                "testFilm",
                "description",
                LocalDate.of(1999,2,2),
                150,
                1,
                new Mpa(),
                10L);

        newFilm.getMpa().setId(1);

        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Film createdFilm = filmStorage.create(newFilm);

        // Проверяем, что фильм успешно создан
        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo(newFilm.getName());

    }

    @Test
    void testUpdateFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                10L);


        newFilm.getMpa().setId(2);

        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Film createdFilm = filmStorage.create(newFilm);

        // Меняем данные фильма
        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        createdFilm.setReleaseDate(LocalDate.now().plusDays(1));
        createdFilm.setDuration(160);
        createdFilm.setRating(4);

        //Устанавливаем MPA
        newFilm.getMpa().setId(4);

        // Обновляем фильм в базе данных
        Film updatedFilm = filmStorage.update(createdFilm);

        // Проверяем, что фильм успешно обновлен
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo(createdFilm.getName());
    }

    @Test
    void testGetFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm3",
                "description3",
                LocalDate.of(1999,2,23),
                90,
                1,
                new Mpa(),
                10L);
        newFilm.getMpa().setId(3);

        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Film createdFilm = filmStorage.create(newFilm);

        // Получаем фильм по его ID
        Film retrievedFilm = filmStorage.get(createdFilm.getId());

        // Проверяем, что получен правильный фильм
        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(retrievedFilm.getName()).isEqualTo(createdFilm.getName());
    }

    @Test
    void testDeleteFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm4",
                "description4",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm.getMpa().setId(5);

        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Film createdFilm = filmStorage.create(newFilm);

        // Удаляем фильм из базы данных
        filmStorage.delete(createdFilm.getId());

        // Пытаемся получить удаленный фильм и ожидаем исключение
        assertThrows(DataNotFoundException.class, () -> filmStorage.get(createdFilm.getId()));
    }

    @Test
    void testGetRecommendationFilmsWithoutCross() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        LikesDbStorage likeStorage = new LikesDbStorage(jdbcTemplate);

        // Подготавливаем данные для теста
        Film newFilm1 = new Film(
                "testFilmr2",
                "description2",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm1.getMpa().setId(5);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm2.getMpa().setId(5);

        Film newFilm3 = new Film(
                "testFilm3",
                "description3",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm3.getMpa().setId(5);

        User newUser1 = new User(
                "test1@email.ru",
                "test1",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User newUser2 = new User(
                "test2@email.ru",
                "test2",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        // Записываем фильмы в БД
        filmStorage.create(newFilm1);
        filmStorage.create(newFilm2);
        filmStorage.create(newFilm3);

        // Записываем пользователей в БД
        userStorage.create(newUser1);
        userStorage.create(newUser2);

        // Ставим лайки НЕ пересекающиеся фильмам
        likeStorage.addLike(newFilm1.getId(), newUser1.getId());
        likeStorage.addLike(newFilm2.getId(), newUser2.getId());
        likeStorage.addLike(newFilm3.getId(), newUser2.getId());

        // Получаем пустой список рекомендаций
        List<Film> recFilms = filmStorage.getRecommendationsFilms(newUser1.getId());

        // Проверяем, что список пустой
        assertThat(recFilms).isEmpty();
    }

    @Test
    void testGetRecommendationFilmsWithCross() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        LikesDbStorage likeStorage = new LikesDbStorage(jdbcTemplate);

        // Подготавливаем данные для теста
        Film newFilm1 = new Film(
                "testFilmr2",
                "description2",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm1.getMpa().setId(5);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm2.getMpa().setId(5);

        Film newFilm3 = new Film(
                "testFilm3",
                "description3",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm3.getMpa().setId(5);

        User newUser1 = new User(
                "test1@email.ru",
                "test1",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User newUser2 = new User(
                "test2@email.ru",
                "test2",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        // Записываем фильмы в БД
        filmStorage.create(newFilm1);
        filmStorage.create(newFilm2);
        filmStorage.create(newFilm3);

        // Записываем пользователей в БД
        userStorage.create(newUser1);
        userStorage.create(newUser2);

        // Ставим лайки
        // Пересечение по фильму 3
        likeStorage.addLike(newFilm1.getId(), newUser1.getId());
        likeStorage.addLike(newFilm3.getId(), newUser1.getId());
        likeStorage.addLike(newFilm2.getId(), newUser2.getId());
        likeStorage.addLike(newFilm3.getId(), newUser2.getId());

        // Получаем список рекомендаций
        List<Film> recFilms = filmStorage.getRecommendationsFilms(newUser1.getId());

        // Проверяем, что рекомендован фильм 2 от пользователя 2
        assertThat(recFilms.get(0).getName()).isEqualTo(newFilm2.getName());
    }
}
