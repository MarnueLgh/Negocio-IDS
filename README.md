**Universidad Nacional Autónoma de México**
**Facultad de Contaduría y Administración**

**Sistema de Compra, Venta e Inventario**

## 1. Descripción del proyecto

Sistema desarrollado para gestionar de manera integrada las operaciones de compras, ventas e inventario, manteniendo la consistencia de la información entre los distintos subsistemas.

## 2. Objetivo

Desarrollar un sistema que permita organizar y gestionar las operaciones de compra, venta y control de inventario mediante una API REST y una base de datos centralizada.

## 3. Tecnologías utilizadas

* Java 25 y Spring Boot 4.1 (Web MVC, Data JPA, Validation, Actuator).
* MySQL 8 (H2 en memoria para pruebas).
* Log4j 2 para bitácoras.
* Maven Wrapper para la construcción.
* Git, GitHub y GitHub Actions (integración continua).
* Extreme Programming (XP) y Scrum como metodologías de desarrollo.

## 4. Subsistemas

El proyecto se organiza en tres subsistemas:

* **Compras:** gestión de las operaciones correspondientes a la adquisición de productos.
* **Ventas:** gestión de las operaciones relacionadas con la venta de productos.
* **Inventario:** control de existencias y movimientos de productos.

Los tres subsistemas deberán mantener la compatibilidad de sus estructuras y operaciones compartidas.

## 5. Requisitos previos

* JDK 21 o superior.
* MySQL 8.0 o superior.
* Git 2.40 o superior.
* No es necesario instalar Maven: el proyecto incluye el wrapper (`mvnw`).

## 6. Instalación y configuración

1. Clonar el repositorio.
2. Crear la base de datos y el usuario en MySQL:

   ```sql
   CREATE DATABASE scvi CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   CREATE USER 'user_scvi_app'@'localhost' IDENTIFIED BY '<tu_clave>';
   GRANT ALL PRIVILEGES ON scvi.* TO 'user_scvi_app'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. Copiar `scvi/.env.example` como `scvi/.env` y escribir el usuario y la clave. El archivo `.env` no se versiona.

## 7. Ejecución

Todos los comandos se ejecutan desde la carpeta `scvi/`.

| Acción | Windows | Mac/Linux |
|---|---|---|
| Ejecutar la aplicación | `.\mvnw.cmd spring-boot:run` | `./mvnw spring-boot:run` |
| Correr las pruebas | `.\mvnw.cmd test` | `./mvnw test` |
| Compilar y verificar (igual que el CI) | `.\mvnw.cmd clean verify` | `./mvnw clean verify` |
| Generar el JAR | `.\mvnw.cmd package` | `./mvnw package` |

Para comprobar la conexión a la base de datos, abrir <http://localhost:8081/actuator/health> y verificar `"db": {"status": "UP"}`.

Las bitácoras se escriben en consola y en `scvi/logs/` (`scvi.log` y `scvi-error.log`).

## 8. Estructura del proyecto

```
scvi/
├── .env.example                      # Plantilla de credenciales (copiar como .env)
├── mvnw, mvnw.cmd                    # Maven Wrapper
├── pom.xml                           # Dependencias y construcción
└── src/
    ├── main/
    │   ├── java/mx/unam/fca/scvi/
    │   │   └── ScviApplication.java          # Clase de arranque
    │   └── resources/
    │       ├── application.properties        # Configuración común (BD, puerto 8081, Actuator)
    │       └── log4j2-spring.xml             # Configuración de bitácoras
    └── test/
        ├── java/mx/unam/fca/scvi/
        │   └── ScviApplicationTests.java     # Prueba de arranque del contexto
        └── resources/
            └── application-test.properties   # Perfil test: H2 en memoria
```

Las clases nuevas van en subpaquetes de `mx.unam.fca.scvi` (por ejemplo, `mx.unam.fca.scvi.inventario`); fuera de ese paquete Spring no las detecta.

## 9. Base de datos

El sistema utiliza MySQL para almacenar la información compartida entre los subsistemas.

* **Script de creación:** [ruta del script SQL].
* **Tablas principales:** [indicar las tablas].
* **Relaciones compartidas:** [describir las relaciones relevantes entre compras, ventas e inventario].

Los cambios en la estructura de la base de datos deberán coordinarse entre los equipos involucrados.

## 10. Pruebas

Las funcionalidades deberán probarse antes de integrar cambios al proyecto compartido.

Se deberán verificar las operaciones principales, las validaciones, el acceso a la base de datos y la compatibilidad entre los subsistemas.

Las pruebas usan el perfil `test` con una base H2 en memoria, por lo que no requieren MySQL ni el archivo `.env`. Se ejecutan con `./mvnw test` (o `.\mvnw.cmd test` en Windows).

GitHub Actions ejecuta `./mvnw clean verify` en cada pull request hacia `main`; el PR no puede fusionarse si falla.

## 11. Control de versiones y colaboración

El proyecto utiliza Git y GitHub para gestionar el código fuente y coordinar el trabajo de los equipos.

Las modificaciones deberán desarrollarse y revisarse conforme a las convenciones establecidas para el proyecto.

## 12. Documentación

* **[Convenciones de desarrollo](CONVENCIONES.md):** contiene las reglas compartidas de programación, nomenclatura, documentación, base de datos, integración y control de versiones.

## 13. Problemas conocidos

[Registrar las limitaciones, errores pendientes o requisitos adicionales identificados durante el desarrollo.]


El objetivo final será que cualquier integrante pueda tomar el código de Compras, Ventas o Inventario y comprenderlo sin depender de la persona que originalmente lo escribió.

---

**Fin del documento**
