package core;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import core.CryptoUtils;

public class FileReceiver {
    private String host;
    private int port;
    private static final String SECRET_KEY = "1234567890123456";

    public FileReceiver(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void receiveFileWithProgress() {
        // Step 1: Select download folder
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select Folder to Save Files");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "No folder selected. Exiting.");
            return;
        }

        File saveDir = folderChooser.getSelectedFile();

        try (Socket socket = new Socket(host, port);
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            int fileCount = in.readInt();

            JProgressBar progressBar = new JProgressBar(0, fileCount);
            progressBar.setStringPainted(true);
            JFrame frame = new JFrame("Receiving Files...");
            frame.setSize(400, 100);
            frame.setLayout(new BorderLayout());
            frame.add(progressBar, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            for (int i = 0; i < fileCount; i++) {
                String fileName = in.readUTF();
                long fileSize = in.readLong();
                String expectedHash = in.readUTF();

                byte[] encrypted = new byte[(int) fileSize];
                in.readFully(encrypted);

                byte[] decrypted = CryptoUtils.decrypt(encrypted, SECRET_KEY);
                String actualHash = CryptoUtils.hash(decrypted);

                if (actualHash.equals(expectedHash)) {
                    File outFile = new File(saveDir, "received_" + fileName);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(decrypted);
                    }
                    System.out.println("✅ Saved: " + outFile.getAbsolutePath());
                } else {
                    System.out.println("❌ Hash mismatch: " + fileName);
                }

                progressBar.setValue(i + 1);
            }

            frame.dispose();

            JOptionPane.showMessageDialog(null, "All files received successfully!", "Done", JOptionPane.INFORMATION_MESSAGE);
            Desktop.getDesktop().open(saveDir);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String host = JOptionPane.showInputDialog("Enter Peer IP:");
        int port = Integer.parseInt(JOptionPane.showInputDialog("Enter Port:"));
        new FileReceiver(host, port).receiveFileWithProgress();
    }
}
