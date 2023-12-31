import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class ChatServer {
    private static final int PORT = 3000;
    private static Map<PrintWriter, String> clientMap = new HashMap<>();
    private static List<String> chatHistory = new ArrayList<>(); // List to store chat history
    private static Connection dbConnection; // Database connection

    public static void main(String[] args) {
        // Establish a database connection
        establishDBConnection();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                Thread clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the database connection when the server is shut down
            closeDBConnection();
        }
    }

    private static void establishDBConnection() {
        try {
            // Replace the database URL, username, and password with your own
            String dbUrl = "jdbc:mysql://localhost:3306/chat_database";
            String dbUser = "root";
            String dbPassword = "Djsd@2611";
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeDBConnection() {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                String username = reader.readLine();
                String password = reader.readLine();

                if (authenticateUser(username, password)) {
                    writer.println("AUTHENTICATED");
                    this.username = username;

                    synchronized (clientMap) {
                        clientMap.put(writer, username);
                    }

                    for (String historyMessage : chatHistory) {
                        writer.println(historyMessage);
                    }

                    String message;
                    while ((message = reader.readLine()) != null) {
                        if (message.startsWith("SEND_FILE:")) {
                            String[] parts = message.split(":", 3);
                            if (parts.length == 3) {
                                String sender = username;
                                String recipient = parts[1];
                                String fileName = parts[2];

                                broadcastFileTransfer(sender, recipient, fileName);
                                receiveFile(sender, fileName);
                            }
                        } else {
                            String formattedMessage = username + ": " + message;
                            System.out.println("Received: " + formattedMessage);
                            chatHistory.add(formattedMessage);
                            broadcast(formattedMessage);
                        }
                    }
                } else {
                    writer.println("AUTH_FAILED");
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (clientMap) {
                    clientMap.remove(writer);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            synchronized (clientMap) {
                for (PrintWriter writer : clientMap.keySet()) {
                    writer.println(message);
                }
            }
        }

        private void broadcastFileTransfer(String sender, String recipient, String fileName) {
            synchronized (clientMap) {
                for (PrintWriter writer : clientMap.keySet()) {
                    if (clientMap.get(writer).equals(recipient)) {
                        writer.println("RECEIVE_FILE:" + sender + ":" + fileName);
                    }
                }
            }
        }

        private boolean authenticateUser(String username, String password) {
            // Use the database to authenticate users
            try {
                String query = "SELECT password FROM users WHERE username = ?";
                PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return password.equals(storedPassword);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

        private void receiveFile(String sender, String fileName) {
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);

                // Generate a unique filename based on sender and timestamp
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

                String uniqueFileName = sender + "_" + dateFormat.format(new Date()) + "_" + fileName;

                FileOutputStream fileOutputStream = new FileOutputStream(uniqueFileName);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = objectInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                }

                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                // Inform the sender that the file was received
                writer.println("FILE_RECEIVED:" + sender + ":" + uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
