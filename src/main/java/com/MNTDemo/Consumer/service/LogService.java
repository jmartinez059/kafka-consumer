package com.MNTDemo.Consumer.service;

import com.MNTDemo.Consumer.model.LogObject;
import com.MNTDemo.Consumer.model.LogType;
import com.MNTDemo.Consumer.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private LogRepository logRepository;

    public List<LogObject> getLogs() {
        logger.info("LogService called, making call to LogRepository");
        List<LogObject> logs = new ArrayList<>();
        try {
             logs = logRepository.findAll();
             logger.info("Response from logRepository:\n" + logs);
        } catch (Exception e) {
            logger.error("LogService caught exception calling LogRepository.getLogs():\n", e.getMessage());
        }
        logger.info("Received response from LogRepository, returning logs to LogController");
        return logs;
    }

    public List<LogObject> getLogsByService(String service) {
        logger.info("LogService called, making call to LogRepository");
        List<LogObject> logs = new ArrayList<>();
        try {
            logs = logRepository.findByService(service);
        } catch (Exception e) {
            logger.error("LogService caught exception calling LogRepository.getLogs():\n", e.getStackTrace());
            e.printStackTrace();
        }
        logger.info("Received response from LogRepository, returning logs to LogController");
        return logs;
    }

    public List<LogObject> getLogsByType(LogType logType) {
        logger.info("LogService called, making call to LogRepository");
        List<LogObject> logs = new ArrayList<>();
        try {
            logs = logRepository.findByType(logType);
        } catch (Exception e) {
            logger.error("LogService caught exception calling LogRepository.getLogs():\n", e.getMessage());
        }
        logger.info("Received response from LogRepository, returning logs to LogController");
        return logs;
    }
}
