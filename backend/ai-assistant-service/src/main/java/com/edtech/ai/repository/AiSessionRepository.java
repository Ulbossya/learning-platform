package com.edtech.ai.repository;

import com.edtech.ai.entity.AiSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiSessionRepository extends JpaRepository<AiSession, String> {
}
