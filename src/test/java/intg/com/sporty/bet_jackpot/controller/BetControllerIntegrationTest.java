package com.sporty.bet_jackpot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.dto.BetResponse;
import com.sporty.bet_jackpot.util.BetRequestGenerator;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"jackpot-bets"})
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class BetControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        var configs = new HashMap<String, Object>(KafkaTestUtils.consumerProps("group-1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");

        consumer = new DefaultKafkaConsumerFactory<String, Object>(configs, new StringDeserializer(), jsonDeserializer)
                .createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "jackpot-bets");
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    private Consumer<String, Object> consumer;

    @Test
    void placeBet() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        BetRequest betRequest = BetRequestGenerator.generateBetRequest();
        var httpEntity = new HttpEntity<>(betRequest, httpHeaders);

        ResponseEntity<BetResponse> responseEntity = restTemplate
                .exchange("/api/bets/place-bet", HttpMethod.POST, httpEntity, BetResponse.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Bet accepted and sent for processing", responseEntity.getBody().getMessage());
        assertEquals("ACCEPTED", responseEntity.getBody().getStatus());

        ConsumerRecords<String, Object> records = KafkaTestUtils.getRecords(consumer);

        assertEquals(1, records.count());

        records.forEach(record -> {
            assertEquals(record.value(), betRequest);
        });
    }
}