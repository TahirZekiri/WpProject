package mk.ukim.finki.wpproject.service.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.User;
import mk.ukim.finki.wpproject.model.dto.*;
import mk.ukim.finki.wpproject.repository.CustomEntityRepository;
import mk.ukim.finki.wpproject.repository.CustomLabelRepository;
import mk.ukim.finki.wpproject.repository.TextEntryRepository;
import mk.ukim.finki.wpproject.repository.UserRepository;
import mk.ukim.finki.wpproject.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final UserRepository userRepository;
    private final TextEntryRepository textEntryRepository;
    private final CustomLabelRepository customLabelRepository;
    private final CustomEntityRepository customEntityRepository;

    @Override
    public SystemStatisticsDto getSystemStatistics() {
        // Get basic counts
        Long totalUsers = userRepository.count();
        Long totalEntries = textEntryRepository.count();
        Long totalLabels = customLabelRepository.count();
        Long totalEntities = customEntityRepository.count();

        // Get type distribution
        List<TypeDistributionDto> typeDistribution = textEntryRepository.countByTextType()
                .stream()
                .map(row -> new TypeDistributionDto((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());

        // Get tone distribution
        List<TypeDistributionDto> toneDistribution = textEntryRepository.countByTextTone()
                .stream()
                .map(row -> new TypeDistributionDto((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());

        // Get top labels
        List<TopItemDto> topLabels = customLabelRepository.findTopLabels()
                .stream()
                .map(row -> new TopItemDto((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());

        // Get top entities
        List<TopItemDto> topEntities = customEntityRepository.findTopEntities()
                .stream()
                .map(row -> new TopItemDto((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());

        return new SystemStatisticsDto(
                totalUsers,
                totalEntries,
                totalLabels,
                totalEntities,
                typeDistribution,
                toneDistribution,
                topLabels,
                topEntities
        );
    }

    @Override
    public List<UserActivityDto> getUserActivityStats() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream().map(user -> {
            Long entriesCreated = textEntryRepository.count(
                    (root, query, cb) -> cb.equal(root.get("user").get("username"), user.getUsername())
            );

            LocalDateTime lastActivity = null;
            Object lastActivityObj = textEntryRepository.getLastActivityDateByUsername(user.getUsername());
            if (lastActivityObj != null) {
                lastActivity = (LocalDateTime) lastActivityObj;
            }

            Integer labelsUsed = textEntryRepository.countDistinctLabelsByUsername(user.getUsername());
            if (labelsUsed == null) labelsUsed = 0;

            Integer entitiesUsed = textEntryRepository.countDistinctEntitiesByUsername(user.getUsername());
            if (entitiesUsed == null) entitiesUsed = 0;

            // Get type breakdown for user
            List<TypeDistributionDto> typeBreakdown = textEntryRepository.countByTextTypeForUser(user.getUsername())
                    .stream()
                    .map(row -> new TypeDistributionDto((String) row[0], ((Number) row[1]).longValue()))
                    .collect(Collectors.toList());

            // Get tone breakdown for user
            List<TypeDistributionDto> toneBreakdown = textEntryRepository.countByTextToneForUser(user.getUsername())
                    .stream()
                    .map(row -> new TypeDistributionDto((String) row[0], ((Number) row[1]).longValue()))
                    .collect(Collectors.toList());

            String fullName = (user.getName() != null ? user.getName() : "") + " " +
                    (user.getSurname() != null ? user.getSurname() : "");

            return new UserActivityDto(
                    user.getUsername(),
                    fullName.trim(),
                    entriesCreated,
                    lastActivity,
                    labelsUsed,
                    entitiesUsed,
                    typeBreakdown,
                    toneBreakdown
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<ActivityTimelineDto> getActivityTimeline() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        return textEntryRepository.findByCreatedAtGreaterThanEqual(cutoff)
                .stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getCreatedAt().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> new ActivityTimelineDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
