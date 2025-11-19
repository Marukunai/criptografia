import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BankServerThread {
    public static final int PORT = 5000;
    public static final int MAX_CLIENTS = 50;

    // Mapa compartido entre hilos
    public static final Map<String, Double> comptes = Collections.synchronizedMap(new HashMap<>());

    // Contador de conexiones activas
    public static int connexionsActives = 0;

    public static void main(String[] args) {
        System.out.println("Servidor bancari escoltant al port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                synchronized (BankServer.class) {
                    if (connexionsActives >= MAX_CLIENTS) {
                        System.out.println("❌ Connexió rebutjada des de " + clientSocket.getInetAddress() +
                                           ". Màxim de 50 clients ja connectats.");
                        try {
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            out.println("ERROR 503: Servidor saturat. Torna-ho a intentar més tard.");
                        } catch (IOException e) {
                            System.out.println("⚠ No s'ha pogut enviar missatge d'error al client.");
                        } finally {
                            clientSocket.close();
                        }
                        continue;
                    }
                    connexionsActives++;
                }

                System.out.println("✅ Client connectat. Total clients: " + connexionsActives);
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error al servidor: " + e.getMessage());
        }
    }

    public static synchronized void desconnectarClient() {
        connexionsActives--;
        System.out.println("🔌 Client desconnectat. Total clients: " + connexionsActives);
    }
}