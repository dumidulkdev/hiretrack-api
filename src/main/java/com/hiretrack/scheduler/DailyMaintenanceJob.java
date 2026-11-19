package com.hiretrack.scheduler;

import com.hiretrack.config.HireTrackProperties;
import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.domain.JobApplication;
import com.hiretrack.repository.JobApplicationRepository;
import com.hiretrack.service.FollowUpService;
import com.hiretrack.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Component
public class DailyMaintenanceJob {

    private static final Logger log = LoggerFactory.getLogger(DailyMaintenanceJob.class);

    private final FollowUpService followUpService;
    private final StatusService statusService;
    private final JobApplicationRepository applicationRepository;
    private final HireTrackProperties properties;
    private final Clock clock;

    public DailyMaintenanceJob(
            FollowUpService followUpService,
            StatusService statusService,
            JobApplicationRepository applicationRepository,
            HireTrackProperties properties,
            Clock clock) {
        this.followUpService = followUpService;
        this.statusService = statusService;
        this.applicationRepository = applicationRepository;
        this.properties = properties;
        this.clock = clock;
    }

    @Scheduled(cron = "${hiretrack.scheduler.daily-cron}")
    @Transactional
    public void runDailyMaintenance() {
        int flagged = followUpService.flagOverdueFollowUps();
        int ghosted = markGhostedApplications();
        log.info("Daily maintenance complete: flaggedFollowUps={}, ghostedApplications={}", flagged, ghosted);
    }

    @Transactional
    public int markGhostedApplications() {
        Instant cutoff = clock.instant().minusSeconds(properties.getGhostingThresholdDays() * 86_400L);
        List<JobApplication> stale = applicationRepository.findStaleApplications(
                List.of(ApplicationStatus.APPLIED, ApplicationStatus.SCREENING), cutoff);
        int count = 0;
        for (JobApplication application : stale) {
            statusService.markGhosted(application, "No response within ghosting threshold");
            count++;
        }
        return count;
    }
}