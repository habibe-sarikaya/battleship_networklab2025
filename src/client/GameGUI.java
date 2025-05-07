package client;

import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {

    private JPanel playerBoard;
    private JPanel targetBoard;

    private String selectedShip = "Destroyer";
    private String orientation = "HORIZONTAL";

    private JComboBox<String> shipSelector;
    private JButton rotateButton;

    public GameGUI() {
        setTitle("Battleship - Game Board");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranı ortala

        // Tahta panelleri
        playerBoard = createBoard("Player Board");
        targetBoard = createBoard("Target Board");

        // Ana layout
        setLayout(new BorderLayout());
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2));
        boardsPanel.add(playerBoard);
        boardsPanel.add(targetBoard);
        add(boardsPanel, BorderLayout.CENTER);

        // Buton paneli
        JPanel controlPanel = new JPanel();

        // Gemi seçim menüsü
        String[] ships = {"Destroyer", "Submarine", "Cruiser", "Battleship", "Aircraft Carrier"};
        shipSelector = new JComboBox<>(ships);
        shipSelector.addActionListener(e -> {
            selectedShip = (String) shipSelector.getSelectedItem();
        });
        controlPanel.add(shipSelector);

        // Yön değiştirme butonu
        rotateButton = new JButton("Rotate: HORIZONTAL");
        rotateButton.addActionListener(e -> {
            orientation = orientation.equals("HORIZONTAL") ? "VERTICAL" : "HORIZONTAL";
            rotateButton.setText("Rotate: " + orientation);
        });
        controlPanel.add(rotateButton);

        // Hazır butonu (henüz bir işlevi yok, 6. aşamada eklenecek)
        JButton readyButton = new JButton("Ready");
        controlPanel.add(readyButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createBoard(String title) {
        JPanel panel = new JPanel(new GridLayout(10, 10));

        for (int i = 0; i < 100; i++) {
            JButton button = new JButton();
            int row = i / 10;
            int col = i % 10;

            if (title.equals("Player Board")) {
                button.addActionListener(e -> {
                    placeShip(row, col);
                });
            }

            panel.add(button);
        }

        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void placeShip(int row, int col) {
        int size = switch (selectedShip) {
            case "Destroyer" -> 2;
            case "Submarine" -> 3;
            case "Cruiser" -> 3;
            case "Battleship" -> 4;
            case "Aircraft Carrier" -> 5;
            default -> 0;
        };

        Component[] components = playerBoard.getComponents();

        // Tahtadan taşmayı engelle
        if (orientation.equals("HORIZONTAL") && col + size > 10) return;
        if (orientation.equals("VERTICAL") && row + size > 10) return;

        // Gemi yerleştir
        for (int i = 0; i < size; i++) {
            int index = orientation.equals("HORIZONTAL")
                    ? (row * 10 + col + i)
                    : ((row + i) * 10 + col);

            JButton cell = (JButton) components[index];
            cell.setBackground(Color.GRAY);
        }

        System.out.println("Yerleştirildi: " + selectedShip + " (" + orientation + ")");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gui = new GameGUI();
            gui.setVisible(true);
        });
    }
}
