package com.hiretrack.repository;

import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.domain.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {

    @Query("""
            select a from JobApplication a
            where a.status in :statuses and a.lastActivityAt < :cutoff
            """)
    List<JobApplication> findStaleApplications(
            @Param("statuses") List<ApplicationStatus> statuses,
            @Param("cutoff") Instant cutoff);
}