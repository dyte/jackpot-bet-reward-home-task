package com.sporty.bet_jackpot.repository;

import com.sporty.bet_jackpot.model.JackpotReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotRewardRepository extends JpaRepository<JackpotReward, Long> {
    
    Optional<JackpotReward> findByBetIdAndJackpotId(String betId, Long jackpotId);
    
    boolean existsByJackpotId(Long jackpotId);
}