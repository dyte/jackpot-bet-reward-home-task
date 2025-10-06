package com.sporty.bet_jackpot.exception.business_exceptions;

import com.sporty.bet_jackpot.exception.JackPotBusinessException;

public class JackpotNotFoundException extends JackPotBusinessException {
    public JackpotNotFoundException(String message) {
        super(message);
    }
}