import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class server_pass {
    private static final int PORT = 3000;
    private static Map<PrintWriter, String> clientMap = new HashMap<>();
    private static Map<String, String> userCredentials = new HashMap<>();
    private static List<String> chatHistory = new ArrayList<>(); // List to store chat history

    public static void main(String[] args) {
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

                    // Send chat history to the client
                    for (String historyMessage : chatHistory) {
                        writer.println(historyMessage);
                    }

                    String message;
                    while ((message = reader.readLine()) != null) {
                        if (message.equals("SEND_FILE")) {
                            String fileName = reader.readLine();
                            String receiver = reader.readLine();
                            sendFile(fileName, receiver);
                        } else {
                            String formattedMessage = username + ": " + message;
                            System.out.println("Received: " + formattedMessage);
                            chatHistory.add(formattedMessage); // Store the message in chat history
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

        private boolean authenticateUser(String username, String password) {
            return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
        }

        private void sendFile(String fileName, String receiver) {
            synchronized (clientMap) {
                for (PrintWriter writer : clientMap.keySet()) {
                    if (clientMap.get(writer).equals(receiver)) {
                        try {
                            writer.println("RECEIVING_FILE");
                            FileInputStream fileInputStream = new FileInputStream(fileName);
                            byte[] buffer = new byte[1024];
                            int bytesRead;

                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                writer.println("FILE_CONTENT");
                                writer.flush();
                                writer.println(Base64.getEncoder().encodeToString(buffer));
                                writer.flush();
                            }

                            writer.println("FILE_END");
                            writer.flush();

                            fileInputStream.close();
                            System.out.println("File " + fileName + " sent to " + receiver);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }
}
