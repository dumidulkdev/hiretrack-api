package com.hiretrack.service;

import com.hiretrack.dto.ApplicationFilter;
import com.hiretrack.export.ApplicationCsvWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Service
public class ApplicationExportService {

    private final ApplicationService applicationService;
    private final ApplicationCsvWriter csvWriter;

    public ApplicationExportService(ApplicationService applicationService, ApplicationCsvWriter csvWriter) {
        this.applicationService = applicationService;
        this.csvWriter = csvWriter;
    }

    @Transactional(readOnly = true)
    public void exportCsv(ApplicationFilter filter, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"applications.csv\"");
        try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
            csvWriter.write(applicationService.streamForExport(filter), writer);
        }
    }
}