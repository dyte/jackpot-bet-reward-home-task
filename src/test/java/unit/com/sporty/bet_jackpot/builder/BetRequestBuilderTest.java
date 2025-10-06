package com.sporty.bet_jackpot.builder;

import com.sporty.bet_jackpot.dto.BetRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BetRequestBuilderTest {

    @Test
    void build_ShouldCreateValidBetRequest_WhenAllRequiredFieldsProvided() {
        // When
        BetRequest betRequest = BetRequestBuilder.newBetRequest()
                .withBetId("bet-123")
                .withUserId("user-456")
                .withJackpotId(1L)
                .withBetAmount(BigDecimal.valueOf(100.0))
                .build();

        // Then
        assertNotNull(betRequest);
        assertEquals("bet-123", betRequest.getBetId());
        assertEquals("user-456", betRequest.getUserId());
        assertEquals(1L, betRequest.getJackpotId());
        assertEquals(BigDecimal.valueOf(100.0), betRequest.getBetAmount());
    }

    @Test
    void build_ShouldCreateValidBetRequest_WhenUsingConvenienceMethods() {
        // When
        BetRequest betRequest = BetRequestBuilder.newBetRequest()
                .withBetId("bet-123")
                .withUserId("user-456")
                .withJackpotId(1L)
                .withBetAmount(100.0) // Using double
                .build();

        // Then
        assertNotNull(betRequest);
        assertEquals("bet-123", betRequest.getBetId());
        assertEquals("user-456", betRequest.getUserId());
        assertEquals(1L, betRequest.getJackpotId());
        assertEquals(BigDecimal.valueOf(100.0), betRequest.getBetAmount());
    }

    @Test
    void build_ShouldCreateValidBetRequest_WhenUsingStringBetAmount() {
        // When
        BetRequest betRequest = BetRequestBuilder.newBetRequest()
                .withBetId("bet-123")
                .withUserId("user-456")
                .withJackpotId(1L)
                .withBetAmount("100.50") // Using string
                .build();

        // Then
        assertNotNull(betRequest);
        assertEquals(new BigDecimal("100.50"), betRequest.getBetAmount());
    }

    @Test
    void build_ShouldCreateValidBetRequest_WhenUsingGeneratedBetId() {
        // When
        BetRequest betRequest = BetRequestBuilder.newBetRequestWithGeneratedId()
                .withUserId("user-456")
                .withJackpotId(1L)
                .withBetAmount(100.0)
                .build();

        // Then
        assertNotNull(betRequest);
        assertNotNull(betRequest.getBetId());
        assertFalse(betRequest.getBetId().isEmpty());
        assertEquals("user-456", betRequest.getUserId());
        assertEquals(1L, betRequest.getJackpotId());
        assertEquals(BigDecimal.valueOf(100.0), betRequest.getBetAmount());
    }

    @Test
    void build_ShouldThrowException_WhenBetIdIsMissing() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withUserId("user-456")
                        .withJackpotId(1L)
                        .withBetAmount(100.0)
                        .build()
        );

        assertEquals("Bet ID is required", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenBetIdIsEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("")
                        .withUserId("user-456")
                        .withJackpotId(1L)
                        .withBetAmount(100.0)
                        .build()
        );

        assertEquals("Bet ID is required", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenUserIdIsMissing() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("bet-123")
                        .withJackpotId(1L)
                        .withBetAmount(100.0)
                        .build()
        );

        assertEquals("User ID is required", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenJackpotIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("bet-123")
                        .withUserId("user-456")
                        .withBetAmount(100.0)
                        .build()
        );

        assertEquals("Jackpot ID must be positive", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenJackpotIdIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("bet-123")
                        .withUserId("user-456")
                        .withJackpotId(0L)
                        .withBetAmount(100.0)
                        .build()
        );

        assertEquals("Jackpot ID must be positive", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenBetAmountIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("bet-123")
                        .withUserId("user-456")
                        .withJackpotId(1L)
                        .build()
        );

        assertEquals("Bet amount must be positive", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenBetAmountIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("bet-123")
                        .withUserId("user-456")
                        .withJackpotId(1L)
                        .withBetAmount(BigDecimal.ZERO)
                        .build()
        );

        assertEquals("Bet amount must be positive", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenBetAmountIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("bet-123")
                        .withUserId("user-456")
                        .withJackpotId(1L)
                        .withBetAmount(-100.0)
                        .build()
        );

        assertEquals("Bet amount must be positive", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenBetAmountExceedsMaximum() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BetRequestBuilder.newBetRequest()
                        .withBetId("bet-123")
                        .withUserId("user-456")
                        .withJackpotId(1L)
                        .withBetAmount(2000000.0) // Exceeds 1,000,000 limit
                        .build()
        );

        assertEquals("Bet amount cannot exceed 1,000,000.00", exception.getMessage());
    }

    @Test
    void build_ShouldAcceptMaximumBetAmount() {
        // When
        BetRequest betRequest = BetRequestBuilder.newBetRequest()
                .withBetId("bet-123")
                .withUserId("user-456")
                .withJackpotId(1L)
                .withBetAmount(1000000.0) // Exactly at limit
                .build();

        // Then
        assertNotNull(betRequest);
        assertEquals(BigDecimal.valueOf(1000000.0), betRequest.getBetAmount());
    }
}
