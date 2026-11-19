package com.hiretrack.service;

import com.hiretrack.domain.JobApplication;
import com.hiretrack.dto.ApplicationFilter;
import com.hiretrack.dto.ApplicationRequest;
import com.hiretrack.dto.ApplicationResponse;
import com.hiretrack.dto.PageResponse;
import com.hiretrack.exception.ResourceNotFoundException;
import com.hiretrack.mapper.ApplicationMapper;
import com.hiretrack.repository.JobApplicationRepository;
import com.hiretrack.repository.spec.ApplicationSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class ApplicationService {

    private final JobApplicationRepository repository;
    private final ApplicationMapper mapper;
    private final Clock clock;
    private final StatusService statusService;

    public ApplicationService(
            JobApplicationRepository repository,
            ApplicationMapper mapper,
            Clock clock,
            @Lazy StatusService statusService) {
        this.repository = repository;
        this.mapper = mapper;
        this.clock = clock;
        this.statusService = statusService;
    }

    @Transactional
    public ApplicationResponse create(ApplicationRequest request) {
        JobApplication entity = mapper.toEntity(request);
        Instant now = clock.instant();
        entity.touchActivity(now);
        entity.setLastActivityAt(now);
        JobApplication saved = repository.save(entity);
        statusService.recordInitialStatus(saved);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getById(Long id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> search(ApplicationFilter filter, Pageable pageable) {
        Specification<JobApplication> spec = ApplicationSpecifications.fromFilter(filter);
        Page<JobApplication> page = repository.findAll(spec, pageable);
        return new PageResponse<>(
                page.map(mapper::toResponse).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public java.util.stream.Stream<JobApplication> streamForExport(ApplicationFilter filter) {
        Specification<JobApplication> spec = ApplicationSpecifications.fromFilter(filter);
        return repository.findAll(spec, Pageable.unpaged()).stream();
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