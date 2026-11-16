package mx.edu.scvi.ventas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.CuentaPorCobrarDao;
import mx.edu.scvi.ventas.dao.FacturaDao;
import mx.edu.scvi.ventas.dao.PedidoDao;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.CuentaPorCobrar;
import mx.edu.scvi.ventas.model.DetallePedido;
import mx.edu.scvi.ventas.model.EstadoCuenta;
import mx.edu.scvi.ventas.model.EstadoFactura;
import mx.edu.scvi.ventas.model.EstadoPedido;
import mx.edu.scvi.ventas.model.EstadoVenta;
import mx.edu.scvi.ventas.model.Factura;
import mx.edu.scvi.ventas.model.FacturaDetalle;
import mx.edu.scvi.ventas.model.Pedido;
import mx.edu.scvi.ventas.model.Venta;
import mx.edu.scvi.ventas.model.VentaDetalle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facturación. Una factura no se modifica: se cancela y se crea otra. Las de pedido generan cuenta por cobrar si
 * hay días de crédito y comisión al vendedor; las de venta directa se pagaron en el momento (sin cuenta por cobrar,
 * la comisión del cajero ya se generó con la venta). Facturar no mueve el inventario.
 */
@Service
@Transactional
public class FacturacionService {

    public record ResultadoFactura(Factura factura, List<FacturaDetalle> detalles, CuentaPorCobrar cuenta) {
    }

    private final FacturaDao facturaDao;
    private final PedidoDao pedidoDao;
    private final CuentaPorCobrarDao cuentaDao;
    private final ClienteService clienteService;
    private final ComisionService comisionService;
    private final VentaDirectaService ventaService;

    public FacturacionService(FacturaDao facturaDao, PedidoDao pedidoDao, CuentaPorCobrarDao cuentaDao,
                              ClienteService clienteService, ComisionService comisionService,
                              VentaDirectaService ventaService) {
        this.facturaDao = facturaDao;
        this.pedidoDao = pedidoDao;
        this.cuentaDao = cuentaDao;
        this.clienteService = clienteService;
        this.comisionService = comisionService;
        this.ventaService = ventaService;
    }

    /** Factura un pedido entregado por completo. Los días de crédito vienen del pedido y pueden modificarse. */
    public Factura facturarPedido(long pedidoId, Integer diasCredito) {
        return emitirDePedido(pedidoId, diasCredito, null);
    }

    /** Factura una venta directa (los datos del cliente son obligatorios). */
    public Factura facturarVenta(long ventaId) {
        return emitirDeVenta(ventaId, null);
    }

    /** Cancela la factura. Si ya tiene abonos no puede cancelarse. El pedido vuelve a ENTREGADO. */
    public Factura cancelar(long facturaId) {
        Factura factura = obtenerFactura(facturaId);
        Reglas.exigir(factura.getEstado() == EstadoFactura.EMITIDA, "La factura ya está cancelada");
        Optional<CuentaPorCobrar> cuenta = cuentaDao.buscarPorFactura(facturaId);
        if (cuenta.isPresent()) {
            Reglas.exigir(cuenta.get().getImportePendiente().compareTo(cuenta.get().getImporteOriginal()) == 0,
                    "La factura ya tiene abonos registrados; no puede cancelarse");
            cuentaDao.cancelarPorFactura(facturaId);
        }
        facturaDao.actualizarEstado(facturaId, EstadoFactura.CANCELADA);
        comisionService.cancelarPorFactura(facturaId);
        if (factura.getPedidoId() != null) {
            pedidoDao.actualizarEstado(factura.getPedidoId(), EstadoPedido.ENTREGADO);
        }
        return obtenerFactura(facturaId);
    }

    /** "No se puede modificar una factura": se cancela y se crea una nueva ligada a la anterior. */
    public Factura sustituir(long facturaId, Integer diasCredito) {
        Factura anterior = obtenerFactura(facturaId);
        Reglas.exigir(anterior.getEstado() == EstadoFactura.EMITIDA, "Solo se sustituye una factura emitida");
        cancelar(facturaId);
        if (anterior.getPedidoId() != null) {
            return emitirDePedido(anterior.getPedidoId(), diasCredito, facturaId);
        }
        return emitirDeVenta(anterior.getVentaId(), facturaId);
    }

    /** Se invoca cuando se cancela una venta directa: su factura emitida, si existe, también se cancela. */
    public void cancelarPorVenta(long ventaId) {
        facturaDao.buscarEmitidaPorVenta(ventaId).ifPresent(f -> cancelar(f.getFacturaId()));
    }

    public ResultadoFactura obtener(long facturaId) {
        Factura factura = obtenerFactura(facturaId);
        return new ResultadoFactura(factura, facturaDao.detalles(facturaId),
                cuentaDao.buscarPorFactura(facturaId).orElse(null));
    }

    public Optional<Factura> facturaDeVenta(long ventaId) {
        return facturaDao.buscarEmitidaPorVenta(ventaId);
    }

    /** Reporte 12: facturas durante un periodo. */
    public List<Factura> listar(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return facturaDao.facturacionPorPeriodo(desde, hasta);
    }

    private Factura emitirDePedido(long pedidoId, Integer diasOverride, Long sustituyeId) {
        Pedido pedido = Reglas.encontrado(pedidoDao.getPedido(pedidoId), "Pedido " + pedidoId + " no encontrado");
        Reglas.exigir(pedido.getEstado() == EstadoPedido.ENTREGADO,
                "Solo se factura un pedido entregado por completo (estado actual: " + pedido.getEstado() + ")");
        Cliente cliente = clienteService.obtener(pedido.getClienteId());
        validarDatosFiscales(cliente);
        int dias = diasOverride == null ? pedido.getDiasCredito() : diasOverride;
        Reglas.exigir(dias >= 0 && dias <= cliente.getDiasCredito(),
                "Los días de crédito deben estar entre 0 y " + cliente.getDiasCredito());

        LocalDate hoy = LocalDate.now();
        Factura factura = facturaDao.emitirFactura(new Factura(null, hoy, dias, hoy.plusDays(dias),
                pedido.getSubtotal(), pedido.getIva(), pedido.getTotal(), EstadoFactura.EMITIDA, null, pedidoId,
                cliente.getClienteId(), pedido.getVendedorId(), sustituyeId));
        for (DetallePedido d : pedidoDao.detalles(pedidoId)) {
            facturaDao.insertarDetalle(new FacturaDetalle(null, factura.getFacturaId(), d.getArticuloId(), d.getCodigo(),
                    d.getDescripcion(), d.getCantidadPedida(), d.getPrecio(), d.getImporte()));
        }
        pedidoDao.actualizarEstado(pedidoId, EstadoPedido.FACTURADO);
        if (dias > 0) {
            cuentaDao.insertarCuenta(new CuentaPorCobrar(null, hoy, hoy, factura.getFechaLimitePago(),
                    EstadoCuenta.PENDIENTE, factura.getTotal(), factura.getTotal(), factura.getFacturaId(),
                    cliente.getClienteId()));
        }
        comisionService.generarPorFactura(factura.getFacturaId(), pedido.getVendedorId(), pedido.getSubtotal());
        return factura;
    }

    private Factura emitirDeVenta(long ventaId, Long sustituyeId) {
        VentaDirectaService.ResultadoVenta resultado = ventaService.obtener(ventaId);
        Venta venta = resultado.venta();
        Reglas.exigir(venta.getEstado() == EstadoVenta.COMPLETADA, "No se factura una venta cancelada");
        Reglas.exigir(facturaDao.buscarEmitidaPorVenta(ventaId).isEmpty(), "La venta ya tiene una factura emitida");
        Reglas.exigir(venta.getClienteId() != null, "Para facturar se requieren los datos del cliente");
        Cliente cliente = clienteService.obtener(venta.getClienteId());
        validarDatosFiscales(cliente);

        LocalDate hoy = LocalDate.now();
        Factura factura = facturaDao.emitirFactura(new Factura(null, hoy, 0, hoy, venta.getSubtotal(), venta.getIva(),
                venta.getTotal(), EstadoFactura.EMITIDA, ventaId, null, cliente.getClienteId(), venta.getCajeroId(),
                sustituyeId));
        for (VentaDetalle d : resultado.detalles()) {
            facturaDao.insertarDetalle(new FacturaDetalle(null, factura.getFacturaId(), d.getArticuloId(), d.getCodigo(),
                    d.getDescripcion(), d.getCantidad(), d.getPrecio(), d.getImporte()));
        }
        return factura;
    }

    private Factura obtenerFactura(long facturaId) {
        return Reglas.encontrado(facturaDao.buscarPorId(facturaId), "Factura " + facturaId + " no encontrada");
    }

    private void validarDatosFiscales(Cliente cliente) {
        Reglas.exigir(!vacio(cliente.getNombre()) && !vacio(cliente.getRfc()) && !vacio(cliente.getDomicilio()),
                "El cliente no tiene completos sus datos fiscales (nombre, RFC y domicilio)");
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
