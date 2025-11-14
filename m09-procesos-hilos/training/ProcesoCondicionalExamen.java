package training;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProcesoCondicionalExamen {

    public static void main(String[] args) {
        String IP_FALLIDA = "10.255.255.1";
        
        // Comandos Ping compatibles con el SO
        String[] comandoPing;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            comandoPing = new String[]{"ping", "-n", "1", IP_FALLIDA};
        } else {
            comandoPing = new String[]{"ping", "-c", "1", IP_FALLIDA};
        }
        
        ProcessBuilder builder1 = new ProcessBuilder(comandoPing);
        Process p1 = null;
        int exitCodeP1 = -1;
        
        System.out.println("--- INICIO DE PROCESOS ---");
        
        try {
            // a) Iniciar el Proceso 1
            builder1.inheritIO();
            p1 = builder1.start();
            System.out.println("Proceso 1: Ejecutando " + String.join(" ", comandoPing));
            
            // b) Esperar y obtener el código de salida
            p1.waitFor();
            exitCodeP1 = p1.exitValue();
            
            System.out.println("Proceso 1 finalizado. Código de salida: " + exitCodeP1);

            // -------------------------------------------------------------------
            // c) Lógica Condicional para Procesos 2 y 3 (mkdir y log)
            // -------------------------------------------------------------------
            
            if (exitCodeP1 != 0) {
                System.out.println("\n--- Condición de FALLO CUMPLIDA (exit " + exitCodeP1 + ") ---");
                
                // Definimos las rutas relativas forzando la creación dentro de 'training'
                final String RUTA_DIR_LOGS = "m09-procesos-hilos/training/ErrorLogs";
                final String RUTA_ARCHIVO_LOG = RUTA_DIR_LOGS + "/fallos.txt";
                
                // PARTE 2: Crear el directorio (mkdir)
                
                String[] comandoMkdir;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    // Windows usa cmd /c mkdir y barras invertidas en el path
                    String winPath = RUTA_DIR_LOGS.replace("/", "\\");
                    comandoMkdir = new String[]{"cmd", "/c", "mkdir", winPath};
                } else {
                    // Unix/Linux/Mac usa mkdir y barras normales
                    comandoMkdir = new String[]{"mkdir", RUTA_DIR_LOGS};
                }
                
                ProcessBuilder builder2 = new ProcessBuilder(comandoMkdir);
                builder2.start().waitFor();
                
                System.out.println("Proceso 2: Se verificó/creó el directorio '" + RUTA_DIR_LOGS + "'.");
                
                // -------------------------------------------------------------------
                // PARTE 3: Crear el archivo de log (echo >> log.txt)
                // -------------------------------------------------------------------
                
                // Obtener timestamp para el log
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String logMessage = String.format("FALLO DETECTADO: [%s] Proceso 'ping' ha devuelto %d.", timestamp, exitCodeP1);

                String[] comandoLog;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    // Windows: cmd /c echo [Mensaje] >> training\ErrorLogs\fallos.txt
                    String winFilePath = RUTA_ARCHIVO_LOG.replace("/", "\\");
                    comandoLog = new String[]{"cmd", "/c", "echo", logMessage, ">>", winFilePath};
                } else {
                    // Unix/Linux/Mac: /bin/sh -c 'echo [Mensaje] >> training/ErrorLogs/fallos.txt'
                    comandoLog = new String[]{"/bin/sh", "-c", "echo '" + logMessage + "' >> " + RUTA_ARCHIVO_LOG};
                }

                ProcessBuilder builder3 = new ProcessBuilder(comandoLog);
                Process p3 = builder3.start();
                p3.waitFor();
                
                int exitCodeP3 = p3.exitValue();

                System.out.println("Proceso 3: Se escribió el log de fallo en " + RUTA_ARCHIVO_LOG + ".");
                System.out.println("Proceso 3 (log) terminado. Código de salida: " + exitCodeP3);

            } else {
                System.out.println("\n--- Condición de FALLO NO CUMPLIDA (exit 0) ---");
                System.out.println("El Proceso 1 no falló. No se ejecuta la acción de log.");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error fatal en el programa: " + e.getMessage());
        }
        
        System.out.println("\n--- FIN DE PROCESOS ---");
    }
}