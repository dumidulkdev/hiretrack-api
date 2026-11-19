package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(example = "{\"status\":\"SCREENING\",\"note\":\"Recruiter scheduled a call\"}")
public record StatusUpdateRequest(
        @NotNull ApplicationStatus status,
        String note
) {
}