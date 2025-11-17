package training;

import java.util.LinkedList;
import java.util.List;

public class BufferCompartido {

    public static final int CAPACIDAD_MAXIMA = 5;
    private List<Integer> buffer = new LinkedList<>();

    public synchronized void poner(int valor) throws InterruptedException {
        while (buffer.size() == CAPACIDAD_MAXIMA) {
            System.out.println("Buffer lleno, productor esperando...");
            wait();
        }
        buffer.add(valor);
        System.out.println("Productor agregó: " + valor);
        notifyAll();
    }

    public synchronized int obtener() throws InterruptedException {
        
        while (buffer.isEmpty()) {
            System.out.println("Buffer vacío. Consumidor en espera...");
            wait();
        }
        
        int valor = buffer.remove(0); 
        System.out.println("Consumidor obtuvo: " + valor + ". Restantes: " + buffer.size());
        
        notifyAll();
        
        return valor;
    }

    public static void main(String[] args) {
        // 1. Crear el recurso compartido
        BufferCompartido buffer = new BufferCompartido();

        // 2. Crear los hilos, pasando la misma instancia del buffer
        Thread hiloProductor = new Thread(new Productor(buffer), "Productor-1");
        Thread hiloConsumidor = new Thread(new Consumidor(buffer), "Consumidor-1");

        System.out.println("--- INICIO SIMULACIÓN PRODUCTOR-CONSUMIDOR ---");
        
        // 3. Iniciar los hilos
        hiloProductor.start();
        hiloConsumidor.start();

        // 4. Esperar a que ambos hilos terminen (join)
        try {
            hiloProductor.join();
            hiloConsumidor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("--- FIN SIMULACIÓN. BUFFER FINAL: " + buffer.buffer.size() + " ---");
    }
}

// Clase Productor (Debe estar en el mismo archivo o su propio archivo Productor.java)
class Productor implements Runnable {
    
    private BufferCompartido buffer;
    
    // Constructor con argumento, crucial para compartir el recurso
    public Productor(BufferCompartido buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                // El productor pondrá los números 0 al 9
                buffer.poner(i);
                // Opcional: Pausa para simular trabajo
                // Thread.sleep(50); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Clase Consumidor (Debe estar en el mismo archivo o su propio archivo Consumidor.java)
class Consumidor implements Runnable {
    
    private BufferCompartido buffer;
    
    // Constructor con argumento, crucial para compartir el recurso
    public Consumidor(BufferCompartido buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int sumaTotal = 0;
        for (int i = 0; i < 10; i++) {
            try {
                int valorObtenido = buffer.obtener();
                sumaTotal += valorObtenido;
                // Opcional: Pausa para simular trabajo
                // Thread.sleep(100); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n*** Consumidor: Suma total de los 10 valores obtenidos: " + sumaTotal + " ***\n");
    }
}