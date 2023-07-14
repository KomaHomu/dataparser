package com.dataparser;

import com.dataparser.mqtt.TelemetryService;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.*;

public class DataGenerator {

    private static final String UUID = "50450000C37FD3C0";

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TelemetryService telemetryService = new TelemetryService();
                String data = generateRandomData();
                try {
                    telemetryService.sendTelemetry(data);
                    System.out.println("Telemetry data sent successfully!");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000); // Generate data every 1 second (1000 milliseconds)
    }

    private static String generateRandomData() {
        Random random = new Random();
        int volume = random.nextInt(100); // Generate a random volume between 0 and 4294967295
        String packageData = generateRandomPackageData();

        return DataGenerator.UUID + "," + volume + "," + packageData;
    }

    private static String generateRandomPackageData() {
        Random random = new Random();
        byte[] packageDataBytes = new byte[15];
        random.nextBytes(packageDataBytes);
        StringBuilder packageDataBuilder = new StringBuilder();
        for (byte b : packageDataBytes) {
            packageDataBuilder.append(String.format("%02X", b));
        }
        return packageDataBuilder.toString();
    }
}
