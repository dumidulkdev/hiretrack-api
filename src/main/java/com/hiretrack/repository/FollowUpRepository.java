package com.hiretrack.repository;

import com.hiretrack.domain.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {

    @Query("""
            select f from FollowUp f
            where f.done = false and f.dueDate <= :today
            order by f.dueDate asc
            """)
    List<FollowUp> findDue(@Param("today") LocalDate today);

    @Query("""
            select f from FollowUp f
            where f.done = false and f.dueDate < :today and f.overdueFlagged = false
            """)
    List<FollowUp> findNewlyOverdue(@Param("today") LocalDate today);

    List<FollowUp> findByApplicationIdOrderByDueDateAsc(Long applicationId);
}