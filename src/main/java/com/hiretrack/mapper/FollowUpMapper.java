package com.hiretrack.mapper;

import com.hiretrack.domain.FollowUp;
import com.hiretrack.dto.FollowUpResponse;
import org.springframework.stereotype.Component;

@Component
public class FollowUpMapper {

    public FollowUpResponse toResponse(FollowUp followUp) {
        return new FollowUpResponse(
                followUp.getId(),
                followUp.getApplication().getId(),
                followUp.getDueDate(),
                followUp.getNote(),
                followUp.isDone(),
                followUp.isOverdueFlagged(),
                followUp.getCreatedAt(),
                followUp.getUpdatedAt()
        );
    }
}