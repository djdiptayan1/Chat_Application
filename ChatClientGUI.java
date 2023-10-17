import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClientGUI extends JFrame {
    private static final String SERVER_ADDRESS = ipaddress();
    // private static final String SERVER_ADDRESS = "10.5.162.37";
    private static final int SERVER_PORT = 3000;

    private Socket socket; // Add a Socket field
    private PrintWriter writer;

    private JTextField inputField;
    private JTextArea chatArea;
    private JButton sendButton; // New button for sending messages
    private JButton sendFileButton; // Button for sending files

    public ChatClientGUI(Socket socket) {
        this.socket = socket;
        initUI();
        connectToServer();
    }

    private void initUI() {
        setTitle("Chat Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);

        inputField = new JTextField();
        chatArea = new JTextArea();
        chatArea.setEditable(false);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        sendButton = new JButton("Send"); // Initialize the send button
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        sendFileButton = new JButton("Send File");
        sendFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.add(sendFileButton, BorderLayout.WEST); // Add the Send File button

        setLayout(new BorderLayout());
        add(chatScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            String name = JOptionPane.showInputDialog("Enter your name:");
            writer.println(name);
            String password = JOptionPane.showInputDialog("Enter your password:");
            writer.println(password);

            Thread outputThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        if (message.equals("AUTHENTICATED")) {
                            JOptionPane.showMessageDialog(this, "Authenticated successfully!");
                        } else if (message.equals("AUTH_FAILED")) {
                            JOptionPane.showMessageDialog(this, "Authentication failed. Exiting...");
                            System.exit(0);
                        } else {
                            chatArea.append(message + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            outputThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            writer.println(message);
            inputField.setText("");
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showOpenDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Sending file: " + selectedFile);

            try {
                OutputStream outputStream = socket.getOutputStream();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);

                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    objectOutputStream.write(buffer, 0, bytesRead);
                }

                objectOutputStream.flush();
                objectOutputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String ipaddress() {
        String ipadd = "";
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                for (InterfaceAddress interfaceAddress : networkInterfaceEnumeration.nextElement()
                        .getInterfaceAddresses())
                    if (interfaceAddress.getAddress().isSiteLocalAddress())
                        ipadd = interfaceAddress.getAddress().getHostAddress();
            }
            return ipadd;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipadd;
    }

    private static int showStartDialog() {
        Object[] options = { "Existing User", "New User" };
        int choice = JOptionPane.showOptionDialog(
                null,
                "Are you an existing user or a new user?",
                "User Type",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        return choice;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int choice = showStartDialog();
            if (choice == 0) {
                // Existing User - Connect to the server
                String username = JOptionPane.showInputDialog("Enter your username:");
                String password = JOptionPane.showInputDialog("Enter your password:");

                try {
                    Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    ChatClientGUI chatClientGUI = new ChatClientGUI(socket);
                    chatClientGUI.authenticate(username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (choice == 1) {
                // New User - Register and then connect to the server
                String username = JOptionPane.showInputDialog("Enter your username:");
                String password = JOptionPane.showInputDialog("Create a password:");

                // You can add your registration logic here, e.g., sending username and password
                // to the server.

                try {
                    Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    ChatClientGUI chatClientGUI = new ChatClientGUI(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void authenticate(String username, String password) {
        // Send the username and password to the server and handle authentication logic.
        writer.println(username);
        writer.println(password);
        // Rest of the authentication logic remains the same.
    }
}