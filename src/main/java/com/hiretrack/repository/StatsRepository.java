package com.hiretrack.repository;

import com.hiretrack.domain.ApplicationSource;
import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.dto.StatsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Repository
public class StatsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<ApplicationStatus, Long> totalsByStatus() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(
                        "SELECT status, COUNT(*) FROM applications GROUP BY status")
                .getResultList();
        Map<ApplicationStatus, Long> totals = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus status : ApplicationStatus.values()) {
            totals.put(status, 0L);
        }
        for (Object[] row : rows) {
            totals.put(ApplicationStatus.valueOf(row[0].toString()), ((Number) row[1]).longValue());
        }
        return totals;
    }

    public long totalApplications() {
        Number count = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM applications")
                .getSingleResult();
        return count.longValue();
    }

    public long countResponded() {
        Number count = (Number) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM applications WHERE status NOT IN ('APPLIED','GHOSTED')")
                .getSingleResult();
        return count.longValue();
    }

    public long countInterviewStage() {
        Number count = (Number) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM applications WHERE status IN ('INTERVIEW','OFFER')")
                .getSingleResult();
        return count.longValue();
    }

    public Double averageDaysToFirstResponse() {
        Object result = entityManager.createNativeQuery("""
                        SELECT AVG(EXTRACT(EPOCH FROM (h.changed_at - a.created_at)) / 86400.0)
                        FROM status_history h
                        JOIN applications a ON a.id = h.application_id
                        WHERE h.from_status = 'APPLIED'
                          AND h.to_status <> 'APPLIED'
                        """)
                .getSingleResult();
        if (result == null) {
            return null;
        }
        return ((Number) result).doubleValue();
    }

    @SuppressWarnings("unchecked")
    public List<StatsResponse.SourceCount> topSources(int limit) {
        List<Object[]> rows = entityManager.createNativeQuery(
                        "SELECT source, COUNT(*) AS cnt FROM applications GROUP BY source ORDER BY cnt DESC")
                .setMaxResults(limit)
                .getResultList();
        return rows.stream()
                .map(row -> new StatsResponse.SourceCount(
                        ApplicationSource.valueOf(row[0].toString()),
                        ((Number) row[1]).longValue()))
                .toList();
    }
}