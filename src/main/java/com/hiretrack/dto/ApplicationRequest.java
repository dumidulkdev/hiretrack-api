package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.validation.SalaryRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@SalaryRange
@Schema(example = """
        {
          "company": "Northwind Systems",
          "role": "Java Developer",
          "jobUrl": "https://northwind.example/jobs/java-developer",
          "source": "LINKEDIN",
          "salaryMin": 85000,
          "salaryMax": 110000,
          "location": "Colombo",
          "remote": true,
          "notes": "Referred by a former teammate.",
          "appliedDate": "2026-02-12"
        }
        """)
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