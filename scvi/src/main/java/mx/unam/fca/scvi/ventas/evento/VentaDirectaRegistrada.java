package mx.unam.fca.scvi.ventas.evento;

/**
 * Evento en proceso: la venta ya se completó; si el cliente pidió factura, Facturación la emite.
 *
 * Subsistema: Ventas.
 *
 * @author Álvarez Aguilar Saúl Antonio Equipo 2
 * @author Pérez Flores Roberto Carlos Equipo 2
 * @author Pérez Muñoz Diego Equipo 2
 *
 * Fecha creación: 15-11-2026
 *
 * Fecha modificación: 23-11-2026
 */
public record VentaDirectaRegistrada(long ventaId, Long clienteId, boolean solicitaFactura) {
}
