package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationStatus;

import java.time.Instant;

public record StatusHistoryResponse(
        Long id,
        ApplicationStatus fromStatus,
        ApplicationStatus toStatus,
        Instant changedAt,
        String note
) {
}