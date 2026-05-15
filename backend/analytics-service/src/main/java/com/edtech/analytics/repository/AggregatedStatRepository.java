package com.edtech.analytics.repository;

import com.edtech.analytics.entity.AggregatedStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AggregatedStatRepository extends JpaRepository<AggregatedStat, String> {
    Optional<AggregatedStat> findById(String id);
}
