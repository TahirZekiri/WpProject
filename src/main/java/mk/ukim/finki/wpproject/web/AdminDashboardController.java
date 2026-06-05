package mk.ukim.finki.wpproject.web;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wpproject.model.dto.ActivityTimelineDto;
import mk.ukim.finki.wpproject.model.dto.SystemStatisticsDto;
import mk.ukim.finki.wpproject.model.dto.TypeDistributionDto;
import mk.ukim.finki.wpproject.model.dto.UserActivityDto;
import mk.ukim.finki.wpproject.service.StatisticsService;
import mk.ukim.finki.wpproject.service.UserActivityExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class AdminDashboardController {
    private final StatisticsService statisticsService;
    private final UserActivityExportService userActivityExportService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        SystemStatisticsDto statistics = statisticsService.getSystemStatistics();
        List<UserActivityDto> userActivity = statisticsService.getUserActivityStats();
        List<ActivityTimelineDto> timeline = statisticsService.getActivityTimeline();

        model.addAttribute("statistics", statistics);
        model.addAttribute("userActivity", userActivity);
        model.addAttribute("timeline", timeline);
        model.addAttribute("typeDistributionChart", toChartData(statistics.typeDistribution()));
        model.addAttribute("toneDistributionChart", toChartData(statistics.toneDistribution()));
        model.addAttribute("timelineChart", toTimelineChartData(timeline));

        return "admin-dashboard";
    }

    private List<Map<String, Object>> toChartData(List<TypeDistributionDto> distribution) {
        return distribution.stream()
                .map(item -> Map.<String, Object>of(
                        "name", item.name(),
                        "count", item.count()
                ))
                .toList();
    }

    private List<Map<String, Object>> toTimelineChartData(List<ActivityTimelineDto> timeline) {
        return timeline.stream()
                .map(item -> Map.<String, Object>of(
                        "date", item.date().toString(),
                        "count", item.count()
                ))
                .toList();
    }

    @GetMapping("/api/statistics")
    @org.springframework.web.bind.annotation.ResponseBody
    public SystemStatisticsDto getStatistics() {
        return statisticsService.getSystemStatistics();
    }

    @GetMapping("/api/users/activity")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<UserActivityDto> getUserActivity() {
        return statisticsService.getUserActivityStats();
    }

    @GetMapping("/api/timeline")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<ActivityTimelineDto> getTimeline() {
        return statisticsService.getActivityTimeline();
    }

    @GetMapping("/export/users/csv")
    public ResponseEntity<byte[]> exportUserActivityCSV() throws IOException {
        List<UserActivityDto> userActivity = statisticsService.getUserActivityStats();
        StringWriter writer = new StringWriter();
        userActivityExportService.exportToCSV(userActivity, writer);

        byte[] content = writer.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        ContentDisposition.attachment()
                                .filename("user-activity-report.csv")
                                .build()
                                .toString())
                .body(content);
    }

    @GetMapping("/export/users/pdf")
    public ResponseEntity<byte[]> exportUserActivityPDF() throws IOException {
        List<UserActivityDto> userActivity = statisticsService.getUserActivityStats();
        byte[] content = userActivityExportService.exportToPDF(userActivity);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        ContentDisposition.attachment()
                                .filename("user-activity-report.pdf")
                                .build()
                                .toString())
                .body(content);
    }
}
