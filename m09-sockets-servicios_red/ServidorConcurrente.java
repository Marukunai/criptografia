import java.io.*;
import java.net.*;

/**
 * Servidor TCP Concurrente.
 * Utiliza un bucle infinito para aceptar múltiples clientes.
 * Por cada cliente conectado, crea un nuevo hilo (ManejadorCliente) para atenderlo.
 */
public class ServidorConcurrente {

    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        // Se usa ServerSocket fuera del try-with-resources para que no se cierre en cada iteración
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            
            System.out.println("Servidor CONCURRENTE escuchando en el puerto: " + PUERTO);
            System.out.println("Listo para recibir múltiples clientes...");

            // Bucle infinito para escuchar y aceptar conexiones de forma continua
            while (true) {
                // Bloquea hasta que un cliente se conecta
                Socket clienteSocket = serverSocket.accept();
                System.out.println("✅ Cliente conectado: " + clienteSocket.getInetAddress().getHostAddress());

                // Crea un nuevo hilo para manejar la comunicación con ESTE cliente.
                // Esto permite que el bucle principal vuelva inmediatamente a serverSocket.accept()
                // para esperar al PRÓXIMO cliente.
                new Thread(new ManejadorCliente(clienteSocket)).start();
            }
            
        } catch (IOException e) {
            System.err.println("Error fatal en el ServerSocket: " + e.getMessage());
        }
    }
}

/**
 * Clase Runnable que maneja la lógica de comunicación con un único cliente.
 * Cada instancia de esta clase corre en su propio hilo.
 */
class ManejadorCliente implements Runnable {
    
    private final Socket socket;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // Usamos try-with-resources para asegurar el cierre automático de los flujos
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {
            
            String mensajeCliente;
            // Lee líneas del cliente hasta que la conexión se cierre o el cliente no envíe nada
            while ((mensajeCliente = entrada.readLine()) != null) {
                System.out.println(" [RECIBIDO de " + socket.getInetAddress().getHostAddress() + "] " + mensajeCliente);
                
                // Lógica del servidor: responde al cliente
                String respuesta = "Echo: " + mensajeCliente.toUpperCase();
                salida.println(respuesta);
            }
            
        } catch (IOException e) {
            // No es necesariamente un error, puede ser el cliente cerrando la conexión.
            // e.printStackTrace(); 
        } finally {
            // Asegurarse de cerrar el socket del cliente
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println(" Cliente desconectado: " + socket.getInetAddress().getHostAddress());
                }
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket del cliente.");
            }
        }
    }
}