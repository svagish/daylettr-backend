package com.app.daylettr.repository;

import com.app.daylettr.entity.DailyVisitorStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyVisitorStatRepository extends JpaRepository<DailyVisitorStat, UUID> {
    Optional<DailyVisitorStat> findByVisitorIdAndStatDate(UUID visitorId, LocalDate statDate);

    List<DailyVisitorStat> findByVisitorIdAndStatDateBetweenOrderByStatDateAsc(UUID visitorId, LocalDate start, LocalDate end);

    long countByVisitorId(UUID visitorId);

    @Query("select coalesce(sum(s.messagesPosted), 0) from DailyVisitorStat s where s.visitorId = :visitorId")
    Long sumMessagesPostedByVisitorId(@Param("visitorId") UUID visitorId);
}
