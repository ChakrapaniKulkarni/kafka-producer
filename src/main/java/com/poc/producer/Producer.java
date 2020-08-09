/**
 * 
 */
package com.poc.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ckulkar
 *
 */
@Service
public class Producer {
	 private static final Logger logger = LoggerFactory.getLogger(Producer.class);
	    public static final String TOPIC = "test";

	    @Autowired
	    private KafkaTemplate<String, String> kafkaTemplate;

	    public void sendMessage(String message) {
	        logger.info(String.format("#### -> Producing message -> %s", message));
	        this.kafkaTemplate.send(TOPIC, message);
	    }
}
