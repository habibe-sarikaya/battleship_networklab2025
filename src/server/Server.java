/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

/**
 *
 * @author Habibe
 */


import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        final int PORT = 5000; // Sabit port numarası
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🔌 Sunucu başlatıldı, client'lar bekleniyor...");

            // 1. Client'ı kabul et
            Socket client1 = serverSocket.accept();
            System.out.println("🧍 Client 1 bağlandı: " + client1.getInetAddress());

            // 2. Client'ı kabul et
            Socket client2 = serverSocket.accept();
            System.out.println("🧍 Client 2 bağlandı: " + client2.getInetAddress());

            // I/O akışlarını oluştur
            BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);

            BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);

            out1.println("🎮 Oyuna hoş geldin! Sen 1. oyuncusun.");
            out2.println("🎮 Oyuna hoş geldin! Sen 2. oyuncusun.");

            // Sıralı mesajlaşma döngüsü
            while (true) {
                String msg1 = in1.readLine();
                if (msg1 == null || msg1.equalsIgnoreCase("exit")) break;
                out2.println("1. oyuncu: " + msg1);

                String msg2 = in2.readLine();
                if (msg2 == null || msg2.equalsIgnoreCase("exit")) break;
                out1.println("2. oyuncu: " + msg2);
            }

            // Kapatmalar
            client1.close();
            client2.close();
            System.out.println("🔚 Oyun sona erdi, bağlantılar kapatıldı.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

