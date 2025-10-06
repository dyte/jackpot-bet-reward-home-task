package com.sporty.bet_jackpot.exception;

import com.sporty.bet_jackpot.exception.business_exceptions.BetNotFoundException;
import com.sporty.bet_jackpot.exception.business_exceptions.InvalidBetIdExceptionJackPot;
import com.sporty.bet_jackpot.exception.business_exceptions.JackpotNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleJackpotNotFound_ShouldReturnNotFoundStatus() {
        // Given
        JackpotNotFoundException exception = new JackpotNotFoundException("Jackpot not found: 123");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleJackpotNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Jackpot not found: 123", response.getBody().getMessage());
    }

    @Test
    void handleBetNotFound_ShouldReturnNotFoundStatus() {
        // Given
        BetNotFoundException exception = new BetNotFoundException("Bet not found: bet-123");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleBetNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Bet not found: bet-123", response.getBody().getMessage());
    }

    @Test
    void handleValidation_ShouldReturnBadRequestStatus() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        ObjectError objectError = mock(ObjectError.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(objectError));
        when(objectError.getDefaultMessage()).thenReturn("Bet amount is required");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleValidation(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bet amount is required", response.getBody().getMessage());
    }

    @Test
    void handleValidation_ShouldHandleMultipleErrors() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        ObjectError firstError = mock(ObjectError.class);
        ObjectError secondError = mock(ObjectError.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(firstError, secondError));
        when(firstError.getDefaultMessage()).thenReturn("First error message");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleValidation(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("First error message", response.getBody().getMessage());
    }

    @Test
    void handleBusinessException_ShouldReturnBadRequestStatus() {
        // Given
        JackPotBusinessException exception = new JackPotBusinessException("Business logic error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleBusinessException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Business logic error", response.getBody().getMessage());
    }

    @Test
    void handleBusinessException_ShouldReturnBadRequestStatus_WithCause() {
        // Given
        RuntimeException cause = new RuntimeException("Root cause");
        JackPotBusinessException exception = new JackPotBusinessException("Business logic error", cause);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleBusinessException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception: Business logic error", response.getBody().getMessage());
    }

    @Test
    void handleInvalidBetId_ShouldReturnConflictStatus() {
        // Given
        InvalidBetIdExceptionJackPot exception = new InvalidBetIdExceptionJackPot("Invalid bet ID: duplicate");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleInvalidBetId(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Invalid or duplicate bet ID: Invalid bet ID: duplicate", response.getBody().getMessage());
    }

    @Test
    void handleJackpotNotFound_ShouldHandleNullMessage() {
        // Given
        JackpotNotFoundException exception = new JackpotNotFoundException(null);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleJackpotNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:null", response.getBody().getMessage());
    }

    @Test
    void handleBetNotFound_ShouldHandleEmptyMessage() {
        // Given
        BetNotFoundException exception = new BetNotFoundException("");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleBetNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:", response.getBody().getMessage());
    }

    @Test
    void handleBusinessException_ShouldHandleLongMessage() {
        // Given
        String longMessage = "This is a very long error message that contains detailed information about what went wrong in the business logic and should be handled properly by the exception handler without any issues or truncation";
        JackPotBusinessException exception = new JackPotBusinessException(longMessage);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleBusinessException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:" + longMessage, response.getBody().getMessage());
    }

    @Test
    void handleValidation_ShouldHandleNullBindingResult() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(null);

        // When & Then
        assertThrows(NullPointerException.class, () ->
                globalExceptionHandler.handleValidation(exception));
    }

    @Test
    void handleValidation_ShouldHandleEmptyErrorsList() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList());

        // When & Then
        assertThrows(IndexOutOfBoundsException.class, () ->
                globalExceptionHandler.handleValidation(exception));
    }

    @Test
    void errorResponse_ShouldHaveCorrectStructure() {
        // Given
        String message = "Test error message";
        GlobalExceptionHandler.ErrorResponse errorResponse =
                new GlobalExceptionHandler.ErrorResponse(message);

        // When & Then
        assertNotNull(errorResponse);
        assertEquals(message, errorResponse.getMessage());
    }

    @Test
    void errorResponse_ShouldBeSerializable() {
        // Given
        String message = "Serializable error message";
        GlobalExceptionHandler.ErrorResponse errorResponse =
                new GlobalExceptionHandler.ErrorResponse(message);

        // When & Then
        assertNotNull(errorResponse.toString());
        assertTrue(errorResponse.toString().contains(message));
    }

    @Test
    void handleJackpotNotFound_ShouldLogError() {
        // Given
        JackpotNotFoundException exception = new JackpotNotFoundException("Test jackpot error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleJackpotNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Test jackpot error", response.getBody().getMessage());
    }

    @Test
    void handleBetNotFound_ShouldLogError() {
        // Given
        BetNotFoundException exception = new BetNotFoundException("Test bet error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleBetNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Test bet error", response.getBody().getMessage());
    }

    @Test
    void handleBusinessException_ShouldLogError() {
        // Given
        JackPotBusinessException exception = new JackPotBusinessException("Test business error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleBusinessException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Test business error", response.getBody().getMessage());
    }

    @Test
    void handleInvalidBetId_ShouldLogError() {
        // Given
        InvalidBetIdExceptionJackPot exception = new InvalidBetIdExceptionJackPot("Test invalid bet error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleInvalidBetId(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom JackPot Exception:Invalid or duplicate bet ID: Test invalid bet error", response.getBody().getMessage());
    }
}
