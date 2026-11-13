package mx.edu.scvi.ventas.evento;

/** Evento en proceso: la venta ya se completó; si el cliente pidió factura, Facturación la emite. */
public record VentaDirectaRegistrada(long ventaId, Long clienteId, boolean solicitaFactura) {
}
