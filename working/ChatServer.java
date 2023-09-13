import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatServer {
    private static final int PORT = 3000;
    private static Map<PrintWriter, String> clientMap = new HashMap<>();
    private static Map<String, String> userCredentials = new HashMap<>();

    public static void main(String[] args) {
        // Initialize user credentials (replace with actual data)
        userCredentials.put("Diptayan", "12");
        userCredentials.put("Palash", "12");
        userCredentials.put("Srijit", "12");

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
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                        true);

                String username = reader.readLine();
                String password = reader.readLine();

                if (authenticateUser(username, password)) {
                    writer.println("AUTHENTICATED");
                    this.username = username;

                    synchronized (clientMap) {
                        clientMap.put(writer, username);
                    }

                    String message;
                    while ((message = reader.readLine()) != null) {
                        if (message.equals("FILE")) {
                            handleFileTransfer(reader);
                        } else {
                            String formattedMessage = username + ": " + message;
                            System.out.println("Received: " + formattedMessage);
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

        private void handleFileTransfer(BufferedReader reader) {
            try {
                String fileName = reader.readLine();
                String sender = clientMap.get(writer);
                String message = sender + " sent a file: " + fileName;

                broadcast(message);

                String filePath = "path_to_save_files/" + fileName; // Specify the path where you want to save the file
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                byte[] buffer = new byte[1024];
                int bytesRead;

                InputStream inputStream = socket.getInputStream(); // Get the socket's input stream for reading bytes

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(String message) {
            synchronized (clientMap) {
                for (PrintWriter writer : clientMap.keySet()) {
                    writer.println(message);
                }
            }
        }

        private boolean authenticateUser(String username, String password) {
            return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
        }
    }
}
