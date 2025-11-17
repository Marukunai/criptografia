# 📚 Documentación de Repaso: Hilos y Procesos

Esta documentación cubre dos ejercicios clave de concurrencia y gestión de procesos externos, diseñados para repasar conceptos críticos para el examen.

---

## 1. Ejercicio de Hilos: Concurrencia y Sincronización

### 📝 Objetivo

Implementar 4 hilos **Worker** que realizan un cálculo y un hilo **Collector** que debe esperar a que los 4 Workers terminen antes de imprimir el resultado final.

### 🔑 Conceptos Clave Repasados

* **`implements Runnable`**: Uso de la interfaz `Runnable` para definir la tarea.
* **`CountDownLatch`**: Mecanismo de sincronización ideal para esperar a que un número *N* de eventos (hilos) se completen.
* **`latch.countDown()`**: Llamado por los Workers para indicar que han terminado.
* **`latch.await()`**: Llamado por el Collector para bloquearse hasta que el contador llegue a cero.
* **`synchronized`**: Utilizado en el método `añadirResultado` para proteger el recurso compartido (`sumaTotal`) y evitar **condiciones de carrera**.

--- 

## 2. Ejercicio de Procesos: Control Condicional y Logging

### 📝 Objetivo

Ejecutar un proceso externo (**`ping`**) y, **solo si falla**, ejecutar una secuencia de dos comandos (**`mkdir`** y **`echo`**) para crear un directorio de logs e insertar una entrada, resolviendo problemas de codificación y compatibilidad con el Sistema Operativo (SO).

### 🔑 Conceptos Clave Repasados

* **`ProcessBuilder`**: Clase fundamental para construir el comando y sus argumentos antes de la ejecución.
* **`Process.waitFor()`**: Método esencial para **bloquear** el hilo principal de Java hasta que el proceso externo termine (análogo a `join()` en hilos).
* **`Process.exitValue()`**: Se utiliza para obtener el **código de salida** del proceso. Un valor de **`0`** significa éxito; cualquier valor **distinto de `0`** significa un fallo o error.
* **Compatibilidad OS**: Se debe diferenciar la sintaxis de comandos entre **Windows** (`cmd /c`, `ping -n 1`) y **Unix/Linux/Mac** (`/bin/sh -c`, `ping -c 1`).
* **Redirección (`>>`)**: Usado en el comando `echo` para **añadir** texto al archivo de log existente (en lugar de sobrescribirlo con `>`).
* **Solución de Codificación**: Uso de **`powershell.exe Add-Content`** en Windows para evitar que caracteres acentuados (como la `ó`) se corrompan en el archivo de log (problema común con la codificación de `cmd /c echo`).

---

## 3. Ejercicio de Procesos: Secuencia Lógica y Redirección de Salida

### 📝 Objetivo

Implementar una secuencia lógica de **cinco procesos** encadenados para verificar una condición, crear un archivo, escribir información inicial, anexar la salida de un listado de directorio y finalmente cerrar el log. La ejecución debe detenerse inmediatamente si cualquiera de los procesos intermedios falla (código de salida distinto de cero).

### 🔑 Conceptos Clave Repasados

* **Encadenamiento Condicional:** Uso de un único bloque `try-catch` principal, con comprobaciones `if (exitCode != 0) return;` después de cada proceso. Esto simplifica la estructura de código y garantiza que un fallo interrumpa la secuencia completa.
* **Redirección de Shell (Sobrescritura):** Uso de `echo ... > archivo` (Proceso 3) para sobrescribir el contenido y colocar la línea de inicio.
* **Redirección de Shell (Anexión):** Uso de `echo ... >> archivo` (Proceso 5) para añadir el mensaje de cierre sin borrar el contenido anterior.
* **Redirección Nativa de Java:** Uso de **`ProcessBuilder.Redirect.appendTo(new File(RUTA))`** (Proceso 4) para redirigir la salida estándar de un proceso nativo (`dir` o `find`) directamente al final del archivo de log.
* **Compatibilidad OS:** Mantener la diferenciación correcta de comandos entre **Windows** (`dir`, `type nul >`, `echo >/>>`) y **Unix/Linux/Mac** (`ls`, `touch`, `echo >/>>`) en cada paso.

--- 

## 4. Ejercicio de Hilos: Productor-Consumidor con Buffer Limitado

### 📝 Objetivo

Implementar un hilo **Productor** que genera ítems y un hilo **Consumidor** que los procesa, utilizando un **Buffer compartido** con capacidad limitada. El objetivo es asegurar la **comunicación segura** y evitar que el Productor escriba si el buffer está lleno o que el Consumidor lea si está vacío.

### 🔑 Conceptos Clave Repasados

* **Patrón Productor-Consumidor:** Diseño fundamental para la comunicación y la transferencia de datos entre hilos de forma coordinada.
* **`synchronized`**: Utilizado para asegurar la **exclusión mutua** en los métodos de acceso al buffer (`poner` y `obtener`), permitiendo que solo un hilo modifique el recurso a la vez.
* **`wait()` y `notifyAll()`:** Mecanismos esenciales para la **comunicación y sincronización** entre hilos:
    * **`wait()`**: Bloquea el hilo actual y **libera el monitor** (el bloqueo `synchronized`), permitiendo que el otro hilo continúe.
    * **`notifyAll()`**: Despierta a todos los hilos que están en espera sobre este objeto para que puedan reevaluar su condición de continuación.
* **Bloqueo Condicional (`while`):** Es crucial el uso de `while (condición)` junto a `wait()` (ej. `while (buffer.isFull()) wait();`). Esto garantiza que, al ser despertado, el hilo **revalida la condición** antes de continuar, evitando errores como el *spurious wakeup* (despertar espurio).
* **Buffer Limitado:** La definición de una capacidad máxima (`CAPACIDAD_MAXIMA`) que fuerza a los hilos a esperar y alternar su acceso al recurso.