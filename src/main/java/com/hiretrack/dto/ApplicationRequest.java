package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.validation.SalaryRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@SalaryRange
public record ApplicationRequest(
        @NotBlank String company,
        @NotBlank String role,
        String jobUrl,
        @NotNull ApplicationSource source,
        Integer salaryMin,
        Integer salaryMax,
        String location,
        boolean remote,
        String notes,
        @NotNull LocalDate appliedDate
) {
}