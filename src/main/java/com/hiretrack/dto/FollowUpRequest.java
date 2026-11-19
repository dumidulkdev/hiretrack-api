package com.hiretrack.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FollowUpRequest(
        @NotNull LocalDate dueDate,
        String note
) {
}