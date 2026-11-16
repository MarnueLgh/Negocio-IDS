package mx.edu.scvi.ventas.evento;

/** Evento en proceso: se canceló una venta directa; si tenía factura, también se cancela. */
public record VentaDirectaCancelada(long ventaId) {
}
