package com.sporty.bet_jackpot.exception.business_exceptions;

import com.sporty.bet_jackpot.exception.JackPotBusinessException;

public class BetNotFoundException extends JackPotBusinessException {
    public BetNotFoundException(String message) {
        super(message);
    }
}