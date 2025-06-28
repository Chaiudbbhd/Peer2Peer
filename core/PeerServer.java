package core;

import java.io.*;
import java.net.*;

public class PeerServer {
    private int port;
    private static final String KEY = "1234567890123456";

    public PeerServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleConnection(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {

            int fileCount = in.readInt();

            for (int i = 0; i < fileCount; i++) {
                String name = in.readUTF();
                long length = in.readLong();
                String hash = in.readUTF();

                byte[] encData = new byte[(int) length];
                in.readFully(encData);

                byte[] fileData = CryptoUtils.decrypt(encData, KEY);
                String calcHash = CryptoUtils.hash(fileData);

                if (calcHash.equals(hash)) {
                    try (FileOutputStream fos = new FileOutputStream("received_" + name)) {
                        fos.write(fileData);
                    }
                    System.out.println("Received " + name + " (verified ✅)");
                } else {
                    System.out.println("Hash mismatch for " + name + " ❌");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
