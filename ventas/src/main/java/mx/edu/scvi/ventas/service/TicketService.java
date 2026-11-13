package mx.edu.scvi.ventas.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import mx.edu.scvi.ventas.dao.TicketDao;
import mx.edu.scvi.ventas.model.Pago;
import mx.edu.scvi.ventas.model.Ticket;
import mx.edu.scvi.ventas.model.Venta;
import mx.edu.scvi.ventas.model.VentaDetalle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public static String folio(long ventaId) {
        return String.format("T-%06d", ventaId);
    }

    /** Arma el texto del ticket (función pura, fácil de probar). */
    public static String construir(Venta venta, List<VentaDetalle> detalles, Pago pago, String cajero) {
        StringBuilder sb = new StringBuilder();
        sb.append("TICKET DE VENTA ").append(folio(venta.getVentaId())).append('\n');
        sb.append("Fecha: ").append(venta.getFechaVenta().format(FORMATO)).append('\n');
        sb.append("Cajero: ").append(cajero).append('\n');
        sb.append("--------------------------------\n");
        for (VentaDetalle d : detalles) {
            sb.append(String.format(Locale.US, "%d x %s  $%.2f\n", d.getCantidad(), d.getDescripcion(), d.getImporte()));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format(Locale.US, "Subtotal: $%.2f\n", venta.getSubtotal()));
        sb.append(String.format(Locale.US, "IVA: $%.2f\n", venta.getIva()));
        sb.append(String.format(Locale.US, "Total: $%.2f\n", venta.getTotal()));
        sb.append("Forma de pago: ").append(pago.getFormaPago()).append('\n');
        sb.append(String.format(Locale.US, "Recibido: $%.2f\n", pago.getImporteRecibido()));
        sb.append(String.format(Locale.US, "Cambio: $%.2f\n", pago.getCambio()));
        return sb.toString();
    }

    public Ticket generar(Venta venta, List<VentaDetalle> detalles, Pago pago, String cajero) {
        String contenido = construir(venta, detalles, pago, cajero);
        return ticketDao.insertar(new Ticket(null, venta.getVentaId(), folio(venta.getVentaId()), contenido,
                LocalDateTime.now()));
    }
}
