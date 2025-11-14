package training;

import java.util.concurrent.CountDownLatch;

public class TareaConteoSincronizado {
    private static final int NUM_WORKERS = 4;
    // Solución real de la suma del 1 al 1000: (1000 * 1001 / 2) = 500500
    // Resultado total esperado (4 workers): 4 * 500500 = 2002000

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(NUM_WORKERS);
        Resultado resultadoGlobal = new Resultado();

        System.out.println("--- INICIO DE TAREAS ---");

        // 2. Creación e inicio de los 4 Workers
        for (int i = 1; i <= NUM_WORKERS; i++) {
            Thread workerThread = new Thread(new Worker("Worker-" + i, latch, resultadoGlobal));
            workerThread.start();
        }

        // 3. Creación e inicio del Collector
        Thread collectorThread = new Thread(new Collector(latch, resultadoGlobal));
        collectorThread.start();

        // Esperamos al Collector para que el programa principal no termine antes
        try {
            collectorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("--- PROGRAMA FINALIZADO ---");
    }
}

// =======================================================
// CLASE A: RECURSO COMPARTIDO (Resultado.java)
// =======================================================
class Resultado {
    private long sumaTotal = 0;

    // Aquí se utiliza 'synchronized' para asegurar que la actualización de sumaTotal 
    // sea una operación atómica y sin condiciones de carrera.
    public synchronized void añadirResultado(long resultadoParcial) {
        this.sumaTotal += resultadoParcial;
        System.out.println(Thread.currentThread().getName() + " añadió un resultado parcial.");
    }

    public long getSumaTotal() {
        return sumaTotal;
    }
}

// =======================================================
// CLASE B: WORKER (Implementa Runnable)
// =======================================================
class Worker implements Runnable {
    private final String name;
    private final CountDownLatch latch;
    private final Resultado resultadoGlobal;

    public Worker(String name, CountDownLatch latch, Resultado resultadoGlobal) {
        this.name = name;
        this.latch = latch;
        this.resultadoGlobal = resultadoGlobal;
    }

    @Override
    public void run() {
        System.out.println(name + " está iniciando el cálculo...");
        long sumaParcial = 0;
        
        // Cálculo (suma del 1 al 1000)
        for (int i = 1; i <= 1000; i++) {
            sumaParcial += i;
        }
        
        // Simulación de trabajo:
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 1. Añadir el resultado parcial al objeto compartido (Uso de synchronized)
        resultadoGlobal.añadirResultado(sumaParcial);

        // 2. Avisar al Latch que la tarea ha terminado (Uso de CountDownLatch)
        latch.countDown(); 
        System.out.println(name + " ha terminado. Latch restante: " + latch.getCount());
    }
}

// =======================================================
// CLASE C: COLLECTOR (Implementa Runnable)
// =======================================================
class Collector implements Runnable {
    private final CountDownLatch latch;
    private final Resultado resultadoGlobal;

    public Collector(CountDownLatch latch, Resultado resultadoGlobal) {
        this.latch = latch;
        this.resultadoGlobal = resultadoGlobal;
    }

    @Override
    public void run() {
        System.out.println("Collector esperando a que los 4 Workers terminen...");
        try {
            // BLOQUEO: Espera hasta que el contador del Latch llegue a cero.
            latch.await(); 
            
            // Una vez desbloqueado, el Collector puede acceder al resultado final
            System.out.println("\n--- ¡TAREAS COMPLETADAS! ---");
            System.out.println("El Collector ha recopilado todos los resultados.");
            System.out.println("La suma total de los 4 Workers es: " + resultadoGlobal.getSumaTotal());
            
        } catch (InterruptedException e) {
            System.err.println("El Collector fue interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}