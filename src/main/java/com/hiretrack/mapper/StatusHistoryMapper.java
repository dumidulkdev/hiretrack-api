package com.hiretrack.mapper;

import com.hiretrack.domain.StatusHistory;
import com.hiretrack.dto.StatusHistoryResponse;
import org.springframework.stereotype.Component;

@Component
public class StatusHistoryMapper {

    public StatusHistoryResponse toResponse(StatusHistory history) {
        return new StatusHistoryResponse(
                history.getId(),
                history.getFromStatus(),
                history.getToStatus(),
                history.getChangedAt(),
                history.getNote()
        );
    }
}