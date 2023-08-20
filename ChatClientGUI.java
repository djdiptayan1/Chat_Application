import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClientGUI extends JFrame {
    private static final String SERVER_ADDRESS = ipaddress();
    private static final int SERVER_PORT = 3000;

    private PrintWriter writer;

    private JTextField inputField;
    private JTextArea chatArea;
    private JButton sendButton; // New button for sending messages

    public ChatClientGUI() {
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
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

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

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

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
                        chatArea.append(message + "\n");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI());
    }
}
