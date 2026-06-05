package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.dto.ActivityTimelineDto;
import mk.ukim.finki.wpproject.model.dto.SystemStatisticsDto;
import mk.ukim.finki.wpproject.model.dto.UserActivityDto;

import java.util.List;

public interface StatisticsService {
    SystemStatisticsDto getSystemStatistics();

    List<UserActivityDto> getUserActivityStats();

    List<ActivityTimelineDto> getActivityTimeline();
}
