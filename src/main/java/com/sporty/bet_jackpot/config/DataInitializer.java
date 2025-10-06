package com.sporty.bet_jackpot.config;

import com.sporty.bet_jackpot.builder.JackpotBuilder;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.repository.JackpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final JackpotRepository jackpotRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing jackpot data...");

        // Jackpot 1: Fixed contribution, Fixed reward
        Jackpot jackpot1 = JackpotBuilder.newJackpot("Classic Jackpot")
                .withFixedContribution()
                .withFixedReward()
                .withPoolValue(BigDecimal.valueOf(1000))
                .withContributionPercentage(5.0)
                .withRewardChancePercentage(1.0)
                .withCurrentTimestamp()
                .build();
        jackpotRepository.save(jackpot1);

        // Jackpot 2: Variable contribution, Variable reward
        Jackpot jackpot2 = JackpotBuilder.newJackpot("Progressive Jackpot")
                .withVariableContribution()
                .withVariableReward()
                .withPoolValue(BigDecimal.valueOf(1000))
                .withCurrentTimestamp()
                .build();
        jackpotRepository.save(jackpot2);

        log.info("Initialized {} jackpots", jackpotRepository.count());
    }
}