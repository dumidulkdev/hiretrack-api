package com.hiretrack.service;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.dto.StatsResponse;
import com.hiretrack.repository.StatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private StatsRepository statsRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    void computesRatesFromAggregates() {
        Map<ApplicationStatus, Long> totals = new EnumMap<>(ApplicationStatus.class);
        totals.put(ApplicationStatus.APPLIED, 4L);
        totals.put(ApplicationStatus.INTERVIEW, 1L);
        when(statsRepository.totalsByStatus()).thenReturn(totals);
        when(statsRepository.totalApplications()).thenReturn(5L);
        when(statsRepository.countResponded()).thenReturn(1L);
        when(statsRepository.countInterviewStage()).thenReturn(1L);
        when(statsRepository.averageDaysToFirstResponse()).thenReturn(6.5);
        when(statsRepository.topSources(5)).thenReturn(List.of(
                new StatsResponse.SourceCount(ApplicationSource.LINKEDIN, 3)));

        StatsResponse stats = statsService.getStats();

        assertThat(stats.responseRate()).isEqualTo(0.2);
        assertThat(stats.interviewRate()).isEqualTo(0.2);
        assertThat(stats.averageDaysToFirstResponse()).isEqualTo(6.5);
        assertThat(stats.topSources()).hasSize(1);
    }

    @Test
    void handlesEmptyDataset() {
        Map<ApplicationStatus, Long> totals = new EnumMap<>(ApplicationStatus.class);
        when(statsRepository.totalsByStatus()).thenReturn(totals);
        when(statsRepository.totalApplications()).thenReturn(0L);
        when(statsRepository.topSources(5)).thenReturn(List.of());

        StatsResponse stats = statsService.getStats();

        assertThat(stats.responseRate()).isZero();
        assertThat(stats.interviewRate()).isZero();
    }
}