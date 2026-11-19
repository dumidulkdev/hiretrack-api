package com.hiretrack.service;

import com.hiretrack.domain.FollowUp;
import com.hiretrack.domain.JobApplication;
import com.hiretrack.dto.FollowUpRequest;
import com.hiretrack.dto.FollowUpResponse;
import com.hiretrack.exception.ResourceNotFoundException;
import com.hiretrack.mapper.FollowUpMapper;
import com.hiretrack.repository.FollowUpRepository;
import com.hiretrack.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationService applicationService;
    private final FollowUpMapper mapper;
    private final Clock clock;

    public FollowUpService(
            FollowUpRepository followUpRepository,
            JobApplicationRepository jobApplicationRepository,
            ApplicationService applicationService,
            FollowUpMapper mapper,
            Clock clock) {
        this.followUpRepository = followUpRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.applicationService = applicationService;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Transactional
    public FollowUpResponse create(Long applicationId, FollowUpRequest request) {
        JobApplication application = applicationService.findOrThrow(applicationId);
        FollowUp followUp = new FollowUp();
        followUp.setApplication(application);
        followUp.setDueDate(request.dueDate());
        followUp.setNote(request.note());
        application.touchActivity(clock.instant());
        jobApplicationRepository.save(application);
        return mapper.toResponse(followUpRepository.save(followUp));
    }

    @Transactional
    public FollowUpResponse markDone(Long applicationId, Long followUpId) {
        FollowUp followUp = findForApplication(applicationId, followUpId);
        followUp.setDone(true);
        JobApplication application = followUp.getApplication();
        application.touchActivity(clock.instant());
        jobApplicationRepository.save(application);
        return mapper.toResponse(followUpRepository.save(followUp));
    }

    @Transactional(readOnly = true)
    public List<FollowUpResponse> dueTodayOrOverdue() {
        LocalDate today = LocalDate.now(clock);
        return followUpRepository.findDue(today).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public int flagOverdueFollowUps() {
        LocalDate today = LocalDate.now(clock);
        List<FollowUp> overdue = followUpRepository.findNewlyOverdue(today);
        overdue.forEach(f -> f.setOverdueFlagged(true));
        followUpRepository.saveAll(overdue);
        return overdue.size();
    }

    private FollowUp findForApplication(Long applicationId, Long followUpId) {
        FollowUp followUp = followUpRepository.findById(followUpId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found: " + followUpId));
        if (!followUp.getApplication().getId().equals(applicationId)) {
            throw new ResourceNotFoundException("Follow-up not found for application: " + applicationId);
        }
        return followUp;
    }
}