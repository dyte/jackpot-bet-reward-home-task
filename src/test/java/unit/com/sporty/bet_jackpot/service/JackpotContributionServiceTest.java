package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.exception.business_exceptions.InvalidBetIdExceptionJackPot;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.model.JackpotContribution;
import com.sporty.bet_jackpot.repository.JackpotContributionRepository;
import com.sporty.bet_jackpot.strategy.contribution.ContributionStrategy;
import com.sporty.bet_jackpot.util.BetRequestGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JackpotContributionServiceTest {

    @Mock
    private JackpotService jackpotService;

    @Mock
    private JackpotContributionRepository contributionRepository;

    @Mock
    private ContributionStrategy contributionStrategy;

    private JackpotContributionService contributionService;

    @BeforeEach
    void setUp() {
        contributionService = new JackpotContributionService(jackpotService, contributionRepository);
    }

    @Test
    void processContribution_ShouldProcessNewBetSuccessfully() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        Jackpot jackpot = createTestJackpot(1L);
        BigDecimal contributionAmount = new BigDecimal("10.00");
        
        when(contributionRepository.findByBetId(betRequest.getBetId())).thenReturn(Optional.empty());
        when(jackpotService.findById(betRequest.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getContributionStrategy(jackpot.getContributionType())).thenReturn(contributionStrategy);
        when(contributionStrategy.calculateContribution(betRequest.getBetAmount(), jackpot)).thenReturn(contributionAmount);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(contributionRepository.save(any(JackpotContribution.class))).thenReturn(new JackpotContribution());

        // When
        contributionService.processContribution(betRequest);

        // Then
        verify(contributionRepository).findByBetId(betRequest.getBetId());
        verify(jackpotService).findById(betRequest.getJackpotId());
        verify(jackpotService).getContributionStrategy(jackpot.getContributionType());
        verify(contributionStrategy).calculateContribution(betRequest.getBetAmount(), jackpot);
        verify(jackpotService).save(jackpot);
        verify(contributionRepository).save(any(JackpotContribution.class));
    }

    @Test
    void processContribution_ShouldThrowException_WhenBetIdAlreadyExists() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        JackpotContribution existingContribution = new JackpotContribution();
        existingContribution.setBetId(betRequest.getBetId());
        
        when(contributionRepository.findByBetId(betRequest.getBetId())).thenReturn(Optional.of(existingContribution));

        // When & Then
        InvalidBetIdExceptionJackPot exception = assertThrows(
                InvalidBetIdExceptionJackPot.class,
                () -> contributionService.processContribution(betRequest)
        );

        assertTrue(exception.getMessage().contains("Invalid or duplicate bet ID"));
        verify(contributionRepository).findByBetId(betRequest.getBetId());
        verify(jackpotService, never()).findById(any());
        verify(contributionRepository, never()).save(any());
    }

    @Test
    void processContribution_ShouldUpdateJackpotPoolValue() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setCurrentPoolValue(new BigDecimal("1000.00"));
        BigDecimal contributionAmount = new BigDecimal("50.00");
        BigDecimal expectedNewPoolValue = new BigDecimal("1050.00");
        
        when(contributionRepository.findByBetId(betRequest.getBetId())).thenReturn(Optional.empty());
        when(jackpotService.findById(betRequest.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getContributionStrategy(jackpot.getContributionType())).thenReturn(contributionStrategy);
        when(contributionStrategy.calculateContribution(betRequest.getBetAmount(), jackpot)).thenReturn(contributionAmount);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(contributionRepository.save(any(JackpotContribution.class))).thenReturn(new JackpotContribution());

        // When
        contributionService.processContribution(betRequest);

        // Then
        ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
        verify(jackpotService).save(jackpotCaptor.capture());
        
        Jackpot savedJackpot = jackpotCaptor.getValue();
        assertEquals(expectedNewPoolValue, savedJackpot.getCurrentPoolValue());
    }

    @Test
    void processContribution_ShouldCreateContributionRecord() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setCurrentPoolValue(new BigDecimal("1000.00"));
        BigDecimal contributionAmount = new BigDecimal("25.00");
        
        when(contributionRepository.findByBetId(betRequest.getBetId())).thenReturn(Optional.empty());
        when(jackpotService.findById(betRequest.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getContributionStrategy(jackpot.getContributionType())).thenReturn(contributionStrategy);
        when(contributionStrategy.calculateContribution(betRequest.getBetAmount(), jackpot)).thenReturn(contributionAmount);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(contributionRepository.save(any(JackpotContribution.class))).thenReturn(new JackpotContribution());

        // When
        contributionService.processContribution(betRequest);

        // Then
        ArgumentCaptor<JackpotContribution> contributionCaptor = ArgumentCaptor.forClass(JackpotContribution.class);
        verify(contributionRepository).save(contributionCaptor.capture());
        
        JackpotContribution savedContribution = contributionCaptor.getValue();
        assertEquals(betRequest.getBetId(), savedContribution.getBetId());
        assertEquals(betRequest.getUserId(), savedContribution.getUserId());
        assertEquals(betRequest.getJackpotId(), savedContribution.getJackpotId());
        assertEquals(betRequest.getBetAmount(), savedContribution.getStakeAmount());
        assertEquals(contributionAmount, savedContribution.getContributionAmount());
        assertNotNull(savedContribution.getCurrentJackpotAmount());
        assertNotNull(savedContribution.getCreatedAt());
    }

    @Test
    void processContribution_ShouldHandleZeroContribution() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setCurrentPoolValue(new BigDecimal("1000.00"));
        BigDecimal contributionAmount = BigDecimal.ZERO;
        
        when(contributionRepository.findByBetId(betRequest.getBetId())).thenReturn(Optional.empty());
        when(jackpotService.findById(betRequest.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getContributionStrategy(jackpot.getContributionType())).thenReturn(contributionStrategy);
        when(contributionStrategy.calculateContribution(betRequest.getBetAmount(), jackpot)).thenReturn(contributionAmount);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(contributionRepository.save(any(JackpotContribution.class))).thenReturn(new JackpotContribution());

        // When
        contributionService.processContribution(betRequest);

        // Then
        verify(contributionRepository).save(any(JackpotContribution.class));
        verify(jackpotService).save(any(Jackpot.class));
    }

    @Test
    void processContribution_ShouldHandleNullBetRequest() {
        // Given
        BetRequest betRequest = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> contributionService.processContribution(betRequest));
    }

    @Test
    void processContribution_ShouldHandleJackpotNotFound() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        
        when(contributionRepository.findByBetId(betRequest.getBetId())).thenReturn(Optional.empty());
        when(jackpotService.findById(betRequest.getJackpotId())).thenThrow(new RuntimeException("Jackpot not found"));

        // When & Then
        assertThrows(RuntimeException.class, () -> contributionService.processContribution(betRequest));
        
        verify(contributionRepository).findByBetId(betRequest.getBetId());
        verify(jackpotService).findById(betRequest.getJackpotId());
        verify(contributionRepository, never()).save(any());
    }

    @Test
    void processContribution_ShouldHandleStrategyCalculationError() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        Jackpot jackpot = createTestJackpot(1L);
        
        when(contributionRepository.findByBetId(betRequest.getBetId())).thenReturn(Optional.empty());
        when(jackpotService.findById(betRequest.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getContributionStrategy(jackpot.getContributionType())).thenReturn(contributionStrategy);
        when(contributionStrategy.calculateContribution(betRequest.getBetAmount(), jackpot))
                .thenThrow(new RuntimeException("Strategy calculation failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> contributionService.processContribution(betRequest));
        
        verify(contributionRepository).findByBetId(betRequest.getBetId());
        verify(jackpotService).findById(betRequest.getJackpotId());
        verify(contributionStrategy).calculateContribution(betRequest.getBetAmount(), jackpot);
        verify(contributionRepository, never()).save(any());
    }

    @Test
    void processContribution_ShouldHandleMultipleContributions() {
        // Given
        BetRequest betRequest1 = BetRequestGenerator.generateBetRequest();
        BetRequest betRequest2 = BetRequestGenerator.generateBetRequest();
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setCurrentPoolValue(new BigDecimal("1000.00"));
        BigDecimal contributionAmount = new BigDecimal("25.00");
        
        when(contributionRepository.findByBetId(anyString())).thenReturn(Optional.empty());
        when(jackpotService.findById(any())).thenReturn(jackpot);
        when(jackpotService.getContributionStrategy(any())).thenReturn(contributionStrategy);
        when(contributionStrategy.calculateContribution(any(), any())).thenReturn(contributionAmount);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(contributionRepository.save(any(JackpotContribution.class))).thenReturn(new JackpotContribution());

        // When
        contributionService.processContribution(betRequest1);
        contributionService.processContribution(betRequest2);

        // Then
        verify(contributionRepository, times(2)).save(any(JackpotContribution.class));
        verify(jackpotService, times(2)).save(any(Jackpot.class));
    }

    private Jackpot createTestJackpot(Long id) {
        Jackpot jackpot = new Jackpot();
        jackpot.setId(id);
        jackpot.setName("Test Jackpot");
        jackpot.setContributionType(ContributionType.FIXED);
        jackpot.setRewardType(RewardType.VARIABLE);
        jackpot.setInitialPoolValue(new BigDecimal("1000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("1000.00"));
        jackpot.setContributionPercentage(10.0);
        jackpot.setRewardChancePercentage(5.0);
        jackpot.setCreatedAt(LocalDateTime.now());
        jackpot.setUpdatedAt(LocalDateTime.now());
        return jackpot;
    }
}
