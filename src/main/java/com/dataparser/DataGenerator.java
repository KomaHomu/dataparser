package com.dataparser;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DataGenerator {

    private static final String UUID = "50450000C37FD3C0";
    private static final String SERVER_ADDRESS = "localhost"; // Change to the server IP where the server is running
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String data = generateRandomData();
                sendDataToServer(data);
                System.out.println("Data sent to server successfully!");
            }
        }, 0, 2000); // Generate data every 2 seconds (2000 milliseconds)
    }

    private static String generateRandomData() {
        Random random = new Random();
        int volume = random.nextInt(100); // Generate a random volume between 0 and 4294967295
        String packageData = generateRandomPackageData();

        return UUID + "," + volume + "," + packageData;
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

    private static void sendDataToServer(String data) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


