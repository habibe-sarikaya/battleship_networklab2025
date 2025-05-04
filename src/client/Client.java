/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author Habibe
 */

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        final String SERVER_IP = "127.0.0.1"; // Aynı makinede test için localhost
        final int SERVER_PORT = 5000;

        try (
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("🌐 Sunucuya bağlanıldı!");

            // Sunucudan gelen ilk mesajı yazdır
            System.out.println(input.readLine());

            // Mesaj gönderme/dinleme döngüsü
            String userInput;
            while (true) {
                System.out.print("Mesaj (çıkmak için 'exit'): ");
                userInput = console.readLine();
                output.println(userInput);

                if (userInput.equalsIgnoreCase("exit")) break;

                // Sunucudan gelen yanıt
                String response = input.readLine();
                if (response == null) break;
                System.out.println("🖥️ Sunucu: " + response);
            }

            System.out.println("🔌 Bağlantı sonlandırıldı.");

        } catch (IOException e) {
            System.out.println("❌ Sunucuya bağlanılamadı: " + e.getMessage());
        }
    }
}
