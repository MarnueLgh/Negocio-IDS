package mx.unam.fca.scvi.ventas.evento;

/**
 * Evento en proceso: se canceló una venta directa; si tenía factura, también se cancela.
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
public record VentaDirectaCancelada(long ventaId) {
}
