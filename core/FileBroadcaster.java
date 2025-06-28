package core;

import java.io.*;
import java.net.*;

public class FileBroadcaster {
    public void requestFileList(String host, int port) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("LIST_FILES");

            System.out.println("Request sent. Implement logic for response in server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
