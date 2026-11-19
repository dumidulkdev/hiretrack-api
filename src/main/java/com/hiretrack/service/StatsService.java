package com.hiretrack.service;

import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.dto.StatsResponse;
import com.hiretrack.repository.StatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class StatsService {

    private static final int TOP_SOURCE_LIMIT = 5;

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        Map<ApplicationStatus, Long> totals = statsRepository.totalsByStatus();
        long total = statsRepository.totalApplications();
        double responseRate = total == 0 ? 0.0 : (double) statsRepository.countResponded() / total;
        double interviewRate = total == 0 ? 0.0 : (double) statsRepository.countInterviewStage() / total;
        return new StatsResponse(
                totals,
                responseRate,
                interviewRate,
                statsRepository.averageDaysToFirstResponse(),
                statsRepository.topSources(TOP_SOURCE_LIMIT)
        );
    }
}