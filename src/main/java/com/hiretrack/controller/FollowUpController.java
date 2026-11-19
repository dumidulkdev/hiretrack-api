package com.hiretrack.controller;

import com.hiretrack.dto.FollowUpRequest;
import com.hiretrack.dto.FollowUpResponse;
import com.hiretrack.service.FollowUpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    @PostMapping("/api/applications/{applicationId}/follow-ups")
    @ResponseStatus(HttpStatus.CREATED)
    public FollowUpResponse create(
            @PathVariable Long applicationId,
            @Valid @RequestBody FollowUpRequest request) {
        return followUpService.create(applicationId, request);
    }

    @PatchMapping("/api/applications/{applicationId}/follow-ups/{followUpId}/done")
    public FollowUpResponse markDone(@PathVariable Long applicationId, @PathVariable Long followUpId) {
        return followUpService.markDone(applicationId, followUpId);
    }

    @GetMapping("/api/follow-ups/due")
    public List<FollowUpResponse> due() {
        return followUpService.dueTodayOrOverdue();
    }
}