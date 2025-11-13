# 🌐 Unidad Formativa 3: Programación de Sockets y Servicios de Red

---

## 1. Introducción a los Sockets

Un **Socket** es una interfaz de programación de aplicaciones (API) del paquete `java.net` que permite la **comunicación bidireccional** entre dos máquinas o procesos a través de una red.

---

## 2. Sockets TCP (Orientado a Conexión)

El **Protocolo de Control de Transmisión (TCP)** es confiable y orientado a conexión.

| Característica | Descripción |
| :--- | :--- |
| **Orientado a Conexión** | Requiere el establecimiento de una conexión previa (**Handshake**). |
| **Fiabilidad** | Garantiza que los datos se entreguen correctamente y en el orden adecuado. |
| **Control de Flujo/Congestión** | Regula la velocidad de envío para evitar la saturación. |

### 2.1. El Three-Way Handshake

1.  **SYN**: El Cliente envía solicitud de sincronización.
2.  **SYN-ACK**: El Servidor responde con sincronización y confirmación.
3.  **ACK**: El Cliente confirma, estableciendo la conexión.

### 2.2. La Clase `ServerSocket` (Lado del Servidor)

Clase fundamental para crear un servidor que espera conexiones.

| Método | Función | Notas |
| :--- | :--- | :--- |
| `ServerSocket(int port)` | Crea el socket en el puerto especificado. | El puerto debe estar libre. |
| `accept()` | **Bloqueante**. Espera hasta que un cliente se conecta, devolviendo un objeto `Socket`. | Usado típicamente en un bucle. |
| `setSoTimeout(int timeout)` | Establece un tiempo máximo de espera (en ms) para `accept()`. | Lanza `SocketTimeoutException` si expira. |
| `close()` | Cierra el `ServerSocket`, liberando el puerto. | Es esencial para evitar bloqueos. |
| `getLocalPort()` | Devuelve el número de puerto de escucha. | |

#### Ejemplo Servidor Básico (Texto)

```java
import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Servidor iniciado. Esperando conexiones...");
            Socket clienteSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());
            
            // Flujo para recibir texto
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            String mensaje = entrada.readLine();
            System.out.println("Mensaje recibido: " + mensaje);
            
            entrada.close();
            clienteSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 2.3. Sockets TCP con Serialización de Objetos

Permite la transmisión de objetos complejos a través de la red mediante su conversión a una secuencia de bytes.

#### Conceptos Clave

* **`Serializable`**: Interfaz de marcador. La clase del objeto a enviar debe implementarla (`public class Persona implements Serializable`).
* **`ObjectOutputStream`**: Se utiliza en el **Cliente** para **escribir** (serializar) objetos.
* **`ObjectInputStream`**: Se utiliza en el **Servidor** para **leer** (deserializar) objetos usando `readObject()`.

#### Flujo de Ejemplo con Serialización

| Componente | Clase Utilizada | Operación Clave |
| :--- | :--- | :--- |
| **Cliente** | `ObjectOutputStream` | `salida.writeObject(objeto)` |
| **Servidor** | `ObjectInputStream` | `(Tipo) entrada.readObject()` |

#### Ejemplo de Clase Serializable (`Persona.java`)

```java
import java.io.Serializable;

public class Persona implements Serializable {
    private String nombre;
    private int edad;

    public Persona(String nombre, int edad) { /* ... */ }
    public String toString() { /* ... */ return "Persona{...}"; }
}
```

---

### 3. Sockets UDP (Sin Conexión)

El **Protocolo de Datagramas de Usuario (UDP)** es rápido y sin conexión, con baja sobrecarga y **sin garantías** de entrega u orden. Los paquetes se llaman **Datagramas**.

| Característica | Descripción |
| :--- | :--- |
| **Sin Conexión** | No requiere establecimiento de conexión (no hay Handshake). |
| **Sin Garantías** | No garantiza la entrega, el orden o la integridad. |
| **Uso Típico** | Streaming, VoIP, Juegos. |

#### 3.1. Clases clave en Java (UDP)

| Clase | Uso |
| :--- | :--- |
| **`DatagramSocket`** | Se usa en Cliente y Servidor para enviar y recibir datagramas. |
| **`DatagramPacket`** | Representa el paquete de datos, incluyendo la información, la dirección IP y el puerto. |

---

### 4. Comparativa Clave (TCP vs. UDP)

| Característica | TCP | UDP |
| :--- | :--- | :--- |
| **Conexión** | Orientado a Conexión | Sin Conexión |
| **Fiabilidad** | Alta | Baja |
| **Velocidad** | Más Lento | Más Rápido |
| **Uso Típico** | HTTP, FTP, Archivos | Streaming, Juegos online, DNS |

---

### 5. Servidores Concurrentes (Multithreading)

Para que un servidor TCP atienda múltiples clientes simultáneamente, se utiliza la programación concurrente (UF2):

```java
while (true) {
    Socket clienteSocket = serverSocket.accept(); 
    // Delega la comunicación a un nuevo hilo por cada cliente
    new Thread(new ManejadorCliente(clienteSocket)).start(); 
}