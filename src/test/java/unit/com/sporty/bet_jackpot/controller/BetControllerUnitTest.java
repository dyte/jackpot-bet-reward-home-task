package com.sporty.bet_jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.dto.BetResponse;
import com.sporty.bet_jackpot.service.BetProducerService;
import com.sporty.bet_jackpot.service.JackpotService;
import com.sporty.bet_jackpot.util.BetRequestGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BetController.class)
@TestPropertySource(properties = {
    "kafka.topic.jackpot-bets=test-topic",
    "spring.cache.type=none"
})
class BetControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private BetProducerService producerService;
    
    @MockitoBean
    private JackpotService jackpotService;

    @Test
    void placeBet_ShouldReturnCreatedStatus_WhenValidRequest() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        String requestContent = objectMapper.writeValueAsString(betRequest);

        doNothing().when(producerService).publishBetAsynchronously(isA(BetRequest.class));

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").value(betRequest.getBetId()))
                .andExpect(jsonPath("$.message").value("Bet accepted and sent for processing"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(producerService).publishBetAsynchronously(betRequest);
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenInvalidBetId() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest_BlankBetId();
        String requestContent = objectMapper.writeValueAsString(betRequest);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest_BlankUserId();
        String requestContent = objectMapper.writeValueAsString(betRequest);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenNullJackpotId() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest_NullJackPotIdId();
        String requestContent = objectMapper.writeValueAsString(betRequest);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenNegativeBetAmount() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        betRequest.setBetAmount(new BigDecimal("-100.00")); // Override to negative amount
        String requestContent = objectMapper.writeValueAsString(betRequest);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenZeroBetAmount() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        betRequest.setBetAmount(BigDecimal.ZERO); // Override to zero amount
        String requestContent = objectMapper.writeValueAsString(betRequest);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenNullBetAmount() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest_NullBetAmount();
        String requestContent = objectMapper.writeValueAsString(betRequest);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenMissingContentType() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        String requestContent = objectMapper.writeValueAsString(betRequest);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenInvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeBet_ShouldHandleLargeBetAmount() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        betRequest.setBetId("bet-123");
        betRequest.setUserId("user-123");
        betRequest.setBetAmount(new BigDecimal("999999.99")); // Override to large amount
        String requestContent = objectMapper.writeValueAsString(betRequest);

        doNothing().when(producerService).publishBetAsynchronously(isA(BetRequest.class));

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").value("bet-123"))
                .andExpect(jsonPath("$.message").value("Bet accepted and sent for processing"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(producerService).publishBetAsynchronously(betRequest);
    }

    @Test
    void placeBet_ShouldHandleSpecialCharactersInBetId() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        betRequest.setBetId("bet-123_abc-456");
        betRequest.setUserId("user-123");
        betRequest.setBetAmount(new BigDecimal("100.00")); // Override to specific amount
        String requestContent = objectMapper.writeValueAsString(betRequest);

        doNothing().when(producerService).publishBetAsynchronously(isA(BetRequest.class));

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").value("bet-123_abc-456"))
                .andExpect(jsonPath("$.message").value("Bet accepted and sent for processing"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(producerService).publishBetAsynchronously(betRequest);
    }

    @Test
    void placeBet_ShouldReturnCorrectResponseStructure() throws Exception {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        String requestContent = objectMapper.writeValueAsString(betRequest);

        doNothing().when(producerService).publishBetAsynchronously(isA(BetRequest.class));

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/bets/place-bet")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.betId").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.betId").isString())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.status").isString());
    }
}
