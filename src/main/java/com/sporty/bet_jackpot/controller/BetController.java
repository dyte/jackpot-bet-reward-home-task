package com.sporty.bet_jackpot.controller;

import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.dto.BetResponse;
import com.sporty.bet_jackpot.service.BetProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
@Slf4j
public class BetController {
    
    private final BetProducerService producerService;
    
    @PostMapping("/place-bet")
    public ResponseEntity<BetResponse> placeBet(@Valid @RequestBody BetRequest betRequest) {
        log.info("Received bet request: {}", betRequest);

        // First option: Publish bet asynchronously to Kafka
        producerService.publishBetAsynchronously(betRequest);

        // Alternative: Based on the situation, we may require making a synchronous request to Kafka
        // producerService.publishBetSynchronously(betRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new BetResponse(
                betRequest.getBetId(), 
                "Bet accepted and sent for processing",
                "ACCEPTED"
            )
        );
    }
}