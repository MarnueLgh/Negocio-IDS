package mx.edu.scvi.ventas.service;

import mx.edu.scvi.ventas.evento.VentaDirectaCancelada;
import mx.edu.scvi.ventas.evento.VentaDirectaRegistrada;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Facturación reacciona a las ventas directas por eventos en proceso (sin acoplarse al código de la venta). */
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
