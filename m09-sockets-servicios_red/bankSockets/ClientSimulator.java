package bankSockets;

import java.io.*;
import java.net.*;

public class ClientSimulator {
    public static void main(String[] args) {
        for (int i = 1; i <= 55; i++) {
            final int clientId = i;
            new Thread(() -> {
                try {
                    Socket socket = new Socket("localhost", 5000);
                    System.out.println("Client " + clientId + " connectat.");
                    
                    Thread.sleep(60000); 
                    
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Client " + clientId + " rebutjat o error.");
                } catch (InterruptedException e) {
                    System.out.println("Client " + clientId + " interromput.");
                }
            }).start();
        }
    }
}
