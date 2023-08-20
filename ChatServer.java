import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 3000;
    private static Map<PrintWriter, String> clientMap = new HashMap<>();
    private static Map<String, String> userCredentials = new HashMap<>(); // Map of usernames and passwords

    public static void main(String[] args) {
        // Initialize user credentials (replace with actual data)
        userCredentials.put("Diptayan", "Torbaperbichi");
        userCredentials.put("Palash", "hola");
        userCredentials.put("Srijit", "Einemukhe");

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

                    String message;
                    while ((message = reader.readLine()) != null) {
                        String formattedMessage = username + ": " + message;
                        System.out.println("Received: " + formattedMessage);
                        broadcast(formattedMessage);
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

        private boolean authenticateUser(String username, String password) {
            return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
        }
    }
}
