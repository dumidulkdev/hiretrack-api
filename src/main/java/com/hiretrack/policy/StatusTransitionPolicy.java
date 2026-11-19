package com.hiretrack.policy;

import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class StatusTransitionPolicy {

    private static final Map<ApplicationStatus, Set<ApplicationStatus>> ALLOWED = new EnumMap<>(ApplicationStatus.class);

    static {
        ALLOWED.put(ApplicationStatus.APPLIED, EnumSet.of(
                ApplicationStatus.SCREENING, ApplicationStatus.INTERVIEW,
                ApplicationStatus.REJECTED, ApplicationStatus.GHOSTED));
        ALLOWED.put(ApplicationStatus.SCREENING, EnumSet.of(
                ApplicationStatus.INTERVIEW, ApplicationStatus.REJECTED, ApplicationStatus.GHOSTED));
        ALLOWED.put(ApplicationStatus.INTERVIEW, EnumSet.of(
                ApplicationStatus.OFFER, ApplicationStatus.REJECTED, ApplicationStatus.GHOSTED));
        ALLOWED.put(ApplicationStatus.OFFER, EnumSet.of(ApplicationStatus.REJECTED));
        ALLOWED.put(ApplicationStatus.REJECTED, EnumSet.noneOf(ApplicationStatus.class));
        ALLOWED.put(ApplicationStatus.GHOSTED, EnumSet.noneOf(ApplicationStatus.class));
    }

    public void assertTransition(ApplicationStatus from, ApplicationStatus to) {
        if (from == to) {
            throw new InvalidStatusTransitionException("Status is already " + to);
        }
        Set<ApplicationStatus> targets = ALLOWED.getOrDefault(from, Set.of());
        if (!targets.contains(to)) {
            throw new InvalidStatusTransitionException(
                    "Cannot move from " + from + " to " + to + ". Allowed next statuses: " + targets);
        }
    }

    public boolean canTransition(ApplicationStatus from, ApplicationStatus to) {
        if (from == to) {
            return false;
        }
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }
}