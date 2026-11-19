package com.hiretrack.export;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.domain.JobApplication;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationCsvWriterTest {

  private final ApplicationCsvWriter writer = new ApplicationCsvWriter();

    @Test
    void escapesCommasAndQuotes() throws Exception {
        JobApplication application = new JobApplication();
        application.setCompany("Acme, Inc");
        application.setRole("Engineer");
        application.setSource(ApplicationSource.LINKEDIN);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDate.of(2026, 1, 1));
        application.setNotes("Said \"hello\"");

        StringWriter out = new StringWriter();
        writer.write(List.of(application), out);

        assertThat(out.toString()).contains("\"Acme, Inc\"");
        assertThat(out.toString()).contains("\"Said \"\"hello\"\"\"");
    }

    @Test
    void includesHeaderRow() throws Exception {
        StringWriter out = new StringWriter();
        writer.write(List.of(), out);
        assertThat(out.toString()).startsWith("id,company,role");
    }
}