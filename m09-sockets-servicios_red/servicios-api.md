# 🌐 UF3 - Parte II: Sockets Avanzados y API REST (Spring Boot)

---

## 1. Programación en Red con Sockets (Repaso y Código)

La comunicación en red en Java se implementa principalmente a través de las clases **`Socket`** (lado cliente) y **`ServerSocket`** (lado servidor), utilizando el protocolo **TCP/IP**.

### 1.1. Estructura Cliente-Servidor Básica

| Clase | Función | Puerto | Notas |
| :--- | :--- | :--- | :--- |
| **`ServerSocket`** | Servidor: Espera y acepta conexiones. | Abierto (ej: 8000) | El método `accept()` es **bloqueante** hasta recibir una petición. |
| **`Socket`** | Cliente: Crea la conexión al servidor. | Específico (ej: 8000) | Se utiliza `new Socket(host, puerto)` para iniciar la conexión. |

#### Servidor Básico (Escucha)

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(8000);
        System.out.println("Servidor iniciado");
        while (true) {
            Socket socket = servidor.accept();
            System.out.println("Conexión aceptada desde " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            socket.close(); // Cierra la conexión después de aceptar
            System.out.println("Conexión cerrada");
        }
    }
}
```

#### Cliente Básico (Conexión)

```java
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
    public static void main(String[] args) throws UnknownHostException, IOException {
        String host = "localhost";
        int puerto = 8000;
        Socket socket = new Socket(host, puerto);
        System.out.println("Conectado a " + host + ":" + puerto);
        socket.close(); // Cierra la conexión
        System.out.println("Conexión cerrada");
    }
}
```

### 1.2. Interfaz `Serializable` y Objetos en Red

Para transferir estructuras de datos complejas (objetos) a través de la red, la clase del objeto debe implementar la interfaz **`Serializable`**.

#### Flujo de Serialización

| Paso | Componente | Clase Utilizada | Operación Clave |
| :--- | :--- | :--- | :--- |
| **Modelo** | Objeto Modelo | Implementa `Serializable`. | N/A |
| **Envío** | Cliente | `ObjectOutputStream` | `oos.writeObject(objeto)` |
| **Recepción** | Servidor | `ObjectInputStream` | `(MiObjeto) ois.readObject()` |

#### Objeto Modelo (`MiObjeto.java`)

```java
class MiObjeto implements Serializable {
    private String mensaje;
    public MiObjeto(String mensaje) { this.mensaje = mensaje; }
    @Override
    public String toString() { return "MiObjeto [mensaje=" + mensaje + "]"; }
}
```

#### Envío de Objeto Serializado (Cliente)

```java
import java.io.*;
import java.net.*;

public class EnviarObjetoSerializado {
    public static void main(String[] args) {
        try {
            MiObjeto objeto = new MiObjeto("Hola mundo!");
            Socket socket = new Socket("192.168.1.100", 3000); // IP y puerto
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(objeto);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### Recepción de Objeto Serializado (Servidor)

```java
import java.io.*;
import java.net.*;

public class RecibirObjetoSerializado {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(3000);
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MiObjeto objeto = (MiObjeto) ois.readObject();
            System.out.println(objeto);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## 2. API REST con Spring Boot

### 2.1. Conceptos Fundamentales

* **API (Application Programming Interface)**: Conjunto de definiciones que permite la comunicación entre dos aplicaciones.
* **API REST (Representational State Transfer)**: Interfaz sin estado que utiliza **HTTP** para obtener y generar datos (**JSON** o XML).
* **Servidor de Aplicaciones (Tomcat)**: Convierte un servidor en remoto. **Spring Boot** lo integra por defecto.

#### Acceso Local
Con Tomcat integrado y arrancado en el puerto 8080:
`http://localhost:8080` (o `127.0.0.1:8080`)

---

### 2.2. Flujo de Funcionamiento REST

La API utiliza **URLs activas (Endpoints)** en lugar de un `main()`. El flujo de datos es:

**API (Vista)** $\rightarrow$ **Service (Lógica)** $\rightarrow$ **Database (Datos)**, y el retorno en orden inverso.

#### Estructura de la URL del Servicio
`<host>:<port>/<app_path>/<service_path>`

**Ejemplo**: `http://localhost:8080/nombreRequestMapping/nombreServiceMapping`
* `/nombreRequestMapping`: Del tag **`@RequestMapping`** (o `@Path`) de la clase API.
* `/nombreServiceMapping`: Del tag del método (ej: `@GetMapping`).

---

### 2.3. Métodos HTTP (Verbos REST)

Los verbos definen la acción sobre el recurso:

| Verbo | Tag Java | Objetivo | Código Retorno Típico |
| :--- | :--- | :--- | :--- |
| **GET** | `@GET / @GetMapping` | **Recibir** información (Consultar). | **200 (OK)** |
| **POST** | `@POST / @PostMapping` | **Crear** un nuevo recurso. | **201 (Created)** |
| **PUT** | `@PUT / @PutMapping` | **Crear o Actualizar**. | 200 (OK) o 201 (Created) |
| **DELETE** | `@DELETE / @DeleteMapping` | **Eliminar** un recurso. | 200 (OK) |

---

### 2.4. Parámetros de Petición

#### `@QueryParam` (Parámetros de Consulta)
Se usan para filtrar o valores opcionales, separados por `?` y `&`.
**URL**: `http://myapi.com/customers?firstname=oscar&lastname=blancarte`

```java
@GET
@Path("/api/test")
public String getTest(@QueryParam("id") String id) {
    return "ID: " + id; // Para URL: http://.../api/test?id=abc
}
```

#### `@PathParam` (Parámetros de Ruta)
Se usan para acceder a recursos concretos. Se definen en la ruta usando llaves `{}` y son obligatorios.
**URL**: `http://localhost:8080/api/test/abc`

```java
@GET
@Path("/api/test/{id}")
public String getTest(@PathParam String id) {
    return "ID: " + id; // El valor 'abc' se mapea contra 'id'
}
```

#### Body (Cuerpo de la Petición)
Usado por **POST** y **PUT** para enviar objetos complejos (**JSON**), que se mapean automáticamente al objeto modelo en Java.

**Petición JSON:**
```json
{
    "id": 1,
    "name": "Asus",
    "price": 900
}
```

#### Mapeo en el Endpoint

```java
@POST
@Path("/api/insert/producto")
public String getTest(Producto producto) { // Se mapea automáticamente
    return producto;
}
```

### 2.5. Estructura de la Aplicación y Retorno

La arquitectura de una aplicación API REST típicamente sigue un patrón de capas (Configuración, Vista/API, Modelo y Lógica/Servicio).

| Package | Clase de Ejemplo | Etiqueta Clave | Propósito |
| :--- | :--- | :--- | :--- |
| **`config`** | `HelloApplication` | `@ApplicationPath("/api")` | Define la ruta inicial genérica de la API (prefijo de todas las rutas). |
| **`api`** | `ProductoAPI` | `@Path("/producto")` | Contiene los **ENDPOINTS** (la capa de **vista**). |
| **`model`** | `Producto` | `@JsonProperty("name")` | Clases de datos. Usan `@JsonProperty` para mapear el **JSON**. |
| **`service`** | `ServiceManager` | N/A | Contiene la lógica de negocio y gestiona la comunicación con la capa de datos. |

#### Ejemplo de Estructura API (Vista)

La clase API define la ruta base (`@Path`) y los tipos de datos que consume y produce (`@Produces`, `@Consumes`).

```java
@Path("/producto")
@Produces("application/json;charset=UTF-8")
@Consumes("application/json;charset=UTF-8")
public class ProductoAPI {
    // ... inyección de serviceManager

    @GET
    @Path("{id}")
    public Response getProductoID(@PathParam("id") int id) {
        return serviceManager.getProductoById(id);
    }
}
```

#### Retorno (`Response`)

Los Endpoints deben retornar objetos de tipo `Response` para controlar el código de estado HTTP y el cuerpo de la respuesta.

| Código de Estado | Ejemplo de Creación | Descripción |
| :--- | :--- | :--- |
| **Éxito (200 OK)** | `Response.ok(producto).build();` | Operación completada con éxito. |
| **Error Cliente (400 Bad Request)** | `Response.status(Response.Status.BAD_REQUEST).entity("El parámetro no es válido.").build();` | Error en la petición del cliente (e.g., parámetro incorrecto). |
| **Error Servidor (500 Internal Error)** | `Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();` | Error interno del servidor (e.g., fallo de base de datos). |

**IMPORTANTE**: La capa **`service`** o controladores debe ser la encargada de construir y retornar el objeto **`Response`** para centralizar la gestión de excepciones y códigos de estado.

### 2.6. Configuración de Base de Datos y Persistencia

Para interactuar con bases de datos relacionales, Spring Boot utiliza **Spring Data JPA** (Java Persistence API), que implementa el patrón **Repository** sobre Hibernate.

#### El Archivo `application.properties`

Este archivo centraliza la configuración de la conexión a la base de datos y el comportamiento de Hibernate.

| Propiedad | Función |
| :--- | :--- |
| `spring.jpa.hibernate.ddl-auto` | Controla cómo Hibernate modifica el esquema de la DB al inicio (DDL). |
| `spring.datasource.url` | URL de conexión de la base de datos (JDBC driver, host, puerto, schema). |
| `spring.datasource.username` | Usuario de la base de datos. |
| `spring.datasource.password` | Contraseña del usuario. |
| `spring.datasource.driver-class-name` | Clase del driver JDBC específico (ej: `com.mysql.cj.jdbc.Driver`). |
| `spring.jpa.show-sql` | Si es `true`, muestra todas las sentencias SQL ejecutadas en la consola. |

#### Valores Clave para `ddl-auto`

| Valor | Efecto | Advertencia |
| :--- | :--- | :--- |
| **`none`** | No modifica el esquema (Recomendado para **producción**). | N/A |
| **`update`** | Actualiza el esquema si hay cambios en las entidades. No elimina datos. | Recomendado en **desarrollo**. |
| **`create`** | Elimina y recrea la base de datos (se **pierden datos**). | Peligroso, solo para pruebas. |
| **`create-drop`** | Crea al inicio y elimina al finalizar la aplicación. | Para **tests unitarios**. |

#### Ejemplo de Configuración

```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_del_schema?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password_segura
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true
```

### 2.7. La Capa Repository (Spring Data JPA)

Spring Data JPA simplifica el acceso a datos. Permite definir la capa de persistencia mediante interfaces que heredan de **`JpaRepository`**, proporcionando métodos CRUD listos para usar sin necesidad de implementación manual.

| Etiqueta/Clase | Función |
| :--- | :--- |
| **`@Entity`** | Marca la clase como una tabla de la base de datos (clase de dominio). |
| **`@Id`** | Define el campo clave primaria de la tabla. |
| **`@Repository`** | Marca la interfaz que Spring debe gestionar para el acceso a datos. |
| **`JpaRepository<T, ID>`** | Interfaz base que proporciona todos los métodos CRUD básicos (`save()`, `findById()`, `findAll()`, `delete()`). |

#### Ejemplo de Entidad y Repositorio

```java
// Entidad (Model)
@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private double precio;
    // ... getters, setters
}

// Repositorio (Data Layer)
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Spring genera automáticamente la implementación de este método por convención:
    List<Producto> findByNombreContaining(String nombre); 
}
```

### 2.8. Compilación y Arranque de Spring Boot 🚀

El proceso de arranque de Spring Boot es sencillo gracias a que incluye un **servidor web embebido** (normalmente **Tomcat**).

* **Clase Principal**: Es la clase que contiene la etiqueta **`@SpringBootApplication`** y el método `main()`. Este es el punto de entrada de la aplicación.
* **Arranque Integrado**: Al ejecutar el método `main()`, Spring Boot automáticamente:
    * Arranca el servidor web embebido (Tomcat por defecto).
    * Carga y configura todos los componentes de la aplicación (Controladores, Servicios, Repositorios).
    * Aplica las configuraciones definidas en el archivo `application.properties`.
* El servidor estará operativo en **`http://localhost:8080`** (o el puerto configurado).

---

## 3. Pruebas y Consumo de Endpoints (Postman)

**Postman** es la herramienta estándar para verificar el funcionamiento de los Endpoints de la API enviando peticiones HTTP.

### 3.1. Funcionalidad Clave de Postman

| Elemento | Función |
| :--- | :--- |
| **Método HTTP** | Selecciona el verbo de la petición: **GET, POST, PUT, DELETE**. |
| **URL (Endpoint)** | Introduce la ruta completa (ej: `http://localhost:8080/api/productos/1`). |
| **Pestaña Body** | Se usa para peticiones **POST/PUT**. Permite escribir el **JSON** o XML a enviar (formato `raw`, tipo `JSON`). |
| **Ventana de Respuesta** | Muestra el **código de estado HTTP** (200 OK, 404 Not Found, etc.) y el cuerpo de la respuesta. |

### 3.2. Mapeo de Parámetros en Postman

| Parámetro Spring | Ejemplo de Endpoint | Uso en Postman |
| :--- | :--- | :--- |
| **`@RequestParam`** | `/productos?filter=name` | Introducir la clave (`filter`) y valor (`name`) en la pestaña **Params**. |
| **`@PathVariable`** | `/productos/123` | Escribir el valor (`123`) directamente en la **URL**. |
| **`@RequestBody`** | `/productos` (con POST) | Escribir el **JSON** en la pestaña **Body** (para enviar objetos). |