package com.dataparser;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        String data = "50450000C37FD3C0,282,6900000001000000000000649FA396";

        String uuid = "";
        int volume = 0;
        String packageData = "";

        String[] parts = data.split(",");
        if (parts.length >= 3) {
            uuid = parts[0];
            volume = Integer.parseInt(parts[1]);
            packageData = parts[2];
        }

        Map<String, Object> telemetryData = new HashMap<>();
        telemetryData.put("ts", byteArrayToUInt32(hexStringToByteArray(packageData), 11)); //

        Map<String, Object> values = new HashMap<>();

        parsePackageData(uuid, volume, packageData, values);

        telemetryData.put("values", values);

        System.out.println(telemetryData.toString());
    }

    private static void parsePackageData(String uuid, int volume, String packageData, Map<String, Object> values) {
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

    private static int byteArrayToUInt32(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & 0xFF) << 24) | ((bytes[startIndex + 1] & 0xFF) << 16) |
                ((bytes[startIndex + 2] & 0xFF) << 8) | (bytes[startIndex + 3] & 0xFF);
    }

    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private static int byteArrayToUInt16(byte[] bytes, int startIndex) {
        return ((bytes[startIndex] & 0xFF) << 8) | (bytes[startIndex + 1] & 0xFF);
    }

}
