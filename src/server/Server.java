package server;

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is running... Waiting for players.");

            char[][] board1 = createBoard();
            char[][] board2 = createBoard();

            Socket player1 = serverSocket.accept();
            System.out.println("Player 1 connected.");
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            out1.println("Waiting for Player 2...");

            Socket player2 = serverSocket.accept();
            System.out.println("Player 2 connected.");
            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

            out1.println("Player 2 connected!");
            out2.println("Connected to server!");

            boolean p1Ready = false, p2Ready = false;
            out1.println("Lütfen gemilerinizi yerleştirip '✔ Hazırım' butonuna basın.");
            out2.println("Lütfen gemilerinizi yerleştirip '✔ Hazırım' butonuna basın.");

            while (!(p1Ready && p2Ready)) {
                if (!p1Ready && in1.ready()) {
                    String msg = in1.readLine();
                    if (msg != null && msg.startsWith("READY:")) {
                        String[] parts = msg.substring(6).split(",");
                        for (String coord : parts) {
                            int row = coord.toUpperCase().charAt(0) - 'A';
                            int col = Integer.parseInt(coord.substring(1)) - 1;
                            board1[row][col] = 'S';
                        }
                        p1Ready = true;
                        out1.println("Hazır oldunuz, rakip bekleniyor...");
                        out2.println("Rakibiniz hazır. Lütfen siz de hazır olun.");
                    }
                }


                if (!p2Ready && in2.ready()) {
                    String msg = in2.readLine();
                    if (msg != null && msg.startsWith("READY:")) {
                        String[] parts = msg.substring(6).split(",");
                        for (String coord : parts) {
                            int row = coord.toUpperCase().charAt(0) - 'A';
                            int col = Integer.parseInt(coord.substring(1)) - 1;
                            board2[row][col] = 'S';
                        }
                        p2Ready = true;
                        out2.println("Hazır oldunuz, rakip bekleniyor...");
                        out1.println("Rakibiniz hazır. Lütfen siz de hazır olun.");
                    }
                }


                Thread.sleep(100); // CPU dostu bekleme
            }


            // Başlangıç mesajları
            out1.println("Oyun başlıyor! İlk hamleyi yapabilirsiniz.");
            out2.println("Oyun başlıyor! Rakibinizin hamlesini bekleyin.");

            boolean isPlayer1Turn = true;

            while (true) {
                if (isPlayer1Turn) {
                    out1.println("Your turn! Enter coordinate (e.g. B4): ");
                    String msg = in1.readLine();
                    if (msg == null || msg.equalsIgnoreCase("exit")) break;

                    String result = handleShot(board2, msg);
                    out1.println("You fired at " + msg + ": " + result);
                    out2.println("Opponent fired at " + msg + ": " + result);

                    if (isAllShipsSunk(board2)) {
                        out1.println("🎉 You WON! All enemy ships sunk!");
                        out2.println("☠️ You LOST! All your ships were sunk.");
                        break;
                    }

                    if (!result.contains("💥")) {
                        isPlayer1Turn = false;
                    }

                } else {
                    out2.println("Your turn! Enter coordinate (e.g. B4): ");
                    String msg = in2.readLine();
                    if (msg == null || msg.equalsIgnoreCase("exit")) break;

                    String result = handleShot(board1, msg);
                    out2.println("You fired at " + msg + ": " + result);
                    out1.println("Opponent fired at " + msg + ": " + result);

                    if (isAllShipsSunk(board1)) {
                        out2.println("🎉 You WON! All enemy ships sunk!");
                        out1.println("☠️ You LOST! All your ships were sunk.");
                        break;
                    }

                    if (!result.contains("💥")) {
                        isPlayer1Turn = true;
                    }
                }
            }

            System.out.println("Game ended.");
            player1.close();
            player2.close();
            serverSocket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void placeShipsOnBoard(char[][] board, String coords) {
        String[] parts = coords.split(",");
        for (String coord : parts) {
            if (coord.length() < 2) continue;
            int row = coord.charAt(0) - 'A';
            int col = Integer.parseInt(coord.substring(1)) - 1;
            if (row >= 0 && row < 10 && col >= 0 && col < 10) {
                board[row][col] = 'S';
            }
        }
    }
    public static boolean isAllShipsSunk(char[][] board) {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                if (board[i][j] == 'S') return false;
        return true;
    }

    public static char[][] createBoard() {
        char[][] board = new char[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                board[i][j] = '-';
        return board;
    }

    public static String handleShot(char[][] board, String coord) {
    try {
        int row = coord.toUpperCase().charAt(0) - 'A';
        int col = Integer.parseInt(coord.substring(1)) - 1;

        if (row < 0 || row >= 10 || col < 0 || col >= 10)
            return "Invalid coordinate!";

        if (board[row][col] == 'S') {
            board[row][col] = 'X';
            return "💥 Vuruldu!";
        } else if (board[row][col] == '-') {
            board[row][col] = 'O';
            return "💨 Karavana!";
        } else if (board[row][col] == 'X' || board[row][col] == 'O') {
            return "Zaten hedef alındı!";
        } else {
            return "Hedef alınamadı!";
        }
    } catch (Exception e) {
        return "Geçersiz giriş!";
    }
}

}
