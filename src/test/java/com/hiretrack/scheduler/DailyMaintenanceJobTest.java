package com.hiretrack.scheduler;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.domain.JobApplication;
import com.hiretrack.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
class DailyMaintenanceJobTest {

    @Autowired
    private DailyMaintenanceJob job;

    @Autowired
    private JobApplicationRepository repository;

    @MockitoBean
    private Clock clock;

    @Test
    void marksStaleAppliedApplicationsAsGhosted() {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        when(clock.instant()).thenReturn(now);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        JobApplication application = new JobApplication();
        application.setCompany("Stale Co");
        application.setRole("Engineer");
        application.setSource(ApplicationSource.LINKEDIN);
        application.setRemote(false);
        application.setAppliedDate(LocalDate.of(2026, 4, 1));
        application.setStatus(ApplicationStatus.APPLIED);
        application.setLastActivityAt(Instant.parse("2026-04-01T10:00:00Z"));
        Long id = repository.save(application).getId();

        int ghosted = job.markGhostedApplications();

        assertThat(ghosted).isGreaterThanOrEqualTo(1);
        Optional<JobApplication> updated = repository.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(ApplicationStatus.GHOSTED);
    }
}