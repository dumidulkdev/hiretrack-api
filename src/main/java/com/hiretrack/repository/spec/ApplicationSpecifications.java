package com.hiretrack.repository.spec;

import com.hiretrack.domain.JobApplication;
import com.hiretrack.dto.ApplicationFilter;
import org.springframework.data.jpa.domain.Specification;

public final class ApplicationSpecifications {

    private ApplicationSpecifications() {
    }

    public static Specification<JobApplication> fromFilter(ApplicationFilter filter) {
        Specification<JobApplication> spec = Specification.where(null);
        if (filter == null) {
            return spec;
        }
        if (filter.status() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), filter.status()));
        }
        if (filter.company() != null && !filter.company().isBlank()) {
            String pattern = "%" + filter.company().trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("company")), pattern));
        }
        if (filter.source() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("source"), filter.source()));
        }
        if (filter.from() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("appliedDate"), filter.from()));
        }
        if (filter.to() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("appliedDate"), filter.to()));
        }
        if (filter.q() != null && !filter.q().isBlank()) {
            String pattern = "%" + filter.q().trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("company")), pattern),
                    cb.like(cb.lower(root.get("role")), pattern),
                    cb.like(cb.lower(root.get("notes")), pattern)
            ));
        }
        return spec;
    }
}