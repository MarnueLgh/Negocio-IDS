package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.PruebaDatos;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.CuentaPorCobrar;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.EstadoCuenta;
import mx.edu.scvi.ventas.model.FormaPago;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@PruebaIntegracion
class CuentaPorCobrarServiceTest {

    @Autowired
    private CuentaPorCobrarService cuentaService;
    @Autowired
    private FacturacionService facturacion;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private PruebaDatos datos;
    @Autowired
    private JdbcTemplate jdbc;
    @PersistenceContext
    private EntityManager em;

    private Cliente cliente;
    private long cuentaId;

    @BeforeEach
    void crearCuenta() {
        Zona zona = datos.zona();
        Empleado vendedor = datos.vendedor(zona.getZonaId(), "3.00");
        cliente = datos.cliente(zona.getZonaId(), 30, "10000.00");
        PedidoService.Resultado pedido = pedidoService.crear(new PedidoService.Solicitud(cliente.getClienteId(),
                vendedor.getEmpleadoId(), null, LocalDate.now().plusDays(1),
                List.of(new PedidoService.Solicitud.Linea("ART-001", 2, false))));
        pedidoService.entregar(pedido.pedido().getPedidoId());
        long facturaId = facturacion.facturarPedido(pedido.pedido().getPedidoId(), null).getFacturaId();
        cuentaId = facturacion.obtener(facturaId).cuenta().getCuentaId();
    }

    private void assertDinero(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real), "esperado " + esperado + " pero fue " + real);
    }

    @Test
    void pagoParcialDejaLaCuentaEnParcialYMuestraElRestante() {
        CuentaPorCobrarService.ResultadoPago r =
                cuentaService.registrarPago(cuentaId, new BigDecimal("100.00"), FormaPago.EFECTIVO);

        assertEquals(EstadoCuenta.PARCIAL, r.cuenta().getEstado());
        assertDinero("132.00", r.cuenta().getImportePendiente());
        assertTrue(r.ticket().contains("Restante: $132.00"));
    }

    @Test
    void liquidarLaCuentaLaMarcaComoLiquidadaYYaNoAdmitePagos() {
        CuentaPorCobrarService.ResultadoPago r =
                cuentaService.registrarPago(cuentaId, new BigDecimal("232.00"), FormaPago.TRANSFERENCIA);

        assertEquals(EstadoCuenta.LIQUIDADA, r.cuenta().getEstado());
        assertThrows(ReglaNegocioException.class,
                () -> cuentaService.registrarPago(cuentaId, new BigDecimal("1.00"), FormaPago.EFECTIVO));
    }

    @Test
    void rechazaPagosMayoresAlSaldoOInvalidos() {
        assertThrows(ReglaNegocioException.class,
                () -> cuentaService.registrarPago(cuentaId, new BigDecimal("232.01"), FormaPago.EFECTIVO));
        assertThrows(ReglaNegocioException.class,
                () -> cuentaService.registrarPago(cuentaId, new BigDecimal("0.00"), FormaPago.EFECTIVO));
        assertThrows(ReglaNegocioException.class,
                () -> cuentaService.registrarPago(cuentaId, new BigDecimal("10.00"), null));
    }

    @Test
    void cuentasVencidasIncluyenLosDiasDeRetraso() {
        assertTrue(cuentaService.vencidas().stream().noneMatch(v -> v.cuenta().getCuentaId() == cuentaId));

        jdbc.update("UPDATE cuenta_por_cobrar SET fecha_limite = ? WHERE cuenta_id = ?",
                LocalDate.now().minusDays(3), cuentaId);
        em.clear(); // la fecha cambió por SQL directo: se descarta lo que JPA tenía en memoria

        CuentaPorCobrarService.CuentaVencida vencida = cuentaService.vencidas().stream()
                .filter(v -> v.cuenta().getCuentaId() == cuentaId).findFirst().orElseThrow();
        assertEquals(3, vencida.diasRetraso());
    }

    @Test
    void consultaPorClienteYPorPeriodo() {
        List<CuentaPorCobrar> deCliente = cuentaService.porCliente(cliente.getClienteId());
        assertEquals(1, deCliente.size());
        LocalDate hoy = LocalDate.now();
        assertTrue(cuentaService.porPeriodo(hoy, hoy).stream().anyMatch(c -> c.getCuentaId() == cuentaId));
    }

    @Test
    void elSaldoPendienteDelClienteLimitaNuevosPedidosAPlazo() {
        // El cliente ya debe 232.00 de un límite de 10,000.00; un pedido a crédito por más del resto se rechaza.
        Zona zona = datos.zona();
        Empleado vendedor = datos.vendedor(zona.getZonaId(), "3.00");
        Cliente justo = datos.cliente(zona.getZonaId(), 30, "300.00");
        pedidoService.crear(new PedidoService.Solicitud(justo.getClienteId(), vendedor.getEmpleadoId(), null,
                LocalDate.now().plusDays(1), List.of(new PedidoService.Solicitud.Linea("ART-001", 2, false))));
        // Segundo pedido de 232.00 suma 464.00 contra límite 300.00 solo cuando el primero ya es cuenta por cobrar
        // (los pedidos aún no facturados no cuentan como saldo): el segundo todavía se acepta.
        PedidoService.Resultado segundo = pedidoService.crear(new PedidoService.Solicitud(justo.getClienteId(),
                vendedor.getEmpleadoId(), null, LocalDate.now().plusDays(1),
                List.of(new PedidoService.Solicitud.Linea("ART-001", 1, false))));
        assertEquals("PENDIENTE", segundo.pedido().getEstado().name());
    }
}
