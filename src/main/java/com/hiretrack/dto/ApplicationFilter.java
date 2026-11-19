package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;

import java.time.LocalDate;

public record ApplicationFilter(
        ApplicationStatus status,
        String company,
        ApplicationSource source,
        LocalDate from,
        LocalDate to,
        String q
) {
}