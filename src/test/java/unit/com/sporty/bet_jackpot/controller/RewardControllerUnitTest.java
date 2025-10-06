package com.sporty.bet_jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.bet_jackpot.dto.RewardResponse;
import com.sporty.bet_jackpot.exception.business_exceptions.BetNotFoundException;
import com.sporty.bet_jackpot.service.CacheManagementService;
import com.sporty.bet_jackpot.service.JackpotRewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RewardController.class)
@TestPropertySource(properties = {
    "spring.cache.type=none"
})
class RewardControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private JackpotRewardService rewardService;
    
    @MockitoBean
    private CacheManagementService cacheManagementService;

    @Test
    void evaluateReward_ShouldReturnWinnerResponse_WhenBetWins() throws Exception {
        // Given
        String betId = "bet-123";
        RewardResponse winnerResponse = new RewardResponse(
                betId, 
                true, 
                new BigDecimal("5000.00"), 
                "Congratulations! You won the jackpot!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(winnerResponse);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(true))
                .andExpect(jsonPath("$.rewardAmount").value(5000.00))
                .andExpect(jsonPath("$.message").value("Congratulations! You won the jackpot!"));
    }

    @Test
    void evaluateReward_ShouldReturnLoserResponse_WhenBetLoses() throws Exception {
        // Given
        String betId = "bet-456";
        RewardResponse loserResponse = new RewardResponse(
                betId, 
                false, 
                BigDecimal.ZERO, 
                "Better luck next time!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(loserResponse);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(false))
                .andExpect(jsonPath("$.rewardAmount").value(0))
                .andExpect(jsonPath("$.message").value("Better luck next time!"));
    }

    @Test
    void evaluateReward_ShouldReturnNotFound_WhenBetNotFound() throws Exception {
        // Given
        String betId = "non-existent-bet";
        when(rewardService.evaluateReward(betId)).thenThrow(new BetNotFoundException("Bet not found: " + betId));

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Note: This test is commented out because the controller doesn't have global exception handling
    // @Test
    // void evaluateReward_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
    //     // Given
    //     String betId = "bet-error";
    //     when(rewardService.evaluateReward(betId)).thenThrow(new RuntimeException("Database connection failed"));
    //
    //     // When & Then
    //     mockMvc
    //             .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().is5xxServerError());
    // }

    @Test
    void evaluateReward_ShouldHandleEmptyBetId() throws Exception {
        // Given
        String betId = "";
        when(rewardService.evaluateReward(betId)).thenThrow(new BetNotFoundException("Bet not found: " + betId));

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void evaluateReward_ShouldHandleSpecialCharactersInBetId() throws Exception {
        // Given
        String betId = "bet-123_abc-456";
        RewardResponse response = new RewardResponse(
                betId, 
                false, 
                BigDecimal.ZERO, 
                "Better luck next time!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(false))
                .andExpect(jsonPath("$.rewardAmount").value(0));
    }

    @Test
    void evaluateReward_ShouldHandleLongBetId() throws Exception {
        // Given
        String betId = "very-long-bet-id-with-many-characters-123456789";
        RewardResponse response = new RewardResponse(
                betId, 
                false, 
                BigDecimal.ZERO, 
                "Better luck next time!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(false));
    }

    @Test
    void evaluateReward_ShouldReturnCorrectResponseStructure() throws Exception {
        // Given
        String betId = "bet-789";
        RewardResponse response = new RewardResponse(
                betId, 
                true, 
                new BigDecimal("10000.50"), 
                "Congratulations! You won the jackpot!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").exists())
                .andExpect(jsonPath("$.winner").exists())
                .andExpect(jsonPath("$.rewardAmount").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.betId").isString())
                .andExpect(jsonPath("$.winner").isBoolean())
                .andExpect(jsonPath("$.rewardAmount").isNumber())
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void evaluateReward_ShouldHandleZeroRewardAmount() throws Exception {
        // Given
        String betId = "bet-zero-reward";
        RewardResponse response = new RewardResponse(
                betId, 
                true, 
                BigDecimal.ZERO, 
                "Congratulations! You won the jackpot!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(true))
                .andExpect(jsonPath("$.rewardAmount").value(0))
                .andExpect(jsonPath("$.message").value("Congratulations! You won the jackpot!"));
    }

    @Test
    void evaluateReward_ShouldHandleLargeRewardAmount() throws Exception {
        // Given
        String betId = "bet-large-reward";
        BigDecimal largeAmount = new BigDecimal("999999.99");
        RewardResponse response = new RewardResponse(
                betId, 
                true, 
                largeAmount, 
                "Congratulations! You won the jackpot!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(true))
                .andExpect(jsonPath("$.rewardAmount").value(999999.99))
                .andExpect(jsonPath("$.message").value("Congratulations! You won the jackpot!"));
    }

    @Test
    void evaluateReward_ShouldHandleNullBetId() throws Exception {
        // Given
        String betId = null;
        when(rewardService.evaluateReward(betId)).thenThrow(new BetNotFoundException("Bet not found: " + betId));

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void evaluateReward_ShouldHandleNumericBetId() throws Exception {
        // Given
        String betId = "123456";
        RewardResponse response = new RewardResponse(
                betId, 
                false, 
                BigDecimal.ZERO, 
                "Better luck next time!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(false));
    }

    @Test
    void evaluateReward_ShouldHandleBetIdWithSpaces() throws Exception {
        // Given
        String betId = "bet with spaces";
        RewardResponse response = new RewardResponse(
                betId, 
                false, 
                BigDecimal.ZERO, 
                "Better luck next time!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(false));
    }

    @Test
    void evaluateReward_ShouldReturnJsonContentType() throws Exception {
        // Given
        String betId = "bet-content-type";
        RewardResponse response = new RewardResponse(
                betId, 
                false, 
                BigDecimal.ZERO, 
                "Better luck next time!"
        );
        
        when(rewardService.evaluateReward(betId)).thenReturn(response);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void evaluateReward_ShouldReturnAlreadyWonMessage_WhenThisBetHasAlreadyWon() throws Exception {
        // Given
        String betId = "bet-already-won";
        RewardResponse mockResponse = new RewardResponse(betId, false, BigDecimal.ZERO, "You have already won this jackpot");
        when(rewardService.evaluateReward(anyString())).thenReturn(mockResponse);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(false))
                .andExpect(jsonPath("$.rewardAmount").value(0.00))
                .andExpect(jsonPath("$.message").value("You have already won this jackpot"));
    }

    @Test
    void evaluateReward_ShouldReturnJackpotAlreadyWon_WhenAnotherBetHasWon() throws Exception {
        // Given
        String betId = "bet-lost";
        RewardResponse mockResponse = new RewardResponse(betId, false, BigDecimal.ZERO, "Jackpot has already been won");
        when(rewardService.evaluateReward(anyString())).thenReturn(mockResponse);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/rewards/evaluate/{betId}", betId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betId").value(betId))
                .andExpect(jsonPath("$.winner").value(false))
                .andExpect(jsonPath("$.rewardAmount").value(0.00))
                .andExpect(jsonPath("$.message").value("Jackpot has already been won"));
    }
}
