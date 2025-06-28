package ui;

import core.PeerServer;
import core.FileSender;
import core.FileBroadcaster;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainGUI {
    public void createAndShowGUI() {
        JFrame frame = new JFrame("P2P File Sharing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        JButton sendButton = new JButton("Send File(s)");
        JButton startServerButton = new JButton("Start Sharing");
        JButton fileListButton = new JButton("View Peer Files");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        sendButton.addActionListener(e -> {
            fileChooser.showOpenDialog(frame);
            File[] selectedFiles = fileChooser.getSelectedFiles();
            String host = JOptionPane.showInputDialog("Enter Peer IP:");
            String portStr = JOptionPane.showInputDialog("Enter Peer Port:");
            int port = Integer.parseInt(portStr);

            new Thread(() -> {
                try {
                    new FileSender(host, port, selectedFiles).sendFiles();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }).start();
        });

        startServerButton.addActionListener(e -> {
            String portStr = JOptionPane.showInputDialog("Enter port to share on:");
            int port = Integer.parseInt(portStr);
            new Thread(() -> new PeerServer(port).startServer()).start();
        });

        fileListButton.addActionListener(e -> {
            String host = JOptionPane.showInputDialog("Enter Peer IP:");
            String portStr = JOptionPane.showInputDialog("Enter Peer Port:");
            int port = Integer.parseInt(portStr);
            new FileBroadcaster().requestFileList(host, port);
        });

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(sendButton);
        panel.add(startServerButton);
        panel.add(fileListButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}
