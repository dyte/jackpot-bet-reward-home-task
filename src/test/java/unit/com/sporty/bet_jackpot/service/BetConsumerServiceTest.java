package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.util.BetRequestGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetConsumerServiceTest {

    @Mock
    private JackpotContributionService contributionService;

    private BetConsumerService betConsumerService;

    @BeforeEach
    void setUp() {
        betConsumerService = new BetConsumerService(contributionService);
    }

    @Test
    void consumeBet_ShouldProcessContributionSuccessfully() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        doNothing().when(contributionService).processContribution(any(BetRequest.class));

        // When
        betConsumerService.consumeBet(betRequest);

        // Then
        verify(contributionService).processContribution(betRequest);
    }

    @Test
    void consumeBet_ShouldHandleExceptionFromContributionService() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        RuntimeException exception = new RuntimeException("Processing failed");
        doThrow(exception).when(contributionService).processContribution(any(BetRequest.class));

        // When & Then - Should not throw exception, should handle gracefully
        assertDoesNotThrow(() -> betConsumerService.consumeBet(betRequest));
        
        verify(contributionService).processContribution(betRequest);
    }

    @Test
    void consumeBet_ShouldHandleNullBetRequest() {
        // Given
        BetRequest betRequest = null;
        doNothing().when(contributionService).processContribution(any(BetRequest.class));

        // When & Then - Should handle null gracefully
        assertDoesNotThrow(() -> betConsumerService.consumeBet(betRequest));
        
        verify(contributionService).processContribution(betRequest);
    }

    // @Test
    
    // void consumeBet_ShouldProcessMultipleBets() {
    //     // Given
    //     BetRequest betRequest1 = BetRequestGenerator.generateBetRequest();
    //     BetRequest betRequest2 = BetRequestGenerator.generateBetRequest();
    //     doNothing().when(contributionService).processContribution(any(BetRequest.class));

    //     // When
    //     betConsumerService.consumeBet(betRequest1);
    //     betConsumerService.consumeBet(betRequest2);

    //     // Then
    //     verify(contributionService).processContribution(betRequest1);
    //     verify(contributionService).processContribution(betRequest2);
    // }

    @Test
    void consumeBet_ShouldHandleContributionServiceException() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        IllegalArgumentException exception = new IllegalArgumentException("Invalid bet data");
        doThrow(exception).when(contributionService).processContribution(any(BetRequest.class));

        // When & Then - Should not propagate exception
        assertDoesNotThrow(() -> betConsumerService.consumeBet(betRequest));
        
        verify(contributionService).processContribution(betRequest);
    }

    @Test
    void consumeBet_ShouldLogErrorOnException() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        RuntimeException exception = new RuntimeException("Database connection failed");
        doThrow(exception).when(contributionService).processContribution(any(BetRequest.class));

        // When
        betConsumerService.consumeBet(betRequest);

        // Then
        verify(contributionService).processContribution(betRequest);
        // Note: In a real test, you might want to verify logging behavior
        // but that would require additional setup for log verification
    }

    @Test
    void consumeBet_ShouldProcessBetWithValidData() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 1L, new java.math.BigDecimal("100.00"));
        doNothing().when(contributionService).processContribution(any(BetRequest.class));

        // When
        betConsumerService.consumeBet(betRequest);

        // Then
        verify(contributionService).processContribution(betRequest);
    }

    @Test
    void consumeBet_ShouldHandleEmptyBetId() {
        // Given
        BetRequest betRequest = new BetRequest("", "user-456", 1L, new java.math.BigDecimal("100.00"));
        doNothing().when(contributionService).processContribution(any(BetRequest.class));

        // When
        betConsumerService.consumeBet(betRequest);

        // Then
        verify(contributionService).processContribution(betRequest);
    }

    @Test
    void consumeBet_ShouldHandleZeroBetAmount() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 1L, java.math.BigDecimal.ZERO);
        doNothing().when(contributionService).processContribution(any(BetRequest.class));

        // When
        betConsumerService.consumeBet(betRequest);

        // Then
        verify(contributionService).processContribution(betRequest);
    }
}
