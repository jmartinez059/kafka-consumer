package com.MNTDemo.Consumer.service;

import com.MNTDemo.Consumer.model.LogObject;
import com.MNTDemo.Consumer.repository.LogRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private LogRepository logRepository;

    Gson gson = new Gson();

    @KafkaListener(topics = "Logging_Topic", groupId = "group_id")
    public void consumeMessage(String logObjectJsonString) {
        logger.info(String.format("consumeMessage: %s", logObjectJsonString));
        logger.info("Saving log object to database");
        LogObject logObject = gson.fromJson(logObjectJsonString, LogObject.class);
        logRepository.save(logObject);
        logger.info("Successfully saved log object to database");
    }
}