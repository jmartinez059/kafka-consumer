package com.MNTDemo.Consumer.service;

import com.MNTDemo.Consumer.model.LogObject;
import com.MNTDemo.Consumer.model.LogType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = { "test_topic" }, partitions = 1, brokerProperties = { "listener=PLAINTEXT://localhost:9092", "port=9092" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConsumerIT {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerIT.class);

    @SpyBean
    private KafkaConsumerService kafkaConsumerServiceMock;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    Producer<String, LogObject> producer;
    private static final String TOPIC = "test_topic";

    @Captor
    ArgumentCaptor<String> timeStampCaptor;

    @BeforeAll
    public void setup() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<String, LogObject>(configs, new StringSerializer(), new JsonSerializer<>()).createProducer();
    }

    @AfterAll
    public void teardown() {
        if (producer != null) {
            producer.close();
        }
    }

    @Test
    public void kafkasListenerTest() {

        String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));

        LogObject logObject = LogObject.builder()
                .service("Test Service")
                .clazz("Test Class")
                .message("Test Message")
                .type(LogType.INFO)
                .timestamp(timestamp)
                .build();


        producer.send(new ProducerRecord<String, LogObject>(TOPIC, logObject));
        ObjectMapper objectMapper = new ObjectMapper();
        String logObjectJsonString = "";
        try {
            logObjectJsonString = objectMapper.writeValueAsString(logObject);
        } catch (JsonProcessingException e) {
            logger.error("KafkaProducerService caught exception while mapping logObject to json string:\n" + e.getStackTrace());
        }

        verify(kafkaConsumerServiceMock).consumeMessage(logObjectJsonString);
    }
}
