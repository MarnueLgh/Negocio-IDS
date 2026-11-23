package mx.edu.scvi.ventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.common.Dinero;
import mx.edu.scvi.ventas.common.NoEncontradoException;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.CorteCajaDao;
import mx.edu.scvi.ventas.dao.PagoDao;
import mx.edu.scvi.ventas.dao.TicketDao;
import mx.edu.scvi.ventas.dao.VentaDao;
import mx.edu.scvi.ventas.evento.VentaDirectaCancelada;
import mx.edu.scvi.ventas.evento.VentaDirectaRegistrada;
import mx.edu.scvi.ventas.integracion.ArticuloRef;
import mx.edu.scvi.ventas.integracion.InventarioGateway;
import mx.edu.scvi.ventas.model.CorteCaja;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.EstadoCorte;
import mx.edu.scvi.ventas.model.EstadoVenta;
import mx.edu.scvi.ventas.model.FormaPago;
import mx.edu.scvi.ventas.model.Pago;
import mx.edu.scvi.ventas.model.Ticket;
import mx.edu.scvi.ventas.model.TipoEmpleado;
import mx.edu.scvi.ventas.model.Venta;
import mx.edu.scvi.ventas.model.VentaDetalle;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Venta directa: se cobra y se entrega en el momento. El precio y las existencias vienen de Inventario
 * (síncrono, por InventarioGateway); la comisión del cajero se genera al instante (síncrono);
 * la factura, si se pidió, se emite por evento en proceso.
 */
@Service
@Transactional
public class VentaDirectaService {

    public record Solicitud(Long cajeroId, List<Linea> lineas, FormaPago formaPago, BigDecimal importeRecibido,
                            Long clienteId, boolean solicitaFactura) {

        public record Linea(String codigo, int cantidad) {
        }
    }

    public record ResultadoVenta(Venta venta, List<VentaDetalle> detalles, Pago pago, String ticket) {
    }

    private final VentaDao ventaDao;
    private final PagoDao pagoDao;
    private final TicketDao ticketDao;
    private final CorteCajaDao corteDao;
    private final InventarioGateway inventario;
    private final EmpleadoService empleadoService;
    private final ClienteService clienteService;
    private final ComisionService comisionService;
    private final TicketService ticketService;
    private final ApplicationEventPublisher eventos;

    public VentaDirectaService(VentaDao ventaDao, PagoDao pagoDao, TicketDao ticketDao, CorteCajaDao corteDao,
                               InventarioGateway inventario, EmpleadoService empleadoService,
                               ClienteService clienteService, ComisionService comisionService,
                               TicketService ticketService, ApplicationEventPublisher eventos) {
        this.ventaDao = ventaDao;
        this.pagoDao = pagoDao;
        this.ticketDao = ticketDao;
        this.corteDao = corteDao;
        this.inventario = inventario;
        this.empleadoService = empleadoService;
        this.clienteService = clienteService;
        this.comisionService = comisionService;
        this.ticketService = ticketService;
        this.eventos = eventos;
    }

    public ResultadoVenta registrar(Solicitud s) {
        Reglas.exigir(s.cajeroId() != null, "El cajero es obligatorio");
        Reglas.exigir(s.formaPago() != null, "La forma de pago es obligatoria");
        Reglas.exigir(s.lineas() != null && !s.lineas().isEmpty(), "La venta debe incluir al menos un artículo");

        Empleado cajero = empleadoService.obtener(TipoEmpleado.CAJERO, s.cajeroId());
        Reglas.exigir(cajero.isActivo(), "El cajero está dado de baja");
        CorteCaja corte = corteDao.buscarAbiertoDeCajero(cajero.getEmpleadoId()).orElseThrow(() ->
                new ReglaNegocioException("El cajero no tiene caja abierta: realice primero el inicio de caja"));

        // Datos del cliente solo son necesarios si se solicita factura
        if (s.solicitaFactura()) {
            Reglas.exigir(s.clienteId() != null, "Para facturar se requieren los datos del cliente");
        }
        if (s.clienteId() != null) {
            clienteService.obtener(s.clienteId());
        }

        // Se junta la cantidad por código y se valida contra existencias (no puede haber negativos)
        Map<String, Integer> pedido = new LinkedHashMap<>();
        for (Solicitud.Linea linea : s.lineas()) {
            Reglas.exigir(linea.codigo() != null && !linea.codigo().isBlank(), "El código del artículo es obligatorio");
            Reglas.exigir(linea.cantidad() > 0, "La cantidad de " + linea.codigo() + " debe ser mayor a cero");
            pedido.merge(linea.codigo(), linea.cantidad(), Integer::sum);
        }
        List<VentaDetalle> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entrada : pedido.entrySet()) {
            ArticuloRef articulo = inventario.buscarPorCodigo(entrada.getKey()).orElseThrow(() ->
                    new NoEncontradoException("El artículo " + entrada.getKey() + " no existe"));
            Reglas.exigir(entrada.getValue() <= articulo.existencias(), "Sin existencias de " + articulo.codigo()
                    + ": solicitadas " + entrada.getValue() + ", disponibles " + articulo.existencias());
            BigDecimal importe = Dinero.importe(articulo.precio(), entrada.getValue());
            subtotal = subtotal.add(importe);
            detalles.add(new VentaDetalle(null, null, articulo.articuloId(), articulo.codigo(), articulo.nombre(),
                    entrada.getValue(), articulo.precio(), importe));
        }
        BigDecimal iva = Dinero.iva(subtotal);
        BigDecimal total = subtotal.add(iva);

        // Pago: en efectivo se registra lo recibido y el cambio
        BigDecimal recibido = total;
        if (s.formaPago() == FormaPago.EFECTIVO) {
            Reglas.exigir(s.importeRecibido() != null && s.importeRecibido().compareTo(total) >= 0,
                    "El importe recibido es menor al total de la venta (" + total + ")");
            recibido = s.importeRecibido();
        }
        BigDecimal cambio = Pago.calcularCambio(total, recibido);

        LocalDateTime ahora = LocalDateTime.now();
        Venta venta = ventaDao.insertarVenta(new Venta(null, ahora, cajero.getEmpleadoId(), corte.getCorteCajaId(),
                s.clienteId(), s.solicitaFactura(), subtotal, iva, total, EstadoVenta.COMPLETADA));
        List<VentaDetalle> guardados = new ArrayList<>();
        for (VentaDetalle d : detalles) {
            VentaDetalle conVenta = d.conVenta(venta.getVentaId());
            ventaDao.insertarDetalle(conVenta);
            inventario.descontar(d.getArticuloId(), d.getCantidad());
            guardados.add(conVenta);
        }
        Pago pago = pagoDao.insertarPago(new Pago(null, venta.getVentaId(), s.formaPago(), total, recibido, cambio, ahora));
        Ticket ticket = ticketService.generar(venta, guardados, pago, cajero.getNombre());
        comisionService.generarPorVenta(venta.getVentaId(), cajero.getEmpleadoId(), subtotal);
        if (s.solicitaFactura()) {
            eventos.publishEvent(new VentaDirectaRegistrada(venta.getVentaId(), s.clienteId(), true));
        }
        return new ResultadoVenta(venta, guardados, pago, ticket.getContenido());
    }

    /** Solo se cancelan ventas de una caja que sigue abierta; se regresa el inventario y se cancela la comisión. */
    public Venta cancelar(long ventaId) {
        Venta venta = Reglas.encontrado(ventaDao.getVenta(ventaId), "Venta " + ventaId + " no encontrada");
        Reglas.exigir(venta.getEstado() == EstadoVenta.COMPLETADA, "La venta ya está cancelada");
        CorteCaja corte = Reglas.encontrado(corteDao.buscarPorId(venta.getCorteCajaId()), "Corte de caja no encontrado");
        Reglas.exigir(corte.getEstado() == EstadoCorte.ABIERTA, "No se puede cancelar una venta de un corte ya realizado");
        for (VentaDetalle d : ventaDao.detalles(ventaId)) {
            inventario.reponer(d.getArticuloId(), d.getCantidad());
        }
        ventaDao.actualizarEstado(ventaId, EstadoVenta.CANCELADA);
        comisionService.cancelarPorVenta(ventaId);
        eventos.publishEvent(new VentaDirectaCancelada(ventaId));
        return Reglas.encontrado(ventaDao.getVenta(ventaId), "Venta " + ventaId + " no encontrada");
    }

    public ResultadoVenta obtener(long ventaId) {
        Venta venta = Reglas.encontrado(ventaDao.getVenta(ventaId), "Venta " + ventaId + " no encontrada");
        Pago pago = pagoDao.getPagoPorVenta(ventaId).orElse(null);
        String ticket = ticketDao.porVenta(ventaId).map(Ticket::getContenido).orElse(null);
        return new ResultadoVenta(venta, ventaDao.detalles(ventaId), pago, ticket);
    }

    public List<Venta> listar(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return ventaDao.listarVentas(desde, hasta);
    }
}
