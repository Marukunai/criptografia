// 1. EL RECURSO COMPARTIDO
class RecursoCompartido {
    private int dato = 0;
    private boolean ocupado = false; // false = libre para escribir, true = hay dato para leer

    public synchronized void modificar(int nuevoValor) {
        // Si está ocupado (ya escribieron y nadie ha leído), espero
        while (ocupado) { 
            try { wait(); } catch (InterruptedException e) { }
        }

        // Escribo
        dato = nuevoValor;
        ocupado = true; // Marco como ocupado para que el lector sepa que hay algo
        System.out.println("Escritor puso: " + dato);
        
        notifyAll(); // Aviso al lector
    }

    public synchronized int leer() {
        // Si NO está ocupado (no hay nada nuevo escrito), espero
        while (!ocupado) {
            try { wait(); } catch (InterruptedException e) { }
        }

        // Leo
        ocupado = false; // Libero para que el escritor pueda volver a escribir
        System.out.println("Lector leyó: " + dato);
        notifyAll(); // Aviso al escritor
        
        return dato;
    }
}

// 2. TAREA DE ESCRIBIR (Productor)
class TareaEscritor implements Runnable {
    private RecursoCompartido recurso;
    public TareaEscritor(RecursoCompartido r) { this.recurso = r; }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) { // Escribimos 5 veces
            recurso.modificar(i);
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }
}

// 3. TAREA DE LEER (Consumidor)
class TareaLector implements Runnable {
    private RecursoCompartido recurso;
    public TareaLector(RecursoCompartido r) { this.recurso = r; }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) { // Leemos 5 veces
            recurso.leer();
            try { Thread.sleep(150); } catch (InterruptedException e) {}
        }
    }
}

// 4. MAIN
public class MainHilos {
    public static void main(String[] args) {
        RecursoCompartido recursoComun = new RecursoCompartido();

        // IMPORTANTE: Un hilo escribe y el otro lee
        Thread tEscritor = new Thread(new TareaEscritor(recursoComun));
        Thread tLector = new Thread(new TareaLector(recursoComun));

        tEscritor.start();
        tLector.start();

        try {
            tEscritor.join();
            tLector.join();
        } catch (InterruptedException e) {}
        
        System.out.println("--- Fin del programa ---");
    }
}