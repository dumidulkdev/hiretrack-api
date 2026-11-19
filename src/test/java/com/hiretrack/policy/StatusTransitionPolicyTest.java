package com.hiretrack.policy;

import com.hiretrack.domain.ApplicationStatus;
import com.hiretrack.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusTransitionPolicyTest {

    private StatusTransitionPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new StatusTransitionPolicy();
    }

    @Test
    void allowsExpectedPipelineMoves() {
        assertThat(policy.canTransition(ApplicationStatus.APPLIED, ApplicationStatus.SCREENING)).isTrue();
        assertThat(policy.canTransition(ApplicationStatus.SCREENING, ApplicationStatus.INTERVIEW)).isTrue();
        assertThat(policy.canTransition(ApplicationStatus.INTERVIEW, ApplicationStatus.OFFER)).isTrue();
    }

    @Test
    void rejectsSkippingStages() {
        assertThatThrownBy(() -> policy.assertTransition(ApplicationStatus.APPLIED, ApplicationStatus.OFFER))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Cannot move");
    }

    @Test
    void rejectsTerminalTransitions() {
        assertThatThrownBy(() -> policy.assertTransition(ApplicationStatus.REJECTED, ApplicationStatus.APPLIED))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}