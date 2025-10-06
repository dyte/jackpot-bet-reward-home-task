package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.util.BetRequestGenerator;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled("Disabled until Kafka is up and running")
class BetProducerServiceTest {

    @Mock
    private KafkaTemplate<String, BetRequest> kafkaTemplate;

    private BetProducerService betProducerService;

    @BeforeEach
    void setUp() {
        betProducerService = new BetProducerService(kafkaTemplate);
        ReflectionTestUtils.setField(betProducerService, "topic", "test-topic");
    }

    @Test
    void publishBetAsynchronously_ShouldSendMessageToKafka() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        RecordMetadata metadata = new RecordMetadata(new TopicPartition("test-topic", 0), 0L, 0, 0L, 0, 0);
        SendResult<String, BetRequest> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, BetRequest>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetAsynchronously(betRequest);

        // Then
        verify(kafkaTemplate).send("test-topic", betRequest.getBetId(), betRequest);
    }

    @Test
    void publishBetAsynchronously_ShouldHandleSuccessCallback() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        RecordMetadata metadata = new RecordMetadata(new TopicPartition("test-topic", 0), 0L, 0, 0L, 0, 0);
        SendResult<String, BetRequest> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, BetRequest>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetAsynchronously(betRequest);

        // Then
        verify(kafkaTemplate).send("test-topic", betRequest.getBetId(), betRequest);
    }

    @Test
    void publishBetAsynchronously_ShouldHandleFailureCallback() {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        RuntimeException exception = new RuntimeException("Kafka error");
        CompletableFuture<SendResult<String, BetRequest>> future = CompletableFuture.failedFuture(exception);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetAsynchronously(betRequest);

        // Then
        verify(kafkaTemplate).send("test-topic", betRequest.getBetId(), betRequest);
    }

    @Test
    void publishBetSynchronously_ShouldSendMessageAndReturnResult() throws ExecutionException, InterruptedException {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        RecordMetadata metadata = new RecordMetadata(new TopicPartition("test-topic", 0), 0L, 0, 0L, 0, 0);
        SendResult<String, BetRequest> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, BetRequest>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetSynchronously(betRequest);

        // Then
        verify(kafkaTemplate).send("test-topic", betRequest.getBetId(), betRequest);
    }

    @Test
    void publishBetSynchronously_ShouldHandleInterruptedException() throws ExecutionException, InterruptedException {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        CompletableFuture<SendResult<String, BetRequest>> future = new CompletableFuture<>();
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetSynchronously(betRequest);
        
        // Simulate InterruptedException
        future.completeExceptionally(new InterruptedException("Interrupted"));

        // Then
        verify(kafkaTemplate).send("test-topic", betRequest.getBetId(), betRequest);
    }

    @Test
    void publishBetSynchronously_ShouldHandleExecutionException() throws ExecutionException, InterruptedException {
        // Given
        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        CompletableFuture<SendResult<String, BetRequest>> future = new CompletableFuture<>();
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetSynchronously(betRequest);
        
        // Simulate ExecutionException
        future.completeExceptionally(new ExecutionException("Execution failed", new RuntimeException()));

        // Then
        verify(kafkaTemplate).send("test-topic", betRequest.getBetId(), betRequest);
    }

    @Test
    void publishBetAsynchronously_ShouldUseCorrectTopicAndKey() {
        // Given
        BetRequest betRequest = new BetRequest("test-bet-123", "user-456", 1L, new BigDecimal("100.00"));
        CompletableFuture<SendResult<String, BetRequest>> future = new CompletableFuture<>();
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetAsynchronously(betRequest);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BetRequest> valueCaptor = ArgumentCaptor.forClass(BetRequest.class);
        
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());
        
        assertEquals("test-topic", topicCaptor.getValue());
        assertEquals("test-bet-123", keyCaptor.getValue());
        assertEquals(betRequest, valueCaptor.getValue());
    }

    @Test
    void publishBetSynchronously_ShouldUseCorrectTopicAndKey() throws ExecutionException, InterruptedException {
        // Given
        BetRequest betRequest = new BetRequest("test-bet-456", "user-789", 2L, new BigDecimal("200.00"));
        RecordMetadata metadata = new RecordMetadata(new TopicPartition("test-topic", 1), 0, 0, 0, 0L, 0, 0);
        SendResult<String, BetRequest> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, BetRequest>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(BetRequest.class)))
                .thenReturn(future);

        // When
        betProducerService.publishBetSynchronously(betRequest);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BetRequest> valueCaptor = ArgumentCaptor.forClass(BetRequest.class);
        
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());
        
        assertEquals("test-topic", topicCaptor.getValue());
        assertEquals("test-bet-456", keyCaptor.getValue());
        assertEquals(betRequest, valueCaptor.getValue());
    }
}
