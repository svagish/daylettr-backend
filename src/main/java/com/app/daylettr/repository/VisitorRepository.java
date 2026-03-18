package com.app.daylettr.repository;

import com.app.daylettr.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, UUID> {
    Optional<Visitor> findByOrderByLastPostedAt();
}
