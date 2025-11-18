import java.io.IOException;

public class MainProcesos {
    public static void main(String[] args) {
        
        // 1. DEFINIR COMANDO (Compatibilidad OS)
        ProcessBuilder pb = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows: cmd /c comando
            pb.command("cmd", "/c", "dir"); 
        } else {
            // Linux/Mac: /bin/sh -c comando
            pb.command("/bin/sh", "-c", "ls -l");
        }

        try {
            // 2. CONFIGURACIÓN EXTRA (Opcional)
            // Redirigir salida a consola para ver qué pasa
            pb.inheritIO(); 
            // O redirigir a fichero:
            // pb.redirectOutput(new File("salida.txt"));
            
            // 3. EJECUTAR (Nace el proceso)
            Process proceso = pb.start();

            // 4. ESPERAR (Sincronización)
            // Java se bloquea aquí hasta que el comando termine
            int codigoSalida = proceso.waitFor();

            // 5. VERIFICAR RESULTADO
            if (codigoSalida == 0) {
                System.out.println("Éxito total.");
                // Aquí podrías lanzar un segundo proceso si el primero fue bien
            } else {
                System.err.println("El proceso falló con código: " + codigoSalida);
                // Manejo de error
            }

        } catch (IOException e) {
            System.err.println("Error al intentar lanzar el comando (no existe, ruta mal, etc)");
        } catch (InterruptedException e) {
            System.err.println("El proceso Java fue interrumpido mientras esperaba.");
        }
    }
}