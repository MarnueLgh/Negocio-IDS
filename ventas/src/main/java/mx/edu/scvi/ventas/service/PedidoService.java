package mx.edu.scvi.ventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import mx.edu.scvi.ventas.common.Dinero;
import mx.edu.scvi.ventas.common.NoEncontradoException;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.CuentaPorCobrarDao;
import mx.edu.scvi.ventas.dao.PedidoDao;
import mx.edu.scvi.ventas.integracion.ArticuloRef;
import mx.edu.scvi.ventas.integracion.InventarioGateway;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.DetallePedido;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.EstadoPedido;
import mx.edu.scvi.ventas.model.Pedido;
import mx.edu.scvi.ventas.model.TipoEmpleado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Venta no directa. El pedido no descuenta inventario: se descuenta hasta la entrega (parcial si falta stock;
 * lo no surtido queda pendiente). Un pedido no puede exceder el inventario salvo que se prometa con anticipación.
 */
@Service
@Transactional
public class PedidoService {

    public record Solicitud(Long clienteId, Long vendedorId, Integer diasCredito, LocalDate fechaEntrega,
                            List<Linea> lineas) {

        public record Linea(String codigo, int cantidad, boolean promesa) {
        }
    }

    public record Resultado(Pedido pedido, List<DetallePedido> detalles) {
    }

    private final PedidoDao pedidoDao;
    private final CuentaPorCobrarDao cuentaDao;
    private final InventarioGateway inventario;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;

    public PedidoService(PedidoDao pedidoDao, CuentaPorCobrarDao cuentaDao, InventarioGateway inventario,
                         ClienteService clienteService, EmpleadoService empleadoService) {
        this.pedidoDao = pedidoDao;
        this.cuentaDao = cuentaDao;
        this.inventario = inventario;
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
    }

    public Resultado crear(Solicitud s) {
        Reglas.exigir(s.clienteId() != null && s.vendedorId() != null, "El cliente y el vendedor son obligatorios");
        Reglas.exigir(s.lineas() != null && !s.lineas().isEmpty(), "El pedido debe incluir al menos un artículo");
        Reglas.exigir(s.fechaEntrega() != null, "La fecha de entrega es obligatoria: se define al hacer el pedido");
        LocalDate hoy = LocalDate.now();
        Reglas.exigir(!s.fechaEntrega().isBefore(hoy), "La fecha de entrega no puede ser anterior a hoy");

        Cliente cliente = clienteService.obtener(s.clienteId());
        Reglas.exigir(cliente.isActivo(), "El cliente está dado de baja");
        Empleado vendedor = empleadoService.obtener(TipoEmpleado.VENDEDOR, s.vendedorId());
        Reglas.exigir(vendedor.isActivo(), "El vendedor está dado de baja");
        Reglas.exigir(cliente.getZonaId().equals(vendedor.getZonaId()),
                "Solo se puede vender a clientes de la misma zona del vendedor");

        int dias = s.diasCredito() == null ? cliente.getDiasCredito() : s.diasCredito();
        Reglas.exigir(dias >= 0 && dias <= cliente.getDiasCredito(),
                "Los días de crédito deben estar entre 0 y " + cliente.getDiasCredito() + " (los otorgados al cliente)");

        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Solicitud.Linea linea : s.lineas()) {
            Reglas.exigir(linea.codigo() != null && !linea.codigo().isBlank(), "El código del artículo es obligatorio");
            Reglas.exigir(linea.cantidad() > 0, "La cantidad de " + linea.codigo() + " debe ser mayor a cero");
            ArticuloRef articulo = inventario.buscarPorCodigo(linea.codigo()).orElseThrow(() ->
                    new NoEncontradoException("El artículo " + linea.codigo() + " no existe"));
            if (linea.cantidad() > articulo.existencias()) {
                Reglas.exigir(linea.promesa() && s.fechaEntrega().isAfter(hoy),
                        "La cantidad pedida de " + articulo.codigo() + " excede el inventario ("
                                + articulo.existencias() + " disponibles): solo se permite con una promesa de entrega"
                                + " con al menos un día de anticipación");
            }
            BigDecimal importe = Dinero.importe(articulo.precio(), linea.cantidad());
            subtotal = subtotal.add(importe);
            detalles.add(new DetallePedido(null, null, articulo.articuloId(), articulo.codigo(), articulo.nombre(),
                    linea.cantidad(), linea.cantidad(), articulo.precio(), importe, linea.promesa()));
        }
        BigDecimal iva = Dinero.iva(subtotal);
        BigDecimal total = subtotal.add(iva);

        if (dias > 0) {
            BigDecimal saldo = cuentaDao.saldoPendienteCliente(cliente.getClienteId());
            Reglas.exigir(saldo.add(total).compareTo(cliente.getLimiteCredito()) <= 0,
                    "Excede el límite de crédito del cliente: límite " + cliente.getLimiteCredito()
                            + ", saldo pendiente " + saldo + ", pedido " + total);
        }

        Pedido pedido = pedidoDao.insertarPedido(new Pedido(null, hoy, s.fechaEntrega(), cliente.getClienteId(),
                vendedor.getEmpleadoId(), dias, subtotal, iva, total, EstadoPedido.PENDIENTE));
        for (DetallePedido d : detalles) {
            pedidoDao.insertarDetalle(d.conPedido(pedido.getPedidoId()));
        }
        return obtener(pedido.getPedidoId());
    }

    /** Entrega lo que haya en existencias; lo que falte queda pendiente. El pedido pasa a ENTREGADO al completarse. */
    public Resultado entregar(long pedidoId) {
        Pedido pedido = obtenerPedido(pedidoId);
        Reglas.exigir(pedido.getEstado() == EstadoPedido.PENDIENTE, "Solo se entregan pedidos pendientes");
        int surtidoTotal = 0;
        for (DetallePedido d : pedidoDao.detalles(pedidoId)) {
            if (d.getCantidadPendiente() == 0) {
                continue;
            }
            int disponibles = inventario.buscarPorId(d.getArticuloId()).map(ArticuloRef::existencias).orElse(0);
            int surtir = Math.min(d.getCantidadPendiente(), disponibles);
            if (surtir > 0) {
                inventario.descontar(d.getArticuloId(), surtir);
                pedidoDao.actualizarPendiente(d.getDetallePedidoId(), d.getCantidadPendiente() - surtir);
                surtidoTotal += surtir;
            }
        }
        Reglas.exigir(surtidoTotal > 0, "No hay existencias para surtir ningún artículo pendiente del pedido");
        boolean completo = pedidoDao.detalles(pedidoId).stream().allMatch(d -> d.getCantidadPendiente() == 0);
        if (completo) {
            pedidoDao.actualizarEstado(pedidoId, EstadoPedido.ENTREGADO);
        }
        return obtener(pedidoId);
    }

    /** Solo se cancela un pedido pendiente al que no se le ha entregado nada. */
    public Pedido cancelar(long pedidoId) {
        Pedido pedido = obtenerPedido(pedidoId);
        Reglas.exigir(pedido.getEstado() == EstadoPedido.PENDIENTE, "Solo se cancelan pedidos pendientes");
        boolean sinEntregas = pedidoDao.detalles(pedidoId).stream()
                .allMatch(d -> d.getCantidadPendiente() == d.getCantidadPedida());
        Reglas.exigir(sinEntregas, "El pedido ya tiene entregas parciales; no puede cancelarse");
        pedidoDao.actualizarEstado(pedidoId, EstadoPedido.CANCELADO);
        return obtenerPedido(pedidoId);
    }

    public Resultado obtener(long pedidoId) {
        return new Resultado(obtenerPedido(pedidoId), pedidoDao.detalles(pedidoId));
    }

    private Pedido obtenerPedido(long pedidoId) {
        return Reglas.encontrado(pedidoDao.getPedido(pedidoId), "Pedido " + pedidoId + " no encontrado");
    }
}
