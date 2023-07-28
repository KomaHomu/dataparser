package com.dataparser;

import com.dataparser.mqtt.TelemetryService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class DataListener {

    @Autowired
    private TelemetryService telemetryService;

    public void startListening(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port + " for user messages...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Received connection from: " + clientSocket.getInetAddress());

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = reader.readLine();

                System.out.println("Received message: " + message);

                // Process and send data to ThingsBoard using TelemetryService
                try {
                    telemetryService.sendTelemetry(message);
                    System.out.println("Telemetry data sent to ThingsBoard successfully!");
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
