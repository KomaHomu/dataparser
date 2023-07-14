package com.dataparser;

import com.dataparser.http.HttpTelemetryPusher;
import com.dataparser.mqtt.TelemetryService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataparserApplication {

	public static void main(String[] args) throws MqttException {
//		HttpTelemetryPusher httpTelemetryPusher = new HttpTelemetryPusher();
//		httpTelemetryPusher.sendTelemetry("dZcPL0QwCYdz2t7Wwvh5", "25");

		TelemetryService telemetryService = new TelemetryService();
		String telemetryData = "50450000C37FD3C0,282,6900000001000000000000649FA396";
		telemetryService.sendTelemetry(telemetryData);

		SpringApplication.run(DataparserApplication.class, args);
	}

}
