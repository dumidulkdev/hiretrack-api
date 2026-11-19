package com.hiretrack.dto;

import java.time.Instant;
import java.time.LocalDate;

public record FollowUpResponse(
        Long id,
        Long applicationId,
        LocalDate dueDate,
        String note,
        boolean done,
        boolean overdueFlagged,
        Instant createdAt,
        Instant updatedAt
) {
}