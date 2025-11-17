package training;

import java.io.File;
import java.io.IOException;

public class ProcesoArchivoSimplificado {

    public static void main(String[] args) {
        System.out.println("--- INICIO DE PROCESO DE ARCHIVO ---");
        
        // Define la ruta del archivo y el mensaje de forma centralizada
        final String RUTA_ARCHIVO = "m09-procesos-hilos/training/info/salida_info.txt";
        final String MSG_INICIO = "Inicio de la ejecución.";
        final String MSG_FIN = "Fin de la ejecución.";
        
        try {
            // =======================================================
            // 1. PROCESO 1: VERIFICACIÓN (Condición de inicio)
            // =======================================================
            ProcessBuilder builder1 = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                builder1.command("cmd", "/c", "dir C:\\Windows\\System32\\drivers\\etc\\hosts");
            } else {
                builder1.command("/bin/sh", "-c", "ls /etc/passwd");
            }

            Process p1 = builder1.inheritIO().start();
            int exitCodeP1 = p1.waitFor();
            
            if (exitCodeP1 != 0) {
                // Si la condición falla, imprime el error y termina el programa.
                System.err.println("ERROR P1: Fallo al verificar el archivo. Código: " + exitCodeP1);
                return; 
            }
            System.out.println("P1: Verificación OK. Código: " + exitCodeP1);

            // -------------------------------------------------------

            // =======================================================
            // 2. PROCESO 2: CREACIÓN DEL ARCHIVO
            // =======================================================
            ProcessBuilder builder2 = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                builder2.command("cmd", "/c", "type nul > " + RUTA_ARCHIVO);
            } else {
                builder2.command("/bin/sh", "-c", "touch " + RUTA_ARCHIVO);
            }
            
            Process p2 = builder2.start();
            int exitCodeP2 = p2.waitFor();

            if (exitCodeP2 != 0) {
                System.err.println("ERROR P2: Fallo al crear el archivo. Código: " + exitCodeP2);
                return;
            }
            System.out.println("P2: Archivo de salida creado. Código: " + exitCodeP2);

            // -------------------------------------------------------

            // =======================================================
            // 3. PROCESO 3: ESCRITURA INICIAL (Sobrescribir >)
            // =======================================================
            ProcessBuilder builder3 = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                builder3.command("cmd", "/c", "echo " + MSG_INICIO + " > " + RUTA_ARCHIVO);
            } else {
                builder3.command("/bin/sh", "-c", "echo '" + MSG_INICIO + "' > " + RUTA_ARCHIVO);
            }

            Process p3 = builder3.start();
            int exitCodeP3 = p3.waitFor();
            
            if (exitCodeP3 != 0) {
                System.err.println("ERROR P3: Fallo al escribir el inicio. Código: " + exitCodeP3);
                return;
            }
            System.out.println("P3: Información inicial escrita. Código: " + exitCodeP3);
            
            // -------------------------------------------------------

            // =======================================================
            // 4. PROCESO 4: REDIRECCIÓN DE LISTADO (Anexar >>)
            // =======================================================
            ProcessBuilder builder4 = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                builder4.command("cmd", "/c", "dir C:\\Windows\\System32");
            } else {
                builder4.command("/bin/sh", "-c", "find /bin");
            }

            // Configurar la redirección a modo APPEND (anexar)
            builder4.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(RUTA_ARCHIVO)));
            
            Process p4 = builder4.start(); 
            int exitCodeP4 = p4.waitFor();
            
            if (exitCodeP4 != 0) {
                System.err.println("ERROR P4: Fallo al anexar listado. Código: " + exitCodeP4);
                return;
            }
            System.out.println("P4: Listado anexado correctamente. Código: " + exitCodeP4);

            // -------------------------------------------------------

            // =======================================================
            // 5. PROCESO 5: CERRAR LOG (Anexar >>)
            // =======================================================
            ProcessBuilder builder5 = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                builder5.command("cmd", "/c", "echo " + MSG_FIN + " >> " + RUTA_ARCHIVO);
            } else {
                builder5.command("/bin/sh", "-c", "echo '" + MSG_FIN + "' >> " + RUTA_ARCHIVO);
            }

            Process p5 = builder5.start();
            int exitCodeP5 = p5.waitFor();

            if (exitCodeP5 != 0) {
                System.err.println("ERROR P5: Fallo al añadir cierre. Código: " + exitCodeP5);
                return;
            }
            System.out.println("P5: Información de cierre añadida. Código: " + exitCodeP5);
        
        } catch (IOException e) {
            // Captura errores de E/S (ej. ruta de archivo incorrecta, comando no encontrado)
            System.err.println("Excepción de E/S (IOException). La operación de archivo falló: " + e.getMessage());
        } catch (InterruptedException e) {
            // Captura errores si el hilo de Java es interrumpido mientras espera un proceso
            System.err.println("Excepción de Interrupción. Proceso interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt(); // Reestablecer el estado de interrupción
        }
        
        System.out.println("--- FIN DEL PROCESO DE ARCHIVO ---");
    }
}