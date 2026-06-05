package mk.ukim.finki.wpproject.service;

import mk.ukim.finki.wpproject.model.dto.UserActivityDto;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface UserActivityExportService {
    void exportToCSV(List<UserActivityDto> userActivities, Writer writer) throws IOException;

    byte[] exportToPDF(List<UserActivityDto> userActivities) throws IOException;
}
