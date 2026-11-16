# Historias de usuario del subsistema de Ventas por desarrollador

Estado: ✅ hecha · ⚠️ parcial · ❌ pendiente. Las horas son las estimadas en Taiga.

## Historias que ya están en Taiga (V-21 a V-37)

| Desarrollador | Historia | Nombre | Horas | Estado | Nota |
|---|---|---|---|---|---|
| Diego | V-21 | Registrar cliente | 15 | ✅ | RFC no duplicado, zona obligatoria, límite de crédito |
| Diego | V-22 | Modificar cliente | 10 | ⚠️ | Se modifica por id; falta una búsqueda por nombre o RFC |
| Diego | V-23 | Dar de baja cliente | 12 | ✅ | Baja lógica (activo = false) |
| Diego | V-24 | Consultar cliente | 8 | ✅ | |
| Diego | V-25 | Listar clientes | 12 | ✅ | Ordenado por nombre; filtro opcional por zona |
| Diego | V-28 | Registrar zona | 8 | ✅ | Nombre obligatorio y sin duplicar; nace activa |
| Diego | V-29 | Modificar zona | 6 | ✅ | Además unir y dividir zonas |
| Diego | V-30 | Dar de baja zona | 10 | ⚠️ | Desactivar (PUT activo=false) es lógico y bloquea si hay clientes o vendedores; DELETE borra físicamente una zona vacía |
| Diego | V-31 | Consultar detalle de zona | 4 | ✅ | |
| Diego | V-32 | Listar zonas | 6 | ✅ | En Taiga está duplicada (refs 85 y 86): borrar una |
| Saul | V-26 | Registrar factura | 20 | ✅ | De pedido entregado o de venta directa; genera cuenta por cobrar si hay días de crédito |
| Saul | V-27 | Cancelar factura | 15 | ✅ | Cancela la cuenta por cobrar y la comisión; no se cancela con abonos |
| Saul | V-33 | Registrar pedido | 20 | ✅ | Valida crédito, zona y promesa de entrega; estado inicial Pendiente |
| Saul | V-34 | Modificar pedido | 15 | ❌ | No existe todavía |
| Saul | V-35 | Cancelar pedido | 10 | ✅ | Solo pendientes sin entregas |
| Saul | V-36 | Consultar detalle de pedido | 10 | ✅ | |
| Saul | V-37 | Listar pedidos generales | 10 | ❌ | Solo existen los reportes de pendientes (9 a 11) |
| Roberto | — | Sin historias en Taiga | — | — | Su módulo no tiene historias creadas; ver la tabla siguiente |

Horas en Taiga: Diego 91 h (10 historias), Saul 100 h (7 historias), Roberto 0 h.

## Trabajo hecho que sale de los requisitos y no tiene historia en Taiga

Los ids V-38 en adelante son una propuesta para darlos de alta.

| Desarrollador | Id propuesto | Trabajo | Estado |
|---|---|---|---|
| Diego | V-38 | CRUD de vendedores (una sola zona) y cajeros (sin zona) | ✅ |
| Diego | V-39 | Comisiones por venta y por factura, y su cancelación | ✅ |
| Diego | V-40 | Reportes 1 a 6 (comisiones, ventas por vendedor, clientes y vendedores, por zona) | ✅ |
| Roberto | V-41 | Venta directa con validación de existencias y cancelación | ✅ |
| Roberto | V-42 | Formas de pago (efectivo, tarjeta, transferencia) y cambio | ✅ |
| Roberto | V-43 | Ticket de venta | ✅ |
| Roberto | V-44 | Inicio y corte de caja con diferencias | ✅ |
| Roberto | V-45 | Integración síncrona con Inventario (InventarioGateway) | ✅ |
| Roberto | V-46 | Reportes 7 y 8 (caja) y 9 a 11, 13 a 17 (pedidos pendientes y facturación) | ✅ |
| Saul | V-47 | Entrega de pedido, parcial si falta stock | ✅ |
| Saul | V-48 | Cuentas por cobrar y registro de pagos con ticket | ✅ |
| Saul | V-49 | Facturación automática de la venta directa por evento | ✅ |
| Saul | V-50 | Reportes 12 y 18 a 20 (facturas y cuentas por cobrar) | ✅ |
