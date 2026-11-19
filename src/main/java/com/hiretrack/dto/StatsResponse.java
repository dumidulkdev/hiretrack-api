package com.hiretrack.dto;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;

import java.util.List;
import java.util.Map;

public record StatsResponse(
        Map<ApplicationStatus, Long> totalsByStatus,
        double responseRate,
        double interviewRate,
        Double averageDaysToFirstResponse,
        List<SourceCount> topSources
) {
    public record SourceCount(ApplicationSource source, long count) {
    }
}