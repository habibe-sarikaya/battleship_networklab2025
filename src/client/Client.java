/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Habibe
 */

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
            Socket socket = new Socket("localhost", 5000);
            char[][] board = createBoard();
            new GameGUI(board, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
