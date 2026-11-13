package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import mx.edu.scvi.ventas.model.EstadoVenta;
import mx.edu.scvi.ventas.model.FormaPago;
import mx.edu.scvi.ventas.model.Pago;
import mx.edu.scvi.ventas.model.Venta;
import mx.edu.scvi.ventas.model.VentaDetalle;
import org.junit.jupiter.api.Test;

class TicketServiceTest {

    @Test
    void folioTieneSeisDigitos() {
        assertEquals("T-000007", TicketService.folio(7));
    }

    @Test
    void ticketIncluyeTotalesYCambio() {
        Venta venta = new Venta(7L, LocalDateTime.of(2026, 11, 8, 10, 30), 1L, 1L, null, false,
                new BigDecimal("200.00"), new BigDecimal("32.00"), new BigDecimal("232.00"), EstadoVenta.COMPLETADA);
        List<VentaDetalle> detalles = List.of(new VentaDetalle(1L, 7L, 1L, "ART-001", "Taladro", 2,
                new BigDecimal("100.00"), new BigDecimal("200.00")));
        Pago pago = new Pago(1L, 7L, FormaPago.EFECTIVO, new BigDecimal("232.00"), new BigDecimal("300.00"),
                new BigDecimal("68.00"), LocalDateTime.of(2026, 11, 8, 10, 30));

        String ticket = TicketService.construir(venta, detalles, pago, "Cajero Uno");

        assertTrue(ticket.contains("T-000007"));
        assertTrue(ticket.contains("2 x Taladro  $200.00"));
        assertTrue(ticket.contains("Subtotal: $200.00"));
        assertTrue(ticket.contains("IVA: $32.00"));
        assertTrue(ticket.contains("Total: $232.00"));
        assertTrue(ticket.contains("Cambio: $68.00"));
    }

    @Test
    void cambioNuncaEsNegativo() {
        assertEquals(0, new BigDecimal("68.00").compareTo(
                Pago.calcularCambio(new BigDecimal("232.00"), new BigDecimal("300.00"))));
        assertEquals(0, BigDecimal.ZERO.compareTo(
                Pago.calcularCambio(new BigDecimal("232.00"), new BigDecimal("100.00"))));
    }
}
