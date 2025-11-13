import java.io.*;
import java.net.*;

/**
 * Cliente TCP básico que se conecta a un servidor en localhost:12345.
 * Envía un mensaje y espera una respuesta.
 */
public class ClienteTCP {

    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        // Usamos try-with-resources para asegurar que el Socket se cierre automáticamente
        try (Socket socket = new Socket(HOST, PUERTO)) {
            
            System.out.println("Conectando a " + HOST + ":" + PUERTO + "...");

            // 1. Configurar flujos de datos
            // InputStreamReader y BufferedReader para leer la respuesta del servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // PrintWriter para enviar el mensaje al servidor
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // 2. Enviar datos al servidor
            String mensajeEnvio = "¡Hola desde el cliente! ¿Estás ahí?";
            salida.println(mensajeEnvio);
            System.out.println(" [ENVIADO] Mensaje al servidor: " + mensajeEnvio);

            // 3. Leer la respuesta del servidor
            String respuestaServidor = entrada.readLine();
            System.out.println(" [RECIBIDO] Respuesta del servidor: " + respuestaServidor);

            // 4. El socket y los flujos se cierran automáticamente gracias al try-with-resources
            
        } catch (ConnectException e) {
            System.err.println("❌ ERROR: No se pudo conectar al servidor.");
            System.err.println("Asegúrate de que 'ServidorTCP.java' esté ejecutándose en el puerto " + PUERTO + ".");
        } catch (IOException e) {
            System.err.println("Error de I/O en el cliente: " + e.getMessage());
            //e.printStackTrace();
        }
    }
}