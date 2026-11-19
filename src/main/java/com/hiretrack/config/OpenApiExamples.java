package com.hiretrack.config;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.dto.ApplicationRequest;
import com.hiretrack.dto.ApplicationResponse;
import com.hiretrack.dto.FollowUpRequest;
import com.hiretrack.dto.StatusUpdateRequest;

import java.time.Instant;
import java.time.LocalDate;

public final class OpenApiExamples {

    private OpenApiExamples() {
    }

    public static ApplicationRequest sampleCreateRequest() {
        return new ApplicationRequest(
                "Northwind Systems",
                "Java Developer",
                "https://northwind.example/jobs/java-developer",
                ApplicationSource.LINKEDIN,
                85000,
                110000,
                "Colombo",
                true,
                "Referred by a former teammate.",
                LocalDate.of(2026, 2, 12)
        );
    }

    public static ApplicationResponse sampleApplicationResponse() {
        return new ApplicationResponse(
                1L,
                "Northwind Systems",
                "Java Developer",
                "https://northwind.example/jobs/java-developer",
                ApplicationSource.LINKEDIN,
                85000,
                110000,
                "Colombo",
                true,
                "Applied notes",
                LocalDate.of(2026, 2, 12),
                ApplicationStatus.APPLIED,
                Instant.parse("2026-02-12T08:30:00Z"),
                Instant.parse("2026-02-12T08:30:00Z"),
                Instant.parse("2026-02-12T08:30:00Z")
        );
    }

    public static StatusUpdateRequest sampleStatusRequest() {
        return new StatusUpdateRequest(ApplicationStatus.SCREENING, "Recruiter scheduled a call");
    }

    public static FollowUpRequest sampleFollowUpRequest() {
        return new FollowUpRequest(LocalDate.of(2026, 2, 20), "Send a polite check-in email");
    }
}