package mk.ukim.finki.wpproject.model.dto;

import java.util.List;

public record SystemStatisticsDto(
        Long totalUsers,
        Long totalEntries,
        Long totalLabels,
        Long totalEntities,
        List<TypeDistributionDto> typeDistribution,
        List<TypeDistributionDto> toneDistribution,
        List<TopItemDto> topLabels,
        List<TopItemDto> topEntities
) {
}
