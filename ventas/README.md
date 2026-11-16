# SCVI – Subsistema de Ventas

API REST (MVC sin vista) del subsistema **Ventas** del monolito modular SCVI.
Spring Boot 3.3.5 · Java 21 · Maven · H2 (archivo) · Jakarta Persistence (JPA/Hibernate) + Lombok · patrón DAO.

```
controller  ->  service  ->  dao (EntityManager)  ->  BD (H2)
                              model = entidades @Entity (Jakarta + Lombok)
                   |
                   +-> integracion (InventarioGateway, síncrono)
                   +-> evento      (VentaDirectaRegistrada / VentaDirectaCancelada, en proceso)
```

Las dependencias solo van hacia abajo. Cada petición pasa por `TrazabilidadFilter`
(cabecera `X-Trace-Id`, que también aparece en cada línea del log).

## Cómo correrlo (IntelliJ IDEA)

1. `File > Open` y elegir la carpeta del proyecto (detecta `pom.xml`; JDK 21). Lombok: el plugin viene incluido en
   IntelliJ; solo confirma `Settings > Build > Compiler > Annotation Processors > Enable annotation processing`.
2. Ejecutar `VentasApplication` (o `mvn spring-boot:run`). Puerto `8080`.
3. Pruebas: `mvn test` (usan H2 en memoria y hacen rollback; no tocan `./data`).

### Base de datos provisional (dentro del editor)

H2 en archivo: `./data/ventasdb` (se crea sola y está en `.gitignore`).

* **Desde IntelliJ**: panel *Database* > `+` > *Data Source* > *H2* > *Remote* (o *Embedded*)
  URL `jdbc:h2:tcp://localhost/./data/ventasdb`, usuario `sa`, contraseña vacía.
  `AUTO_SERVER=TRUE` permite abrirla mientras la app corre.
* **Desde el navegador**: <http://localhost:8080/h2-console>
  JDBC URL `jdbc:h2:file:./data/ventasdb;AUTO_SERVER=TRUE`, usuario `sa`.
* El esquema (`db/schema-NN-*.sql`) y los datos semilla (`db/data-NN-*.sql`) se cargan en cada arranque
  y son idempotentes. Si cambias una tabla, borra la carpeta `./data` y vuelve a arrancar.

Datos de ejemplo: zonas Norte y Sur, un vendedor por zona, un cajero, tres clientes y los
artículos `ART-001` (100.00, 50 pzas), `ART-002` (25.00, 3 pzas) y `ART-003` (10.00, 200 pzas).

## Endpoints

| Área | Rutas |
|------|-------|
| Zonas | `GET/POST /api/zonas`, `GET/PUT/DELETE /api/zonas/{id}`, `POST /api/zonas/unir`, `POST /api/zonas/{id}/dividir` |
| Vendedores / Cajeros | `/api/vendedores`, `/api/cajeros` (GET, GET {id}, POST, PUT {id}, DELETE {id}) |
| Clientes | `/api/clientes` (GET, GET {id}, POST, PUT {id}, DELETE {id}) |
| Venta directa | `POST /api/ventas-directas`, `GET /api/ventas-directas[/{id}]`, `POST /api/ventas-directas/{id}/cancelar` |
| Corte de caja | `POST /api/cortes-caja`, `POST /api/cortes-caja/{id}/cerrar`, `GET /api/cortes-caja/{id}` |
| Pedidos | `POST /api/pedidos`, `GET /api/pedidos/{id}`, `POST /api/pedidos/{id}/entregar`, `POST /api/pedidos/{id}/cancelar` |
| Facturas | `POST /api/facturas/pedidos/{pedidoId}`, `GET /api/facturas/{id}`, `POST /api/facturas/{id}/cancelar`, `POST /api/facturas/{id}/sustituir`, `GET /api/facturas?desde=&hasta=` |
| Cuentas por cobrar | `GET /api/cuentas-por-cobrar/{id}`, `POST /api/cuentas-por-cobrar/{id}/pagos` |

### Reportes (fechas `AAAA-MM-DD`)

| # | Ruta |
|---|------|
| 1–6 | `/api/reportes/vendedores/comisiones`, `/vendedores/{id}/comisiones`, `/vendedores/ventas`, `/vendedores/{id}/clientes`, `/clientes/{id}/vendedores`, `/zonas/vendedores`, `/zonas/clientes` |
| 7 | `/api/reportes/caja/ventas-directas` |
| 8 | `/api/reportes/caja/diferencias` |
| 9–11 | `/api/reportes/pedidos/pendientes`, `/pedidos/pendientes/cliente/{id}`, `/pedidos/pendientes/vendedor/{id}` |
| 12 | `/api/reportes/facturacion/periodo` |
| 13–17 | `/api/reportes/facturacion/clientes`, `/vendedores`, `/productos`, `/ranking-articulos`, `/zonas` |
| 18–20 | `/api/reportes/cxc/cliente/{id}`, `/cxc/periodo`, `/cxc/vencidas` |

Errores: `404` no encontrado, `422` regla de negocio, `409` integridad, `400` petición inválida.

## Entidades (Jakarta + Lombok)

Solo las clases de `model` que son tablas llevan `@Entity` (Zona, Empleado, Cliente, Comision, CorteCaja, Venta,
VentaDetalle, Pago, Ticket, Pedido, DetallePedido, Factura, FacturaDetalle, CuentaPorCobrar, PagoCuentaPorCobrar).
Siguen el modelo del profesor: `@Entity @Data @NoArgsConstructor @AllArgsConstructor @ToString`, `@Id` +
`@GeneratedValue(IDENTITY)`. Los enums, DTO (`records` de entrada/salida) y servicios no llevan `@Entity`.
Spring convierte `zonaId` en la columna `zona_id` y `CorteCaja` en la tabla `corte_caja`, por eso no hay `@Column`.
Las tablas las siguen creando los scripts SQL (`ddl-auto=none`). Los DAO usan `EntityManager` (JPQL) y los reportes,
SQL nativo (`Consultas`). `InventarioLocalGateway` conserva JDBC porque simula a otro subsistema, no es una entidad de Ventas.

## Supuestos y decisiones (para documentar con el equipo)

* **Inventario** pertenece a otro subsistema. Aquí se simula con la tabla `articulo_ref` detrás de la interfaz
  `InventarioGateway` (comunicación síncrona porque se necesita consistencia inmediata). Para integrarlo basta
  otra implementación de la interfaz.
* **Facturación por evento**: la venta directa publica `VentaDirectaRegistrada` / `VentaDirectaCancelada` y
  `FacturaVentaListener` reacciona (el retraso es tolerable). Pedido -> factura sí es una llamada directa.
* **VentaIndirecta** se modela como `Pedido` + `Factura` (se aparta del diagrama de clases; justificar en la entrega).
* Un vendedor pertenece a **una** zona; el cajero no tiene zona. El porcentaje de comisión se fija al contratar.
* Comisión de venta directa: al completar la venta (cajero). Comisión de pedido: al facturar (vendedor).
  Se cancelan junto con la venta o la factura.
* Corte de caja: esperado = importe inicial + pagos en efectivo.
* Sin existencias negativas. Un pedido por encima del inventario solo se acepta con **promesa** y entrega futura.
  La entrega parcial deja cantidades pendientes.
* La factura no se edita: se **cancela** y se **sustituye** por otra ligada a la anterior. Solo se factura un
  pedido `ENTREGADO`. Con abonos registrados no puede cancelarse.
* Cuenta por cobrar solo si `dias_credito > 0`. Crédito: saldo pendiente + total <= límite del cliente y
  días <= días otorgados. El vendedor solo vende a clientes de su zona.
* Dinero en `BigDecimal` con 2 decimales; IVA 16 %.

## Reparto del equipo

| Persona | Módulo |
|---------|--------|
| **Diego** | Proyecto base, zonas, vendedores y cajeros, clientes, comisiones, consultas (DAO) de los reportes 1–6, script de commits |
| **Roberto** | Utilidades comunes, inventario (gateway), venta directa, pago y ticket, corte de caja, reportes 7–11 y 13–17, README |
| **Saul** | Excepciones y filtro de trazabilidad, pedidos, facturación, cuentas por cobrar, servicio y API de los reportes 1–6, reportes 12 y 18–20 |

## Historias de usuario por desarrollador

Ver `docs/historias-por-desarrollador.md` (Taiga V-21 a V-37 y el trabajo que aún no tiene historia).

Los commits siguen TDD: primero la prueba (*red*) y después la implementación (*green*).
`subir-commits.sh` reproduce esa secuencia paso a paso (ver el script: `--ayuda`).
