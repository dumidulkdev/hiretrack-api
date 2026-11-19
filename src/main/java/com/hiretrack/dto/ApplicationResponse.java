package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;

import java.time.Instant;
import java.time.LocalDate;

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