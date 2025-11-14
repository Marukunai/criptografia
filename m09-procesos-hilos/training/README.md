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