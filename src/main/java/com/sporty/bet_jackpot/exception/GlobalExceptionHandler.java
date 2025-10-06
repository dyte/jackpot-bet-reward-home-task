package com.sporty.bet_jackpot.exception;

import com.sporty.bet_jackpot.exception.business_exceptions.BetNotFoundException;
import com.sporty.bet_jackpot.exception.business_exceptions.InvalidBetIdExceptionJackPot;
import com.sporty.bet_jackpot.exception.business_exceptions.JackpotNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(JackpotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJackpotNotFound(JackpotNotFoundException ex) {
        log.error("Jackpot not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(BetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBetNotFound(BetNotFoundException ex) {
        log.error("Bet not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(message));
    }

    @ExceptionHandler(JackPotBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(JackPotBusinessException ex) {
        log.error("Business exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidBetIdExceptionJackPot.class)
    public ResponseEntity<ErrorResponse> handleInvalidBetId(InvalidBetIdExceptionJackPot ex) {
        log.error("Invalid bet ID: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private String message;
    }
}
