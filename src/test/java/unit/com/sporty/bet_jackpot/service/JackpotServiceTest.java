package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.exception.business_exceptions.JackpotNotFoundException;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.repository.JackpotRepository;
import com.sporty.bet_jackpot.strategy.contribution.ContributionStrategy;
import com.sporty.bet_jackpot.strategy.contribution.FixedContributionStrategy;
import com.sporty.bet_jackpot.strategy.contribution.VariableContributionStrategy;
import com.sporty.bet_jackpot.strategy.reward.FixedRewardStrategy;
import com.sporty.bet_jackpot.strategy.reward.RewardStrategy;
import com.sporty.bet_jackpot.strategy.reward.VariableRewardStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JackpotServiceTest {

    @Mock
    private JackpotRepository jackpotRepository;

    @Mock
    private FixedContributionStrategy fixedContributionStrategy;

    @Mock
    private VariableContributionStrategy variableContributionStrategy;

    @Mock
    private FixedRewardStrategy fixedRewardStrategy;

    @Mock
    private VariableRewardStrategy variableRewardStrategy;

    private JackpotService jackpotService;

    @BeforeEach
    void setUp() {
        jackpotService = new JackpotService(
                jackpotRepository,
                fixedContributionStrategy,
                variableContributionStrategy,
                fixedRewardStrategy,
                variableRewardStrategy
        );
    }

    @Test
    void getContributionStrategy_ShouldReturnFixedStrategy_WhenFixedType() {
        // When
        ContributionStrategy strategy = jackpotService.getContributionStrategy(ContributionType.FIXED);

        // Then
        assertNotNull(strategy);
        assertEquals(fixedContributionStrategy, strategy);
    }

    @Test
    void getContributionStrategy_ShouldReturnVariableStrategy_WhenVariableType() {
        // When
        ContributionStrategy strategy = jackpotService.getContributionStrategy(ContributionType.VARIABLE);

        // Then
        assertNotNull(strategy);
        assertEquals(variableContributionStrategy, strategy);
    }

    @Test
    void getRewardStrategy_ShouldReturnFixedStrategy_WhenFixedType() {
        // When
        RewardStrategy strategy = jackpotService.getRewardStrategy(RewardType.FIXED);

        // Then
        assertNotNull(strategy);
        assertEquals(fixedRewardStrategy, strategy);
    }

    @Test
    void getRewardStrategy_ShouldReturnVariableStrategy_WhenVariableType() {
        // When
        RewardStrategy strategy = jackpotService.getRewardStrategy(RewardType.VARIABLE);

        // Then
        assertNotNull(strategy);
        assertEquals(variableRewardStrategy, strategy);
    }

    @Test
    void findById_ShouldReturnJackpot_WhenExists() {
        // Given
        Long jackpotId = 1L;
        Jackpot expectedJackpot = createTestJackpot(jackpotId);
        when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.of(expectedJackpot));

        // When
        Jackpot result = jackpotService.findById(jackpotId);

        // Then
        assertNotNull(result);
        assertEquals(expectedJackpot, result);
        assertEquals(jackpotId, result.getId());
        verify(jackpotRepository).findById(jackpotId);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        // Given
        Long jackpotId = 999L;
        when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.empty());

        // When & Then
        JackpotNotFoundException exception = assertThrows(
                JackpotNotFoundException.class,
                () -> jackpotService.findById(jackpotId)
        );

        assertTrue(exception.getMessage().contains("Jackpot not found"));
        verify(jackpotRepository).findById(jackpotId);
    }

    @Test
    void findById_ShouldThrowException_WhenNullId() {
        // Given
        Long jackpotId = null;
        when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.empty());

        // When & Then
        JackpotNotFoundException exception = assertThrows(
                JackpotNotFoundException.class,
                () -> jackpotService.findById(jackpotId)
        );

        assertTrue(exception.getMessage().contains("Jackpot not found"));
        verify(jackpotRepository).findById(jackpotId);
    }

    @Test
    void save_ShouldReturnSavedJackpot() {
        // Given
        Jackpot jackpot = createTestJackpot(1L);
        Jackpot savedJackpot = createTestJackpot(1L);
        when(jackpotRepository.save(any(Jackpot.class))).thenReturn(savedJackpot);

        // When
        Jackpot result = jackpotService.save(jackpot);

        // Then
        assertNotNull(result);
        assertEquals(savedJackpot, result);
        verify(jackpotRepository).save(jackpot);
    }

    @Test
    void save_ShouldHandleNullJackpot() {
        // Given
        Jackpot jackpot = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> jackpotService.save(jackpot));
    }

    @Test
    void save_ShouldHandleNewJackpot() {
        // Given
        Jackpot newJackpot = new Jackpot();
        newJackpot.setName("New Jackpot");
        newJackpot.setContributionType(ContributionType.FIXED);
        newJackpot.setRewardType(RewardType.VARIABLE);
        newJackpot.setInitialPoolValue(BigDecimal.valueOf(1000));
        newJackpot.setCurrentPoolValue(BigDecimal.valueOf(1000));

        Jackpot savedJackpot = new Jackpot();
        savedJackpot.setId(1L);
        savedJackpot.setName("New Jackpot");
        savedJackpot.setContributionType(ContributionType.FIXED);
        savedJackpot.setRewardType(RewardType.VARIABLE);
        savedJackpot.setInitialPoolValue(BigDecimal.valueOf(1000));
        savedJackpot.setCurrentPoolValue(BigDecimal.valueOf(1000));

        when(jackpotRepository.save(any(Jackpot.class))).thenReturn(savedJackpot);

        // When
        Jackpot result = jackpotService.save(newJackpot);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Jackpot", result.getName());
        verify(jackpotRepository).save(newJackpot);
    }

    @Test
    void getContributionStrategy_ShouldHandleAllContributionTypes() {
        // Test all contribution types
        for (ContributionType type : ContributionType.values()) {
            ContributionStrategy strategy = jackpotService.getContributionStrategy(type);
            assertNotNull(strategy);
            
            if (type == ContributionType.FIXED) {
                assertEquals(fixedContributionStrategy, strategy);
            } else if (type == ContributionType.VARIABLE) {
                assertEquals(variableContributionStrategy, strategy);
            }
        }
    }

    @Test
    void getRewardStrategy_ShouldHandleAllRewardTypes() {
        // Test all reward types
        for (RewardType type : RewardType.values()) {
            RewardStrategy strategy = jackpotService.getRewardStrategy(type);
            assertNotNull(strategy);
            
            if (type == RewardType.FIXED) {
                assertEquals(fixedRewardStrategy, strategy);
            } else if (type == RewardType.VARIABLE) {
                assertEquals(variableRewardStrategy, strategy);
            }
        }
    }

    private Jackpot createTestJackpot(Long id) {
        Jackpot jackpot = new Jackpot();
        jackpot.setId(id);
        jackpot.setName("Test Jackpot");
        jackpot.setContributionType(ContributionType.FIXED);
        jackpot.setRewardType(RewardType.VARIABLE);
        jackpot.setInitialPoolValue(BigDecimal.valueOf(1000));
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(1000));
        jackpot.setContributionPercentage(10.0);
        jackpot.setRewardChancePercentage(5.0);
        jackpot.setCreatedAt(LocalDateTime.now());
        jackpot.setUpdatedAt(LocalDateTime.now());
        return jackpot;
    }
}
