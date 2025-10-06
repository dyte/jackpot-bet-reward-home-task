package com.sporty.bet_jackpot.repository;

import com.sporty.bet_jackpot.model.Jackpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JackpotRepository extends JpaRepository<Jackpot, Long> {
}