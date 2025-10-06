package com.sporty.bet_jackpot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BetRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBetRequest {
    String message() default "Invalid bet request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
