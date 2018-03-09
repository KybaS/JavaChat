package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener{

    private static final String IP_ADDR = "192.168.0.103";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;


    public static void main(String[]args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextArea fieldNickName = new JTextArea("John");
    private final JTextArea fieldInput = new JTextArea();

    private TCPConnection connection;

        private ClientWindow(){
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLocationRelativeTo(null);
            setAlwaysOnTop(true);
            log.setEditable(false);
            log.setLineWrap(true);
            add(log, BorderLayout.CENTER);

            fieldInput.addAncestorListener((AncestorListener) this);
            add(fieldInput, BorderLayout.SOUTH);
            add(fieldNickName, BorderLayout.NORTH);

            setVisible(true);
            try {
                connection = new TCPConnection(this, IP_ADDR, PORT);
            }catch (IOException e){
                printMsg("Connection exception: " + e);
            }
        }

    @Override
    public void actionPerformed(ActionEvent e) {
            String msg = fieldInput.getText();
            if (msg.equals("")) return;
            fieldInput.setText(null);
            connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(chat.network.TCPConnection tcpConnection) {
            printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(chat.network.TCPConnection tcpConnection, String value) {
            printMsg(value);
    }

    @Override
    public void onDisconnect(chat.network.TCPConnection tcpConnection) {
            printMsg("Connection close");
    }

    @Override
    public void onException(chat.network.TCPConnection tcpConnection, Exception e) {
            printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    log.append(msg + "\n");
                    log.setCaretPosition(log.getDocument().getLength());
                }
            });
    }
}
