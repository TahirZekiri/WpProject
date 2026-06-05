package mk.ukim.finki.wpproject.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserActivityDto(
        String username,
        String fullName,
        Long entriesCreated,
        LocalDateTime lastActivityDate,
        Integer labelsUsed,
        Integer entitiesUsed,
        List<TypeDistributionDto> typeBreakdown,
        List<TypeDistributionDto> toneBreakdown
) {
}
