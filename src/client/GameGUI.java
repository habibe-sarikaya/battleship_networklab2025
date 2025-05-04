/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author Habibe
 */

import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {

    private JPanel playerBoard;
    private JPanel targetBoard;

    public GameGUI() {
        setTitle("Battleship - Game Board");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın ortasında aç

        // Oyun tahtalarını oluştur
        playerBoard = createBoard("Player Board");
        targetBoard = createBoard("Target Board");

        // Ana panele yerleştir
        setLayout(new BorderLayout());
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2));
        boardsPanel.add(playerBoard);
        boardsPanel.add(targetBoard);
        add(boardsPanel, BorderLayout.CENTER);

        // Alt kontrol butonları
        JPanel controlPanel = new JPanel();
        JButton rotateButton = new JButton("Rotate Ship");
        JButton readyButton = new JButton("Ready");
        controlPanel.add(rotateButton);
        controlPanel.add(readyButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createBoard(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 10)); // 10x10 kare

        for (int i = 0; i < 100; i++) {
            JButton button = new JButton();
            panel.add(button);
        }

        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gui = new GameGUI();
            gui.setVisible(true);
        });
    }
}
