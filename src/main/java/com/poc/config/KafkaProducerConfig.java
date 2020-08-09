/**
 * 
 */
package com.poc.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
/**
 * 
 * Configuration class for Kafka producer, sending the store time updates to Kafka Cluster
 * @author ckulkar
 * 
 */
@Configuration
public class KafkaProducerConfig {

	private static final String ACKS_ALL = "all";
	private static final String IO_CONFLUENT_MONITORING_PRODUCER_INTERCEPTOR = "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor";
	private static final String JKS = "JKS";
	private static final String SSL_TRUSTSTORE_TYPE = "ssl.truststore.type";
	private static final String SSL_TRUSTSTORE_PASSWORD = "ssl.truststore.password";
	private static final String SSL_TRUSTSTORE_LOCATION = "ssl.truststore.location";
	private static final String SSL = "SSL";
	private static final String SECURITY_PROTOCOL = "security.protocol";
	private static final String CONFLUENT_MONITORING_INTERCEPTOR_SSL_TRUSTSTORE_PASSWORD = "confluent.monitoring.interceptor.ssl.truststore.password";
	private static final String CONFLUENT_MONITORING_INTERCEPTOR_SSL_TRUSTSTORE_LOCATION = "confluent.monitoring.interceptor.ssl.truststore.location";
	private static final String CONFLUENT_MONITORING_INTERCEPTOR_SECURITY_PROTOCOL = "confluent.monitoring.interceptor.security.protocol";
	private static final String CONFLUENT_MONITORING_INTERCEPTOR_BOOTSTRAP_SERVERS = "confluent.monitoring.interceptor.bootstrap.servers";

	/**
	 * Kafka Brokers list
	 */
	@Value("${kafka.bootstrap-servers}")
	private String KAFKA_BROKER;

	/**
	 * Location of the trust store
	 */
	@Value("${ssl.truststore.location}")
	private String TRUSTSTORE_LOCATION;

	/**
	 * Password to unlock truststore
	 */
	@Value("${ssl.truststore.password}")
	private String TRUSTSTORE_PASSWORD;
	
	@Value("${spring.profiles.active}")
	private String springProfile;

	/**
	 * Sets the configurations necessary to bootstrap kafka producer.
	 * 
	 * @return ProducerFactory after setting necessary cofigs for SSL and
	 *         Serialization/De-serilization etc
	 */
	@Bean
	public ProducerFactory<String, ?> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		System.out.println("Bootsrtap servers " + KAFKA_BROKER);
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
//		IntegerSerializer.class
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put(ProducerConfig.ACKS_CONFIG, ACKS_ALL);
		config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
		config.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		if(!springProfile.equalsIgnoreCase("local")) {
			enableSSLConfig(config);
		}
		return new DefaultKafkaProducerFactory<>(config);
	}

	/**
	 * Template created using the config params
	 * @return KafkaTemplate used to send messages to bootstraped servers
	 */
	@Bean
	public KafkaTemplate<String, ?> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	/**
	 * This method sets the additional params needed for HTTPS connection using JKS
	 * 
	 * @param config
	 */
	public void enableSSLConfig(Map<String, Object> config) {
		config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, IO_CONFLUENT_MONITORING_PRODUCER_INTERCEPTOR);
		/* INTERCEPTOR CONFIG */
		config.put(CONFLUENT_MONITORING_INTERCEPTOR_BOOTSTRAP_SERVERS, KAFKA_BROKER);
		config.put(CONFLUENT_MONITORING_INTERCEPTOR_SECURITY_PROTOCOL, SSL);
		config.put(CONFLUENT_MONITORING_INTERCEPTOR_SSL_TRUSTSTORE_LOCATION, TRUSTSTORE_LOCATION);
		config.put(CONFLUENT_MONITORING_INTERCEPTOR_SSL_TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSWORD);
		config.put(SECURITY_PROTOCOL, SSL);
		/* SSL CONFIG */
		config.put(SSL_TRUSTSTORE_LOCATION, TRUSTSTORE_LOCATION);
		config.put(SSL_TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSWORD);
		config.put(SSL_TRUSTSTORE_TYPE, JKS);
	}

}
