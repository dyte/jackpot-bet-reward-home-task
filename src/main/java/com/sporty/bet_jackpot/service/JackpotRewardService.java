package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.RewardResponse;
import com.sporty.bet_jackpot.exception.business_exceptions.BetNotFoundException;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.model.JackpotContribution;
import com.sporty.bet_jackpot.model.JackpotReward;
import com.sporty.bet_jackpot.repository.JackpotContributionRepository;
import com.sporty.bet_jackpot.repository.JackpotRewardRepository;
import com.sporty.bet_jackpot.strategy.reward.RewardStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JackpotRewardService {

    private final JackpotService jackpotService;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotRewardRepository rewardRepository;

    public RewardResponse evaluateReward(String betId) {
        // Find contribution
        JackpotContribution contribution = contributionRepository.findByBetId(betId)
                .orElseThrow(() -> new BetNotFoundException("Bet not found: " + betId));

        // Find jackpot
        Jackpot jackpot = jackpotService.findById(contribution.getJackpotId());

        // 1st check: If this specific BetId has already won
        Optional<JackpotReward> byBetIdAndJackpotId = rewardRepository.findByBetIdAndJackpotId(betId, jackpot.getId());
        if (byBetIdAndJackpotId.isPresent()) {
            log.info("Bet {} has already won jackpot {}. Returning previous win result.", betId, jackpot.getId());
            return new RewardResponse(betId, false, byBetIdAndJackpotId.get().getJackpotRewardAmount(), "You have already won this jackpot");
        }

        // 2nd check: If any bet has won for this JackpotId
        if (rewardRepository.existsByJackpotId(jackpot.getId())) {
            log.info("Jackpot {} has already been won by another bet. This bet loses.", jackpot.getId());
            return new RewardResponse(betId, false, BigDecimal.ZERO, "Jackpot has already been won");
        }

        // Get reward strategy
        RewardStrategy strategy = jackpotService.getRewardStrategy(jackpot.getRewardType());

        // Evaluate if winner
        boolean isWinner = strategy.evaluateWin(jackpot);

        if (isWinner) {
            BigDecimal rewardAmount = jackpot.getCurrentPoolValue();

            // Save reward record
            JackpotReward reward = new JackpotReward();
            reward.setBetId(betId);
            reward.setUserId(contribution.getUserId());
            reward.setJackpotId(jackpot.getId());
            reward.setJackpotRewardAmount(rewardAmount);
            reward.setCreatedAt(LocalDateTime.now());
            rewardRepository.save(reward);

            // Reset jackpot to initial value
            jackpot.setCurrentPoolValue(jackpot.getInitialPoolValue());
            jackpotService.save(jackpot);
            
            // Evict cache for this jackpot since it was reset
            jackpotService.evictCache(jackpot.getId());

            log.info("Bet {} won jackpot! Amount: {}", betId, rewardAmount);

            return new RewardResponse(betId, true, rewardAmount, "Congratulations! You won the jackpot!");
        }

        return new RewardResponse(betId, false, BigDecimal.ZERO, "Better luck next time!");
    }
}