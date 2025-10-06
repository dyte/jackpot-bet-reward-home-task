package com.sporty.bet_jackpot.exception;

/**
 * Base exception class for all business-related exceptions in the BetJackpot application.
 */
public class JackPotBusinessException extends RuntimeException {
    
    public JackPotBusinessException(String message) {
        // Customizable to handle business exceptions.
        super("Custom JackPot Exception:" + message);
    }
    
    public JackPotBusinessException(String message, Throwable cause) {
        // Customizable to handle business exceptions.
        super("Custom JackPot Exception: " + message, cause);
    }
}