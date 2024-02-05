package ru.java.practicum.filmorate.service; // add-reviews - file 4

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.model.Review;
import ru.java.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService extends AbstractService<Review> {

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.abstractStorage = reviewStorage;
    }

    @Override
    public void validate(Review review) {
        if (review.getContent().length() > 200) {
            throw new IllegalArgumentException("Максимальная длина отзыва — 200 символов");
        }
        if (review.getContent().isEmpty()) {
            throw new IllegalArgumentException("Отзыв не может быть пустым");
        }
        // Другие возможные проверки валидации
    }

    @Override
    public void validateParameter(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректные параметры поля, проверь null или отрицательное значение");
        }
        if (getData(id) == null) {
            throw new IllegalArgumentException("Отзыва с указанным ID не существует");
        }
    }

    @Override
    public void validateParameters(Long id, Long otherId) {
        // Ваша логика валидации параметров, если необходимо
    }

    public List<Review> getReviewsOfFilm(long filmId, int count) {
        // Добавьте вашу логику получения отзывов по фильму
        return ((ReviewStorage) abstractStorage).getReviewsOfFilm(filmId, count);
    }

    public void addLike(long reviewId, long userId) {
        // Добавьте вашу логику для добавления лайка
        ((ReviewStorage) abstractStorage).addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        // Добавьте вашу логику для добавления дизлайка
        ((ReviewStorage) abstractStorage).addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        // Добавьте вашу логику для удаления лайка
        ((ReviewStorage) abstractStorage).deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        // Добавьте вашу логику для удаления дизлайка
        ((ReviewStorage) abstractStorage).deleteDislike(reviewId, userId);
    }
}
