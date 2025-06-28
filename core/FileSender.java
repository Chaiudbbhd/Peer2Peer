package core;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FileSender {
    private String host;
    private int port;
    private File[] files;
    private static final String KEY = "1234567890123456";

    public FileSender(String host, int port, File[] files) {
        this.host = host;
        this.port = port;
        this.files = files;
    }

    public void sendFiles() throws Exception {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeInt(files.length); // number of files

            for (File file : files) {
                byte[] fileBytes = CryptoUtils.encryptFile(file, KEY);
                String hash = CryptoUtils.hash(fileBytes);

                out.writeUTF(file.getName());
                out.writeLong(fileBytes.length);
                out.writeUTF(hash);
                out.write(fileBytes);
                out.flush();

                System.out.println("Sent: " + file.getName() + " [Encrypted + Hashed]");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
