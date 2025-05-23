package client;

import java.io.*;
import java.net.*;

public class Client {

    public static char[][] createBoard() {
        char[][] board = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = '-';
            }
        }
        return board;
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("54.204.163.176", 5000);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            char[][] board = createBoard();

            GameGUI gui = new GameGUI(board, socket, input, output);
            gui.start();  // yeni oyun başlatır

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
