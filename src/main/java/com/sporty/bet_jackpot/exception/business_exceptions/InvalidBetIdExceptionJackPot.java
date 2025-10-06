package com.sporty.bet_jackpot.exception.business_exceptions;

import com.sporty.bet_jackpot.exception.JackPotBusinessException;

/**
 * Exception thrown when a bet ID is invalid or already exists in the system.
 */
public class InvalidBetIdExceptionJackPot extends JackPotBusinessException {
    
    public InvalidBetIdExceptionJackPot(String betId) {
        super("Invalid or duplicate bet ID: " + betId);
    }
}