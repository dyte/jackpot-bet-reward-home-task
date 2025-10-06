package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.BetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetConsumerService {
    
    private final JackpotContributionService contributionService;
    
    @KafkaListener(topics = "${kafka.topic.jackpot-bets}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBet(BetRequest betRequest) {
        log.info("Consumed bet from Kafka: {}", betRequest);
        try {
            contributionService.processContribution(betRequest);
        } catch (Exception e) {
            log.error("Error processing bet: {}", betRequest, e);
            // In production, consider DLQ (Dead Letter Queue)
        }
    }
}