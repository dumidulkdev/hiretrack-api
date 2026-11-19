package com.hiretrack.export;

import com.hiretrack.domain.JobApplication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ApplicationCsvWriter {

    private static final String HEADER = "id,company,role,jobUrl,source,salaryMin,salaryMax,location,remote,status,appliedDate,notes";

    public void write(Stream<JobApplication> applications, Writer writer) throws IOException {
        writer.write(HEADER);
        writer.write('\n');
        applications.forEach(application -> {
            try {
                writer.write(formatLine(application));
                writer.write('\n');
            } catch (IOException ex) {
                throw new IllegalStateException("Failed writing CSV row", ex);
            }
        });
        writer.flush();
    }

    public void write(List<JobApplication> applications, Writer writer) throws IOException {
        write(applications.stream(), writer);
    }

    String formatLine(JobApplication application) {
        return String.join(",",
                escape(String.valueOf(application.getId())),
                escape(application.getCompany()),
                escape(application.getRole()),
                escape(nullToEmpty(application.getJobUrl())),
                escape(application.getSource().name()),
                escape(nullToEmpty(application.getSalaryMin())),
                escape(nullToEmpty(application.getSalaryMax())),
                escape(nullToEmpty(application.getLocation())),
                escape(String.valueOf(application.isRemote())),
                escape(application.getStatus().name()),
                escape(application.getAppliedDate().toString()),
                escape(nullToEmpty(application.getNotes()))
        );
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean mustQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }

    private String nullToEmpty(Object value) {
        return value == null ? "" : value.toString();
    }
}