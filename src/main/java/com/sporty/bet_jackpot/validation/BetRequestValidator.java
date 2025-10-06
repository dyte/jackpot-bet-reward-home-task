package com.sporty.bet_jackpot.validation;

import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.service.JackpotService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BetRequestValidator implements ConstraintValidator<ValidBetRequest, BetRequest> {

    private final JackpotService jackpotService;

    @Override
    public void initialize(ValidBetRequest constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(BetRequest betRequest, ConstraintValidatorContext context) {
        if (betRequest == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = true;

        // Business rule: Check if jackpot exists
        try {
            jackpotService.findById(betRequest.getJackpotId());
        } catch (Exception e) {
            if (context != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Jackpot with ID " + betRequest.getJackpotId() + " does not exist")
                        .addPropertyNode("jackpotId")
                        .addConstraintViolation();
            }
            isValid = false;
        }

        // Business rule: Minimum bet amount based on jackpot type
        if (betRequest.getBetAmount() != null && isValid) {
            try {
                var jackpot = jackpotService.findById(betRequest.getJackpotId());
                BigDecimal minimumBet = getMinimumBetForJackpot(jackpot);

                if (betRequest.getBetAmount().compareTo(minimumBet) < 0) {
                    if (context != null) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate("Minimum bet amount for this jackpot is " + minimumBet)
                                .addPropertyNode("betAmount")
                                .addConstraintViolation();
                    }
                    isValid = false;
                }
            } catch (Exception e) {
                // Jackpot validation already handled above
            }
        }

        return isValid;
    }

    private BigDecimal getMinimumBetForJackpot(com.sporty.bet_jackpot.model.Jackpot jackpot) {
        // Business rule: Different minimum bets based on jackpot type
        return switch (jackpot.getContributionType()) {
            case FIXED -> BigDecimal.valueOf(1.00); // $1 minimum for fixed jackpots
            case VARIABLE -> BigDecimal.valueOf(5.00); // $5 minimum for variable jackpots
        };
    }
}
