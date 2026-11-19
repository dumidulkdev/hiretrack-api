package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@Schema(example = """
        {
          "id": 1,
          "company": "Northwind Systems",
          "role": "Java Developer",
          "jobUrl": "https://northwind.example/jobs/java-developer",
          "source": "LINKEDIN",
          "salaryMin": 85000,
          "salaryMax": 110000,
          "location": "Colombo",
          "remote": true,
          "notes": "Referred by a former teammate.",
          "appliedDate": "2026-02-12",
          "status": "APPLIED",
          "lastActivityAt": "2026-02-12T08:30:00Z",
          "createdAt": "2026-02-12T08:30:00Z",
          "updatedAt": "2026-02-12T08:30:00Z"
        }
        """)
public record ApplicationResponse(
        Long id,
        String company,
        String role,
        String jobUrl,
        ApplicationSource source,
        Integer salaryMin,
        Integer salaryMax,
        String location,
        boolean remote,
        String notes,
        LocalDate appliedDate,
        ApplicationStatus status,
        Instant lastActivityAt,
        Instant createdAt,
        Instant updatedAt
) {
}