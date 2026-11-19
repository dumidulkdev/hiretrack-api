package com.hiretrack.service;

import com.hiretrack.domain.JobApplication;
import com.hiretrack.dto.ApplicationRequest;
import com.hiretrack.dto.ApplicationResponse;
import com.hiretrack.exception.ResourceNotFoundException;
import com.hiretrack.mapper.ApplicationMapper;
import com.hiretrack.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class ApplicationService {

    private final JobApplicationRepository repository;
    private final ApplicationMapper mapper;
    private final Clock clock;

    public ApplicationService(JobApplicationRepository repository, ApplicationMapper mapper, Clock clock) {
        this.repository = repository;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Transactional
    public ApplicationResponse create(ApplicationRequest request) {
        JobApplication entity = mapper.toEntity(request);
        Instant now = clock.instant();
        entity.touchActivity(now);
        entity.setLastActivityAt(now);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getById(Long id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public ApplicationResponse update(Long id, ApplicationRequest request) {
        JobApplication entity = findOrThrow(id);
        mapper.applyRequest(entity, request);
        entity.touchActivity(clock.instant());
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found: " + id);
        }
        repository.deleteById(id);
    }

    public JobApplication findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }
}