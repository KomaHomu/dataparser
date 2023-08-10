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
    private static final String DEVICE_ACCESS_TOKEN = "yXI9X2Yo0QEUg81ZNwzH"; // change access token accordingly
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
        Long timestamp = 1000L * byteArrayToUInt32(hexStringToByteArray(packageData), 11);
        telemetryData.put("ts", timestamp); //

        Map<String, Object> values = new HashMap<>();

        parsePackageData(uuid, volume, packageData, values);

        telemetryData.put("values", values); //

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(DEVICE_ACCESS_TOKEN);

        client.connect(options);

        MqttMessage message = new MqttMessage();
        message.setPayload(telemetryData.toString().getBytes());
        client.publish(DEVICE_TELEMETRY_TOPIC, message);
        System.out.println(telemetryData.toString());

        client.disconnect();
    }

    private void parsePackageData(String uuid, int volume, String packageData, Map<String, Object> values) {
        values.put("uuid", uuid);
        values.put("volume", volume);

        if (packageData.length() == 30) {
            byte[] bytes = hexStringToByteArray(packageData);

            int absoluteValue = byteArrayToUInt32(bytes, 1);
            values.put("absoluteValue", absoluteValue);

            int reverseFlowCounter = byteArrayToUInt16(bytes, 5);
            values.put("reverseFlowCounter", reverseFlowCounter);

            int kIdx = bytes[7];
            values.put("kIdx", kIdx);

            int alarmFlags = bytes[8];
            values.put("alarmFlags", alarmFlags);

            int batteryVoltage = (bytes[9] << 8) | bytes[10];
            values.put("batteryVoltage", batteryVoltage);
        }
    }

    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private int byteArrayToUInt16(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & 0xFF) << 8) | (bytes[startIndex + 1] & 0xFF);
    }

    private int byteArrayToUInt32(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & 0xFF) << 24) | ((bytes[startIndex + 1] & 0xFF) << 16) |
                ((bytes[startIndex + 2] & 0xFF) << 8) | (bytes[startIndex + 3] & 0xFF);
    }
}