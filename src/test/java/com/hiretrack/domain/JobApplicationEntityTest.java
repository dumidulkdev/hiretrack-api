package com.hiretrack.domain;

import com.hiretrack.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import com.hiretrack.config.JpaAuditingConfig;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("local")
@Import(JpaAuditingConfig.class)
@Sql(scripts = "/sql/clean-applications.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JobApplicationEntityTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Test
    void persistsApplicationWithAuditing() {
        JobApplication application = new JobApplication();
        application.setCompany("Acme Corp");
        application.setRole("Backend Engineer");
        application.setSource(ApplicationSource.LINKEDIN);
        application.setRemote(true);
        application.setAppliedDate(LocalDate.of(2026, 1, 15));
        application.setLastActivityAt(Instant.parse("2026-01-15T10:00:00Z"));

        JobApplication saved = jobApplicationRepository.saveAndFlush(application);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}