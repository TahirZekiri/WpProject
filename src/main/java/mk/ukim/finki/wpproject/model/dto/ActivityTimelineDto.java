package mk.ukim.finki.wpproject.model.dto;

import java.time.LocalDate;

public record ActivityTimelineDto(
        LocalDate date,
        Long count
) {
}
