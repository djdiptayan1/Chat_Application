import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "10.3.131.199";
    private static final int SERVER_PORT = 3000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            writer.println(name); // Send the client's name to the server

            Thread inputThread = new Thread(() -> {
                while (true) {
                    String message = scanner.nextLine();
                    writer.println(message);
                }
            });

            Thread outputThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            inputThread.start();
            outputThread.start();

            inputThread.join();
            outputThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
