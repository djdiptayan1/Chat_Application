import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 3000;
    private static Map<PrintWriter, String> clientMap = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientMap.put(writer, ""); // Initialize with an empty name

                Thread clientThread = new ClientHandler(clientSocket, writer);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter writer;

        public ClientHandler(Socket socket, PrintWriter writer) {
            this.socket = socket;
            this.writer = writer;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String clientName = reader.readLine(); // Read the client's name
                System.out.println("Client name: " + clientName);

                synchronized (clientMap) {
                    clientMap.put(writer, clientName);
                }

                String message;
                while ((message = reader.readLine()) != null) {
                    String formattedMessage = clientName + ": " + message;
                    System.out.println("Received: " + formattedMessage);
                    broadcast(formattedMessage);
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
    }
}
