import java.io.*;
import java.net.*;

/**
 * Servidor TCP básico que espera una única conexión en el puerto 12345.
 * Lee un mensaje del cliente y envía una respuesta antes de cerrar la conexión.
 */
public class ServidorTCP {

    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        // Usamos try-with-resources para asegurar que el ServerSocket se cierre automáticamente
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            
            System.out.println("Servidor TCP escuchando en el puerto: " + PUERTO);
            System.out.println("Esperando conexión de un cliente...");

            // Bloquea la ejecución hasta que un cliente se conecta
            try (Socket socketCliente = serverSocket.accept()) {
                System.out.println("✅ Cliente conectado desde: " + socketCliente.getInetAddress().getHostAddress());

                // 1. Configurar flujos de datos (usando BufferedReader y PrintWriter para texto)
                // InputStreamReader convierte bytes del socket a caracteres
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                // PrintWriter permite enviar texto y el 'true' es para auto-flush (envío inmediato)
                PrintWriter salida = new PrintWriter(socketCliente.getOutputStream(), true);

                // 2. Leer datos del cliente
                String mensajeCliente = entrada.readLine();
                if (mensajeCliente != null) {
                    System.out.println(" [RECIBIDO] Mensaje del cliente: " + mensajeCliente);
                }

                // 3. Enviar respuesta al cliente
                String respuesta = "Hola, soy el servidor! Hemos recibido tu mensaje.";
                salida.println(respuesta);
                System.out.println(" [ENVIADO] Respuesta al cliente: " + respuesta);

                // 4. Los flujos y el socketCliente se cierran automáticamente gracias al try-with-resources
            }
            
        } catch (IOException e) {
            System.err.println("Error de I/O en el servidor: " + e.getMessage());
            //e.printStackTrace();
        }
    }
}