package ru.java.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Film extends BaseUnit {

    @NotNull(message = "Не может быть null")
    @NotBlank(message = "Не может быть пустым")
    private String name;
    @Size(max = 200, message = "Не более 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(value = 1, message = "Не может быть <= 0")
    private int duration;
    HashSet<Long> likes = new HashSet<>(); //для лайков

    public int getLikesCount() {
        return likes.size();
    }
}