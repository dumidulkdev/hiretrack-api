package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull ApplicationStatus status,
        String note
) {
}