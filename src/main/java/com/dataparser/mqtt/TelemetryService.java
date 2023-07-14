package com.dataparser.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelemetryService {

    private static final String THINGSBOARD_HOST = "localhost";
    private static final int THINGSBOARD_PORT = 1883;
    private static final String DEVICE_ACCESS_TOKEN = "dZcPL0QwCYdz2t7Wwvh5";
    private static final String DEVICE_TELEMETRY_TOPIC = "v1/devices/me/telemetry";

    public void sendTelemetry(String data) throws MqttException {
        String uuid = "";
        int volume = 0;
        String packageData = "";

        String[] parts = data.split(",");
        if (parts.length >= 3) {
            uuid = parts[0];
            volume = Integer.parseInt(parts[1]);
            packageData = parts[2];
        }

        String serverUri = "tcp://" + THINGSBOARD_HOST + ":" + THINGSBOARD_PORT;
        MqttClient client = new MqttClient(serverUri, MqttClient.generateClientId());

        Map<String, Object> telemetryData = new HashMap<>();
        telemetryData.put("uuid", uuid);
        telemetryData.put("volume", volume);

        // Parse packageData and add each part as telemetry
        parsePackageData(packageData, telemetryData);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(DEVICE_ACCESS_TOKEN);

        client.connect(options);

        MqttMessage message = new MqttMessage();
        message.setPayload(telemetryData.toString().getBytes());
        client.publish(DEVICE_TELEMETRY_TOPIC, message);

        client.disconnect();
    }

    // Parse and extract the relevant information from the package data
    private void parsePackageData(String packageData, Map<String, Object> telemetryData) {
        if (packageData.length() == 30) {
            byte[] bytes = hexStringToByteArray(packageData);
            //System.out.println(bytes.length);

            // Extract information from the bytes and add as telemetry
            int absoluteValue = byteArrayToUInt32(bytes, 1);
            telemetryData.put("absoluteValue", absoluteValue);
            //System.out.println("Absolute Value: " + absoluteValue);

            int reverseFlowCounter = byteArrayToUInt16(bytes, 5);
            telemetryData.put("reverseFlowCounter", reverseFlowCounter);
            //System.out.println("Reverse Flow Counter: " + reverseFlowCounter);

            int kIdx = bytes[7];
            telemetryData.put("kIdx", kIdx);
            //System.out.println("K idx: " + kIdx);

            int alarmFlags = bytes[8];
            telemetryData.put("alarmFlags", alarmFlags);
            //System.out.println("Alarm Flags: " + alarmFlags);

            int batteryVoltage = (bytes[9] << 8) | bytes[10];
            telemetryData.put("batteryVoltage", batteryVoltage);
            //System.out.println("Battery Voltage: " + batteryVoltage);

            int timestamp = byteArrayToUInt32(bytes, 11);
            telemetryData.put("timestamp", timestamp);
            //System.out.println("Timestamp: " + timestamp);
        }
    }

    // Helper method to convert a hex string to a byte array
    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    // Helper method to convert a byte array to an unsigned 16-bit integer
    private int byteArrayToUInt16(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & 0xFF) << 8) | (bytes[startIndex + 1] & 0xFF);
    }

    // Helper method to convert a byte array to an unsigned 32-bit integer
    private int byteArrayToUInt32(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & 0xFF) << 24) | ((bytes[startIndex + 1] & 0xFF) << 16) |
                ((bytes[startIndex + 2] & 0xFF) << 8) | (bytes[startIndex + 3] & 0xFF);
    }

}
