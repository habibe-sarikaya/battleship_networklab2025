
🛳️ Battleship Game – Computer Networks Project

This is a Java-based Battleship game developed as part of the Computer Networks course at FSMVU.

The system architecture consists of a console-based server and two GUI-based clients built using Java Swing.
The server is hosted on an AWS EC2 instance and uses TCP socket communication to manage game flow, player turns, and data exchange.

Each client connects to the server using its IP address and operates independently, offering a user-friendly graphical interface for interacting with the game.
Players can place five ships on a 10x10 grid, take turns attacking by entering coordinates (e.g., B4), and receive real-time visual feedback (hit or miss) on their game boards.

To enhance the gameplay experience, the client interface also includes:
- 💬 A built-in chat system for real-time communication between players
- 🆘 A "Surrender" button to allow players to concede the game
- 🔁 The ability to play multiple games without restarting the application

This project demonstrates practical use of Java networking (sockets), multithreading for simultaneous client handling, and GUI design principles.


---

🌐 Technologies Used

- Java SE
- Java Swing (for GUI)
- TCP Sockets (java.net)
- AWS EC2 (Ubuntu Linux)

---

📡 Server Deployment on AWS

To start the server on your EC2 instance:

```bash
ssh -i battleship-key.pem ubuntu@54.204.163.176
cd ~/server
javac server/Server.java
java server.Server
````

Expected output:

```bash
Server is running... Waiting for players.
```

---

💻 Running the Client

1. Compile the client-side files:

```bash
javac client/Client.java
javac gui/GameGUI.java
```

2. Run the client:

```bash
java client.Client
```

3. When prompted, enter the server IP:

```
54.204.163.176
```

---

🕹️ How to Play

* Each player places 5 ships on a 10x10 grid.
* Players take turns guessing coordinates (e.g., B4).
* The GUI shows hit/miss results graphically.
* When all opponent ships are sunk, a winner is declared.
* A final screen appears with options to “Play Again” or “Exit”.
* Players can also:

  * 💬 Send chat messages
  * 🆘 Use a **Surrender** button to forfeit

---

✨ Features

* 🎨 Clean and user-friendly GUI (Java Swing)
* 🔁 Supports replay without restarting the app
* 🌐 Real-time two-player networked gameplay
* 💬 In-game chat system
* 🆘 Surrender button
* 📡 Server deployed on AWS


 👩‍💻 Developed by

**Habibe Sarıkaya**

Student ID: 2121251009
Fatih Sultan Mehmet Vakıf University
Spring 2025 – Computer Networks Lab Project
23.05.2025
````

----
