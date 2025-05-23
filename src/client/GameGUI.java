package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;
import javax.swing.JOptionPane;


public class GameGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton[][] playerButtons = new JButton[10][10];
    private JButton[][] targetButtons = new JButton[10][10];
    private JLabel statusLabel;
    private Stack<PlacedShip> placedShips = new Stack<>();
    private Set<String> placedShipTypes = new HashSet<>();
    private PrintWriter out;
    private BufferedReader in;
    private boolean myTurn = false;
    private boolean gameStarted = false;

    private static class PlacedShip {
        int row, col, size;
        String type, direction;

        PlacedShip(int row, int col, int size, String type, String direction) {
            this.row = row;
            this.col = col;
            this.size = size;
            this.type = type;
            this.direction = direction;
        }
    }

    private char[][] board;
    private JComboBox<String> shipSelector;
    private JRadioButton horizontalL2R, horizontalR2L, verticalT2B, verticalB2T;
    private JButton undoButton, readyButton;

    public GameGUI(char[][] board, Socket socket, BufferedReader in, PrintWriter out) {
        this.board = board;
        this.out = out;
        this.in = in;
 

        setTitle("Battleship – Multiplayer Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setLayout(new BorderLayout());
        statusLabel = new JLabel("Oyun başlıyor...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);


        JPanel boardPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel playerPanel = new JPanel(new GridLayout(10, 10));
        JPanel targetPanel = new JPanel(new GridLayout(10, 10));
        playerPanel.setBorder(BorderFactory.createTitledBorder("Senin Tahtan"));
        targetPanel.setBorder(BorderFactory.createTitledBorder("Rakibin Tahtası (Tıklayarak Atış Yap)"));

        Dimension buttonSize = new Dimension(50, 50);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int row = i, col = j;

                JButton playerBtn = new JButton();
                playerBtn.setPreferredSize(buttonSize);
                playerBtn.setMargin(new Insets(0, 0, 0, 0));
                playerBtn.setBackground(Color.WHITE);
                playerButtons[i][j] = playerBtn;
                playerPanel.add(playerBtn);

                JButton targetBtn = new JButton();
                targetBtn.setPreferredSize(buttonSize);
                targetBtn.setMargin(new Insets(0, 0, 0, 0));
                targetBtn.setBackground(Color.WHITE);
                targetButtons[i][j] = targetBtn;
                targetPanel.add(targetBtn);

                targetBtn.addActionListener(e -> {
                    if (!gameStarted || !myTurn || targetBtn.getText().length() > 0) return;
                    String coordinate = (char) ('A' + row) + Integer.toString(col + 1);
                    out.println(coordinate);
                    targetBtn.setEnabled(false);
                    myTurn = false;
                });

                playerBtn.addActionListener(e -> placeShip(row, col));
            }
        }

        boardPanel.add(playerPanel);
        boardPanel.add(targetPanel);
        add(boardPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        String[] ships = {"Aircraft Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
        shipSelector = new JComboBox<>(ships);
        controls.add(shipSelector);

        horizontalL2R = new JRadioButton("Soldan Sağa");
        horizontalR2L = new JRadioButton("Sağdan Sola");
        verticalT2B = new JRadioButton("Yukarıdan Aşağı");
        verticalB2T = new JRadioButton("Aşağıdan Yukarı");

        ButtonGroup directionGroup = new ButtonGroup();
        directionGroup.add(horizontalL2R);
        directionGroup.add(horizontalR2L);
        directionGroup.add(verticalT2B);
        directionGroup.add(verticalB2T);
        horizontalL2R.setSelected(true);

        controls.add(horizontalL2R);
        controls.add(horizontalR2L);
        controls.add(verticalT2B);
        controls.add(verticalB2T);

        undoButton = new JButton("← Geri Al");
        undoButton.addActionListener(e -> undoLastPlacement());
        controls.add(undoButton);

        readyButton = new JButton("✔ Hazırım");
        readyButton.setEnabled(false);
        readyButton.addActionListener(e -> {
            List<String> coords = new ArrayList<>();
            for (PlacedShip ship : placedShips) {
                for (int i = 0; i < ship.size; i++) {
                    int r = ship.row;
                    int c = ship.col;
                    if (ship.direction.equals("H-L2R")) c += i;
                    if (ship.direction.equals("H-R2L")) c -= i;
                    if (ship.direction.equals("V-T2B")) r += i;
                    if (ship.direction.equals("V-B2T")) r -= i;
                    coords.add((char) ('A' + r) + Integer.toString(c + 1));
                }
            }
            String message = "READY:" + String.join(",", coords);
            out.println(message);

            readyButton.setEnabled(false);
            new Thread(this::gameLoop).start();
        });
        controls.add(readyButton);
        JButton surrenderButton = new JButton("🚩 Pes Et");
        surrenderButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Pes etmek istediğinize emin misiniz?\nOyun kaybedilecek.",
                "Pes Et",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                out.println("SURRENDER");
            }
        });
        controls.add(surrenderButton);


        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(controls, BorderLayout.SOUTH);
        statusLabel = new JLabel("Durum: Oyuna Hazır!");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);
        // Sohbet paneli
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Sohbet"));

        chatArea = new JTextArea(6, 30);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        chatInput = new JTextField();
        chatInput.addActionListener(e -> {
            String msg = chatInput.getText().trim();
            if (!msg.isEmpty()) {
                out.println("CHAT:" + msg);
                chatArea.append("Siz: " + msg + "\n");
                chatInput.setText("");
            }
        });

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.EAST); // sağ tarafa ekleniyor


        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void gameLoop() {
    try {
        while (true) {
            String line = in.readLine();

            if (line == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            System.out.println("DEBUG: Sunucudan gelen mesaj: " + line);

            // 💬 Chat mesajı kontrolü
            if (line.startsWith("CHAT:")) {
                String chatMsg = line.substring(5).trim();
                chatArea.append("Rakip: " + chatMsg + "\n");
                continue;
            }

            // 🔫 Atış sırası
            if (line.equals("Your turn! Enter coordinate (e.g. B4): ")) {
                myTurn = true;
                gameStarted = true;
                statusLabel.setText("🎯 Sıra sizde! Hedef tahtasından bir kareye tıklayın.");
                continue;
            }

            // ❗ Pes Et mesajı
            if (line.equals("SURRENDER")) {
                System.out.println("DEBUG: SURRENDER mesajı yakalandı, yoksayıldı.");
                continue;
            }

            // 💥 Kullanıcı atış yaptı
            if (line.startsWith("You fired at ")) {
                if (line.contains("SURRENDER")) {
                    System.out.println("DEBUG: 'You fired at SURRENDER' mesajı yoksayıldı.");
                    continue;
                }

                try {
                    String[] parts = line.split(" ");
                    String coord = parts[3].replace(":", "");
                    int row = coord.charAt(0) - 'A';
                    int col = Integer.parseInt(coord.substring(1)) - 1;
                    String result = line.substring(line.indexOf(":") + 2);

                    if (result.contains("💥")) {
                        targetButtons[row][col].setText("X");
                        targetButtons[row][col].setBackground(Color.RED);
                        statusLabel.setText("💥 Vuruldu!");
                    } else {
                        targetButtons[row][col].setText("O");
                        targetButtons[row][col].setBackground(Color.LIGHT_GRAY);
                        statusLabel.setText("💨 Karavana!");
                    }
                } catch (Exception ex) {
                    System.out.println("DEBUG: Koordinat işlenemedi – muhtemelen SURRENDER formatında.");
                    continue;
                }
                continue;
            }

            // 🎯 Rakip atış yaptı
            if (line.startsWith("Opponent fired at ")) {
                try {
                    String[] parts = line.split(" ");
                    String coord = parts[3].replace(":", "");
                    int row = coord.charAt(0) - 'A';
                    int col = Integer.parseInt(coord.substring(1)) - 1;
                    String result = line.substring(line.indexOf(":") + 2);

                    if (result.contains("💥")) {
                        playerButtons[row][col].setText("X");
                        playerButtons[row][col].setBackground(Color.RED);
                        statusLabel.setText("❗ Rakip vurdu! Konum: " + coord);
                    } else {
                        playerButtons[row][col].setText("O");
                        playerButtons[row][col].setBackground(Color.LIGHT_GRAY);
                        statusLabel.setText("✅ Rakip karavana attı! Konum: " + coord);
                    }
                } catch (Exception ex) {
                    System.out.println("DEBUG: Rakip atış koordinat işlenemedi.");
                    continue;
                }
                continue;
            }

            // 🏆 Kazandı
            if (line.contains("🎉 You WON")) {
                statusLabel.setText("🎉 Kazandınız! Tüm düşman gemileri batırıldı!");
                continue;
            }

            // ☠️ Kaybetti
            if (line.contains("☠️ You LOST") || line.contains("☠️ Oyunu bıraktınız")) {
                statusLabel.setText("☠️ Kaybettiniz! Tüm gemileriniz batırıldı.");
                continue;
            }

            // 🔁 Oyun bitti → Kullanıcıya seçenek sun
            if (line.equals("RESTART?")) {
                String resultMessage = statusLabel.getText();
                Object[] options = {"🔁 Yeniden Oyna", "❌ Çıkış"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        resultMessage + "\nYeniden oynamak ister misiniz?",
                        "Oyun Bitti",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice == JOptionPane.YES_OPTION) {
                    out.println("evet");
                    resetGameGUI();
                    new Thread(this::gameLoop).start();
                } else {
                    out.println("hayır");
                    System.exit(0);
                }
                continue;
            }

            // 🔔 Diğer tüm mesajlar
            statusLabel.setText(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}






    public void start() {
    resetGameGUI();  // GUI’yi sıfırla
    setVisible(true);
    new Thread(this::gameLoop).start();
}

    private void undoLastPlacement() {
        if (!placedShips.isEmpty()) {
            PlacedShip last = placedShips.pop();
            placedShipTypes.remove(last.type);
            for (int i = 0; i < last.size; i++) {
                int r = last.row;
                int c = last.col;
                if (last.direction.equals("H-L2R")) c += i;
                if (last.direction.equals("H-R2L")) c -= i;
                if (last.direction.equals("V-T2B")) r += i;
                if (last.direction.equals("V-B2T")) r -= i;
                if (r >= 0 && r < 10 && c >= 0 && c < 10) {
                    playerButtons[r][c].setIcon(null);
                    playerButtons[r][c].setText("");
                    playerButtons[r][c].setBackground(Color.WHITE);
                }
            }
            readyButton.setEnabled(false);
        }
    }

    private void placeShip(int row, int col) {
        String selectedShip = (String) shipSelector.getSelectedItem();
        if (placedShipTypes.contains(selectedShip)) {
            statusLabel.setText("Bu gemi zaten yerleştirildi!");
            return;
        }

        int shipSize = switch (selectedShip) {
            case "Aircraft Carrier" -> 5;
            case "Battleship" -> 4;
            case "Cruiser" -> 3;
            case "Submarine" -> 3;
            case "Destroyer" -> 2;
            default -> 1;
        };

        String imageName = switch (selectedShip) {
            case "Aircraft Carrier" -> "aircraft.png";
            case "Battleship" -> "battleship.png";
            case "Cruiser" -> "cruiser.png";
            case "Submarine" -> "submarine.png";
            case "Destroyer" -> "destroyer.png";
            default -> "";
        };

        String direction = horizontalL2R.isSelected() ? "H-L2R" :
                           horizontalR2L.isSelected() ? "H-R2L" :
                           verticalT2B.isSelected() ? "V-T2B" : "V-B2T";

        List<Point> positions = new ArrayList<>();

        for (int k = 0; k < shipSize; k++) {
            int r = row;
            int c = col;
            if (direction.equals("H-L2R")) c += k;
            if (direction.equals("H-R2L")) c -= k;
            if (direction.equals("V-T2B")) r += k;
            if (direction.equals("V-B2T")) r -= k;

            if (r < 0 || r >= 10 || c < 0 || c >= 10 || playerButtons[r][c].getIcon() != null) {
                statusLabel.setText("Gemi bu yönde yerleştirilemez!");
                return;
            }
            positions.add(new Point(r, c));
        }

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/client/images/" + imageName));
            Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            Color highlight = switch (selectedShip) {
                case "Aircraft Carrier" -> new Color(220, 245, 255);
                case "Battleship" -> new Color(255, 240, 220);
                case "Cruiser" -> new Color(230, 255, 230);
                case "Submarine" -> new Color(245, 230, 255);
                case "Destroyer" -> new Color(255, 255, 220);
                default -> Color.LIGHT_GRAY;
            };
            for (Point p : positions) {
                playerButtons[p.x][p.y].setIcon(new ImageIcon(scaled));
                playerButtons[p.x][p.y].setBackground(highlight);
            }
            placedShips.push(new PlacedShip(row, col, shipSize, selectedShip, direction));
            placedShipTypes.add(selectedShip);

            if (placedShipTypes.size() == 5) {
                readyButton.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getShipCoordinates() {
    StringBuilder sb = new StringBuilder();
    for (PlacedShip ship : placedShips) {
        for (int i = 0; i < ship.size; i++) {
            int r = ship.row;
            int c = ship.col;
            if (ship.direction.equals("H-L2R")) c += i;
            if (ship.direction.equals("H-R2L")) c -= i;
            if (ship.direction.equals("V-T2B")) r += i;
            if (ship.direction.equals("V-B2T")) r -= i;
            sb.append((char) ('A' + r)).append(c + 1).append(",");
        }
    }
    return sb.toString();
}
    private void resetGameGUI() {
        placedShips.clear();
        placedShipTypes.clear();
        readyButton.setEnabled(false);
        gameStarted = false;
        myTurn = false;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                playerButtons[i][j].setText("");
                playerButtons[i][j].setIcon(null);
                playerButtons[i][j].setBackground(Color.WHITE);

                targetButtons[i][j].setText("");
                targetButtons[i][j].setIcon(null);
                targetButtons[i][j].setBackground(Color.WHITE);
                targetButtons[i][j].setEnabled(true);
            }
        }
    }

}
