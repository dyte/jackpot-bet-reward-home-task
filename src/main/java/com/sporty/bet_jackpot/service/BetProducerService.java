package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.BetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetProducerService {
    
    private final KafkaTemplate<String, BetRequest> kafkaTemplate;
    
    @Value("${kafka.topic.jackpot-bets}")
    private String topic;
    
    public void publishBetAsynchronously(BetRequest betRequest) {
        log.info("Publishing bet to Kafka: {}", betRequest);
        var key = betRequest.getBetId();

        CompletableFuture<SendResult<String, BetRequest>> topicSend = kafkaTemplate.send(topic, key, betRequest);

        topicSend
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, betRequest, throwable);
                    } else {
                        handleSuccess(key, betRequest, sendResult);
                    }
                });
    }


    // This is an alternative approach to publishing bets "SYNCHRONOUSLY" in case it is required.
    public void publishBetSynchronously(BetRequest betRequest) {
        log.info("Publishing bet to Kafka synchronously: {}", betRequest);
        var key = betRequest.getBetId();


         try {
             SendResult<String, BetRequest> sendResult = kafkaTemplate.send(topic, key, betRequest).get();
             handleSuccess(key, betRequest, sendResult);
         } catch (InterruptedException | ExecutionException e) {
            handleFailure(key, betRequest, e);
         }
    }

    private void handleSuccess(String key, BetRequest betRequest, SendResult<String, BetRequest> sendResult) {
        // Handle success here, e.g., logging or storing the offset.
        log.info("Bet ID: {}, with value: {} sent to Kafka (partition: {}) successfully.", key, betRequest, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(String key, BetRequest betRequest, Throwable throwable) {
        // Handle any errors here, e.g., logging, retrying the sending,
        // or send to another retry/failure topic - producer or throwing an exception.
        log.error("Failed bet key: {}", key);
        log.error("Failed bet error: {}", throwable.getMessage());
        log.error("Error sending bet to Kafka: {}", betRequest, throwable);

    }
}