package com.dataparser;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataparserApplication {

	public static void main(String[] args) throws MqttException {
		ConfigurableApplicationContext context = SpringApplication.run(DataparserApplication.class, args);

		DataListener dataListener = context.getBean(DataListener.class);
		dataListener.startListening(12345); // Specify the desired listening port here
	}
}
