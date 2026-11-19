package com.hiretrack.mapper;

import com.hiretrack.domain.JobApplication;
import com.hiretrack.dto.ApplicationRequest;
import com.hiretrack.dto.ApplicationResponse;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    public JobApplication toEntity(ApplicationRequest request) {
        JobApplication entity = new JobApplication();
        applyRequest(entity, request);
        return entity;
    }

    public void applyRequest(JobApplication entity, ApplicationRequest request) {
        entity.setCompany(request.company());
        entity.setRole(request.role());
        entity.setJobUrl(request.jobUrl());
        entity.setSource(request.source());
        entity.setSalaryMin(request.salaryMin());
        entity.setSalaryMax(request.salaryMax());
        entity.setLocation(request.location());
        entity.setRemote(request.remote());
        entity.setNotes(request.notes());
        entity.setAppliedDate(request.appliedDate());
    }

    public ApplicationResponse toResponse(JobApplication entity) {
        return new ApplicationResponse(
                entity.getId(),
                entity.getCompany(),
                entity.getRole(),
                entity.getJobUrl(),
                entity.getSource(),
                entity.getSalaryMin(),
                entity.getSalaryMax(),
                entity.getLocation(),
                entity.isRemote(),
                entity.getNotes(),
                entity.getAppliedDate(),
                entity.getStatus(),
                entity.getLastActivityAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}