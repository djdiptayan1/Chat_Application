package test;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import raven.chat.component.ChatBox;
import raven.chat.model.ModelMessage;
import raven.chat.swing.ChatEvent;

/**
 *
 * 
 */
public class Test extends javax.swing.JFrame {

    /**
     * Creates new form Test
     */
    private String name;
    private static final String SERVER_ADDRESS = ipaddress();
private static final int SERVER_PORT = 3000;
private boolean isSelfMessage=false;

private PrintWriter writer;

    public Test() {
        initComponents();
        chatArea.setTitle("Java Swing Chat");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, hh:mmaa");
        connectToServer();
        
        chatArea.addChatEvent(new ChatEvent() {
            @Override
            public void mousePressedSendButton(ActionEvent evt) {
                
                Icon icon = new ImageIcon(getClass().getResource("/test/p1.png"));
                         
                String date = df.format(new Date());
                String message = chatArea.getText().trim();
                isSelfMessage=true;
                chatArea.addChatBox(new ModelMessage(icon, name, date, message), ChatBox.BoxType.RIGHT);
                chatArea.clearTextAndGrabFocus();
                sendMessageToServer(message);
            }

            @Override
            public void mousePressedFileButton(ActionEvent evt) {

            }

            @Override
            public void keyTyped(KeyEvent evt) {

            }
        });
    }
    private void sendMessageToServer(String message) {
        if (!message.isEmpty() && writer != null) {
            writer.println(message);
        }
    }
    private void connectToServer() {
    try {
        Icon icon = new ImageIcon(getClass().getResource("/test/p1.png"));
         SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, hh:mmaa");
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
         String date = df.format(new Date());
        name = JOptionPane.showInputDialog("Enter your name:");
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
                        if(!isSelfMessage){
                            String[] parts = message.split(": ", 2);
                            if (parts.length == 2) {
                                String senderName = parts[0];
                                String actualMessage = parts[1];
                                chatArea.addChatBox(new ModelMessage(icon, senderName, date, actualMessage), ChatBox.BoxType.LEFT);
                            }
                         }
                        isSelfMessage=false;
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background1 = new raven.chat.swing.Background();
        chatArea = new raven.chat.component.ChatArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        javax.swing.GroupLayout background1Layout = new javax.swing.GroupLayout(background1);
        background1.setLayout(background1Layout);
        background1Layout.setHorizontalGroup(
            background1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(background1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chatArea, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );
        background1Layout.setVerticalGroup(
            background1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(background1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chatArea, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Test().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private raven.chat.swing.Background background1;
    private raven.chat.component.ChatArea chatArea;
    // End of variables declaration//GEN-END:variables
}
