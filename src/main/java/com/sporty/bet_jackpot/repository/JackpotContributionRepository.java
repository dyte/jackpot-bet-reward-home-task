package com.sporty.bet_jackpot.repository;

import com.sporty.bet_jackpot.model.JackpotContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotContributionRepository extends JpaRepository<JackpotContribution, Long> {
    Optional<JackpotContribution> findByBetId(String betId);
}