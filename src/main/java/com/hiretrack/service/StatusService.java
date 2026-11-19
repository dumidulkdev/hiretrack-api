package com.hiretrack.service;

import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.domain.JobApplication;
import com.hiretrack.domain.StatusHistory;
import com.hiretrack.dto.ApplicationResponse;
import com.hiretrack.dto.StatusHistoryResponse;
import com.hiretrack.dto.StatusUpdateRequest;
import com.hiretrack.mapper.StatusHistoryMapper;
import com.hiretrack.mapper.ApplicationMapper;
import com.hiretrack.policy.StatusTransitionPolicy;
import com.hiretrack.repository.JobApplicationRepository;
import com.hiretrack.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class StatusService {

    private final ApplicationService applicationService;
    private final JobApplicationRepository applicationRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final StatusTransitionPolicy transitionPolicy;
    private final ApplicationMapper mapper;
    private final StatusHistoryMapper statusHistoryMapper;
    private final Clock clock;

    public StatusService(
            ApplicationService applicationService,
            JobApplicationRepository applicationRepository,
            StatusHistoryRepository statusHistoryRepository,
            StatusTransitionPolicy transitionPolicy,
            ApplicationMapper mapper,
            StatusHistoryMapper statusHistoryMapper,
            Clock clock) {
        this.applicationService = applicationService;
        this.applicationRepository = applicationRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.transitionPolicy = transitionPolicy;
        this.mapper = mapper;
        this.statusHistoryMapper = statusHistoryMapper;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public java.util.List<StatusHistoryResponse> timeline(Long applicationId) {
        applicationService.findOrThrow(applicationId);
        return statusHistoryRepository.findByApplicationIdOrderByChangedAtAsc(applicationId).stream()
                .map(statusHistoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, StatusUpdateRequest request) {
        JobApplication application = applicationService.findOrThrow(id);
        ApplicationStatus current = application.getStatus();
        transitionPolicy.assertTransition(current, request.status());
        return applyStatusChange(application, current, request.status(), request.note());
    }

    @Transactional
    public ApplicationResponse markGhosted(JobApplication application, String note) {
        ApplicationStatus current = application.getStatus();
        if (current != ApplicationStatus.APPLIED && current != ApplicationStatus.SCREENING) {
            return mapper.toResponse(application);
        }
        if (!transitionPolicy.canTransition(current, ApplicationStatus.GHOSTED)) {
            return mapper.toResponse(application);
        }
        return applyStatusChange(application, current, ApplicationStatus.GHOSTED, note);
    }

    private ApplicationResponse applyStatusChange(
            JobApplication application,
            ApplicationStatus from,
            ApplicationStatus to,
            String note) {
        Instant now = clock.instant();
        application.setStatus(to);
        application.touchActivity(now);

        StatusHistory history = new StatusHistory();
        history.setApplication(application);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setChangedAt(now);
        history.setNote(note);
        statusHistoryRepository.save(history);

        return mapper.toResponse(applicationRepository.save(application));
    }

    @Transactional
    public void recordInitialStatus(JobApplication application) {
        Instant now = clock.instant();
        StatusHistory history = new StatusHistory();
        history.setApplication(application);
        history.setFromStatus(null);
        history.setToStatus(ApplicationStatus.APPLIED);
        history.setChangedAt(now);
        history.setNote("Application created");
        statusHistoryRepository.save(history);
    }
}