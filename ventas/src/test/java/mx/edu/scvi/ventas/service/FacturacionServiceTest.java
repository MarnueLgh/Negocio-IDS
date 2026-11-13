package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.PruebaDatos;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.EstadoFactura;
import mx.edu.scvi.ventas.model.EstadoPedido;
import mx.edu.scvi.ventas.model.Factura;
import mx.edu.scvi.ventas.model.FormaPago;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@PruebaIntegracion
class FacturacionServiceTest {

    @Autowired
    private FacturacionService facturacion;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private CuentaPorCobrarService cuentaService;
    @Autowired
    private VentaDirectaService ventaService;
    @Autowired
    private CorteCajaService corteService;
    @Autowired
    private PruebaDatos datos;
    @Autowired
    private JdbcTemplate jdbc;

    private Zona zona;
    private Empleado vendedor;
    private Cliente cliente;

    @BeforeEach
    void preparar() {
        zona = datos.zona();
        vendedor = datos.vendedor(zona.getZonaId(), "3.00");
        cliente = datos.cliente(zona.getZonaId(), 30, "10000.00");
    }

    private long pedidoEntregado(Cliente c, Integer dias) {
        PedidoService.Resultado creado = pedidoService.crear(new PedidoService.Solicitud(c.getClienteId(),
                vendedor.getEmpleadoId(), dias, LocalDate.now().plusDays(1),
                List.of(new PedidoService.Solicitud.Linea("ART-001", 2, false))));
        pedidoService.entregar(creado.pedido().getPedidoId());
        return creado.pedido().getPedidoId();
    }

    private int contar(String sql, Object arg) {
        Integer total = jdbc.queryForObject(sql, Integer.class, arg);
        return total == null ? 0 : total;
    }

    private void assertDinero(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real), "esperado " + esperado + " pero fue " + real);
    }

    @Test
    void facturarPedidoConCreditoCreaCuentaPorCobrarYComisionDelVendedor() {
        long pedidoId = pedidoEntregado(cliente, null);

        Factura factura = facturacion.facturarPedido(pedidoId, null);

        assertEquals(EstadoFactura.EMITIDA, factura.getEstado());
        assertEquals(LocalDate.now().plusDays(30), factura.getFechaLimitePago());
        FacturacionService.ResultadoFactura detalle = facturacion.obtener(factura.getFacturaId());
        assertNotNull(detalle.cuenta());
        assertDinero("232.00", detalle.cuenta().getImportePendiente());
        BigDecimal comision = jdbc.queryForObject("SELECT importe_comision FROM comision WHERE factura_id = ?",
                BigDecimal.class, factura.getFacturaId());
        assertDinero("6.00", comision);
        assertEquals("FACTURADO", jdbc.queryForObject("SELECT estado FROM pedido WHERE pedido_id = ?",
                String.class, pedidoId));
    }

    @Test
    void sinDiasDeCreditoNoSeGeneraCuentaPorCobrar() {
        Cliente contado = datos.cliente(zona.getZonaId(), 0, "0.00");
        long pedidoId = pedidoEntregado(contado, null);

        Factura factura = facturacion.facturarPedido(pedidoId, null);

        assertEquals(0, contar("SELECT COUNT(*) FROM cuenta_por_cobrar WHERE factura_id = ?", factura.getFacturaId()));
    }

    @Test
    void losDiasDeCreditoSePuedenReducirPeroNoExcederLosDelCliente() {
        long pedidoId = pedidoEntregado(cliente, null);
        assertThrows(ReglaNegocioException.class, () -> facturacion.facturarPedido(pedidoId, 45));
        Factura factura = facturacion.facturarPedido(pedidoId, 10);
        assertEquals(LocalDate.now().plusDays(10), factura.getFechaLimitePago());
    }

    @Test
    void noSeFacturaUnPedidoPendienteNiDosVeces() {
        PedidoService.Resultado pendiente = pedidoService.crear(new PedidoService.Solicitud(cliente.getClienteId(),
                vendedor.getEmpleadoId(), 0, LocalDate.now().plusDays(1),
                List.of(new PedidoService.Solicitud.Linea("ART-001", 1, false))));
        assertThrows(ReglaNegocioException.class,
                () -> facturacion.facturarPedido(pendiente.pedido().getPedidoId(), null));

        long entregado = pedidoEntregado(cliente, null);
        facturacion.facturarPedido(entregado, null);
        assertThrows(ReglaNegocioException.class, () -> facturacion.facturarPedido(entregado, null));
    }

    @Test
    void cancelarDevuelveElPedidoACanceladoDeFacturaYCancelaCuentaYComision() {
        long pedidoId = pedidoEntregado(cliente, null);
        Factura factura = facturacion.facturarPedido(pedidoId, null);

        Factura cancelada = facturacion.cancelar(factura.getFacturaId());

        assertEquals(EstadoFactura.CANCELADA, cancelada.getEstado());
        assertEquals(EstadoPedido.ENTREGADO.name(), jdbc.queryForObject(
                "SELECT estado FROM pedido WHERE pedido_id = ?", String.class, pedidoId));
        assertEquals("CANCELADA", jdbc.queryForObject("SELECT estado FROM cuenta_por_cobrar WHERE factura_id = ?",
                String.class, factura.getFacturaId()));
        assertEquals("CANCELADA", jdbc.queryForObject("SELECT estado FROM comision WHERE factura_id = ?",
                String.class, factura.getFacturaId()));
    }

    @Test
    void sustituirCancelaLaFacturaYCreaOtraLigada() {
        long pedidoId = pedidoEntregado(cliente, null);
        Factura original = facturacion.facturarPedido(pedidoId, null);

        Factura nueva = facturacion.sustituir(original.getFacturaId(), 15);

        assertEquals(original.getFacturaId(), nueva.getSustituyeFacturaId());
        assertEquals(EstadoFactura.EMITIDA, nueva.getEstado());
        assertEquals(LocalDate.now().plusDays(15), nueva.getFechaLimitePago());
        assertEquals(EstadoFactura.CANCELADA, facturacion.obtener(original.getFacturaId()).factura().getEstado());
    }

    @Test
    void noSePuedeCancelarUnaFacturaQueYaTieneAbonos() {
        long pedidoId = pedidoEntregado(cliente, null);
        Factura factura = facturacion.facturarPedido(pedidoId, null);
        long cuentaId = facturacion.obtener(factura.getFacturaId()).cuenta().getCuentaId();
        cuentaService.registrarPago(cuentaId, new BigDecimal("50.00"), FormaPago.TRANSFERENCIA);

        assertThrows(ReglaNegocioException.class, () -> facturacion.cancelar(factura.getFacturaId()));
    }

    @Test
    void ventaDirectaConFacturaSolicitadaEmiteFacturaPorEvento() {
        Empleado cajero = datos.cajero();
        corteService.abrir(cajero.getEmpleadoId(), 61, new BigDecimal("100.00"));

        VentaDirectaService.ResultadoVenta r = ventaService.registrar(new VentaDirectaService.Solicitud(
                cajero.getEmpleadoId(), List.of(new VentaDirectaService.Solicitud.Linea("ART-001", 1)),
                FormaPago.TARJETA, null, cliente.getClienteId(), true));

        Factura factura = facturacion.facturaDeVenta(r.venta().getVentaId()).orElseThrow();
        assertDinero("116.00", factura.getTotal());
        assertEquals(0, contar("SELECT COUNT(*) FROM cuenta_por_cobrar WHERE factura_id = ?", factura.getFacturaId()));
    }

    @Test
    void ventaDirectaSinFacturaNoGeneraFacturaYCancelarlaCancelaLaFactura() {
        Empleado cajero = datos.cajero();
        corteService.abrir(cajero.getEmpleadoId(), 62, new BigDecimal("100.00"));
        VentaDirectaService.ResultadoVenta sinFactura = ventaService.registrar(new VentaDirectaService.Solicitud(
                cajero.getEmpleadoId(), List.of(new VentaDirectaService.Solicitud.Linea("ART-001", 1)),
                FormaPago.TARJETA, null, null, false));
        assertTrue(facturacion.facturaDeVenta(sinFactura.venta().getVentaId()).isEmpty());

        VentaDirectaService.ResultadoVenta conFactura = ventaService.registrar(new VentaDirectaService.Solicitud(
                cajero.getEmpleadoId(), List.of(new VentaDirectaService.Solicitud.Linea("ART-001", 1)),
                FormaPago.TARJETA, null, cliente.getClienteId(), true));
        Factura factura = facturacion.facturaDeVenta(conFactura.venta().getVentaId()).orElseThrow();

        ventaService.cancelar(conFactura.venta().getVentaId());

        assertFalse(facturacion.facturaDeVenta(conFactura.venta().getVentaId()).isPresent());
        assertEquals(EstadoFactura.CANCELADA, facturacion.obtener(factura.getFacturaId()).factura().getEstado());
    }
}
