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
├── sql/                              # Scripts de creación de tablas (ejecutar en orden)
└── src/
    ├── main/
    │   ├── java/mx/unam/fca/scvi/
    │   │   ├── ScviApplication.java          # Clase de arranque
    │   │   └── inventario/                   # Subsistema de Inventario
    │   │       ├── modelo/                   # Entidades, enums y DTOs
    │   │       ├── repositorio/              # Interfaces de Spring Data JPA
    │   │       ├── servicio/                 # Lógica de negocio y excepciones
    │   │       └── controlador/              # API REST
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

* **Scripts de creación:** carpeta `scvi/sql/`, numerados en orden de ejecución. Se ejecutan a mano en MySQL antes de arrancar la aplicación, porque `ddl-auto=validate` solo comprueba que las tablas coincidan con las entidades.
  * `01_crear_articulo.sql`: tabla `Articulo` (INV-04; columna `activo` agregada en INV-07).
  * `02_crear_bodega.sql`: tabla `Bodega` con dos bodegas iniciales (INV-07).
  * `03_crear_recepcion.sql`: tabla `Recepcion` (INV-07).
* **Tablas principales:**
  * `Articulo`: catálogo de artículos. Su llave `articuloId` es el "ID artículo" que usan las demás historias.
  * `Bodega`: bodegas donde se recibe la mercancía (estructura mínima: `bodegaId`, `nombre`).
  * `Recepcion`: llegadas de mercancía a una bodega.
* **Relaciones:** `Recepcion` → `Articulo` (`fkRecepcionArticulo`) y `Recepcion` → `Bodega` (`fkRecepcionBodega`), ambas muchos a uno.

Hibernate usa `PhysicalNamingStrategyStandardImpl`, así que los nombres de tablas y columnas son idénticos a los de las entidades (regla 7). Sin esa configuración, `codigoBarras` se convertiría en `codigo_barras`.

### Reglas de negocio de `Articulo` (INV-04)

* El código de barras es único (`uqArticuloCodigoBarras`); un duplicado responde HTTP 409.
* Un artículo `PERECEDERO` exige fecha de caducidad. En uno `NO_PERECEDERO` la fecha se descarta.
* Las existencias, el precio y el costo no pueden ser negativos, y las existencias mínimas no pueden superar a las máximas.
* `proveedor` es texto libre hasta que Compras cree la tabla `Proveedor`; entonces pasará a llave foránea.
* `precio` y `costo` usan `BigDecimal` en Java y `DECIMAL(12,2)` en MySQL, como recomienda el manual de JPA de la materia. Esto difiere de la regla 4.11 (Double), por lo que se propondrá el cambio a los demás equipos.

API (`/api/inventario/articulos`):

| Método | Ruta | Respuesta |
|---|---|---|
| `POST` | `/api/inventario/articulos` | 201 con el artículo creado; 400 si los datos son inválidos; 409 si el código de barras ya existe |
| `GET` | `/api/inventario/articulos` | 200 con la lista de artículos |
| `GET` | `/api/inventario/articulos/{articuloId}` | 200 con el artículo; 404 si no existe |

### Reglas de negocio de `Recepcion` (INV-07)

* Toda recepción exige artículo, bodega destino, cantidad mayor que cero, fecha de recepción (no futura), ubicación y estado.
* La mercancía entra siempre a una `Bodega`: `Recepcion` solo tiene llave foránea hacia `Bodega` y no hacia `Almacen`.
* El artículo debe existir (404) y estar activo (400). La bodega debe existir (404).
* Si el artículo es `PERECEDERO`, se exigen lote y fecha de caducidad posterior a la fecha de recepción (400). En uno `NO_PERECEDERO` ambos se descartan.
* `estado` es la condición de la mercancía: `BUENO` suma la cantidad a `Articulo.existencias`; `DANADO` queda registrado pero no suma.
* Guardar la recepción y sumar las existencias ocurren en una sola transacción. La suma se hace con un `UPDATE` atómico para que dos recepciones simultáneas no se pisen.
* Superar `existenciasMaximas` no bloquea la recepción, porque la mercancía ya está físicamente en la bodega: se registra una advertencia en la bitácora y la respuesta trae `excedeMaximo: true`. El tope corresponde a Compras, al generar la orden.
* `ubicacion` es texto libre (p. ej. "Pasillo 3, Anaquel B") hasta que exista la tabla `Anaquel`.
* El stock es global por artículo (`Articulo.existencias`); el stock por bodega se agregará con la historia de traspaso Bodega → Almacén.
* La API responde con `RecepcionRespuestaDto` y nunca expone la entidad `Recepcion`.

API (`/api/inventario/recepciones`):

| Método | Ruta | Respuesta |
|---|---|---|
| `POST` | `/api/inventario/recepciones` | 201 con la recepción creada; 400 si los datos son inválidos, el artículo está inactivo o falta lote/caducidad de un perecedero; 404 si el artículo o la bodega no existen |
| `GET` | `/api/inventario/recepciones` | 200 con la lista de recepciones |

Ejemplo de cuerpo para `POST`:

```json
{
  "articuloId": 1,
  "bodegaId": 1,
  "cantidad": 20,
  "fechaRecepcion": "2026-11-09",
  "ubicacion": "Pasillo 3, Anaquel B",
  "estado": "BUENO",
  "lote": "L-2026-11",
  "fechaCaducidad": "2026-12-31"
}
```

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
