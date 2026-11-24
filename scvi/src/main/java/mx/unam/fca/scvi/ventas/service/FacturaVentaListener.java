package mx.unam.fca.scvi.ventas.service;

import mx.unam.fca.scvi.ventas.evento.VentaDirectaCancelada;
import mx.unam.fca.scvi.ventas.evento.VentaDirectaRegistrada;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Facturación reacciona a las ventas directas por eventos en proceso (sin acoplarse al código de la venta).
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
@Component
public class FacturaVentaListener {

    private final FacturacionService facturacionService;

    public FacturaVentaListener(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @EventListener
    public void alRegistrarVenta(VentaDirectaRegistrada evento) {
        if (evento.solicitaFactura()) {
            facturacionService.facturarVenta(evento.ventaId());
        }
    }

    @EventListener
    public void alCancelarVenta(VentaDirectaCancelada evento) {
        facturacionService.cancelarPorVenta(evento.ventaId());
    }
}
