package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.PruebaDatos;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.integracion.InventarioGateway;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.EstadoPedido;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PruebaIntegracion
class PedidoServiceTest {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private InventarioGateway inventario;
    @Autowired
    private PruebaDatos datos;

    private Zona zona;
    private Empleado vendedor;
    private Cliente cliente;

    @BeforeEach
    void preparar() {
        zona = datos.zona();
        vendedor = datos.vendedor(zona.getZonaId(), "3.00");
        cliente = datos.cliente(zona.getZonaId(), 30, "10000.00");
    }

    private PedidoService.Solicitud solicitud(Cliente c, Empleado v, Integer dias, LocalDate entrega,
                                              String codigo, int cantidad, boolean promesa) {
        return new PedidoService.Solicitud(c.getClienteId(), v.getEmpleadoId(), dias, entrega,
                List.of(new PedidoService.Solicitud.Linea(codigo, cantidad, promesa)));
    }

    private void assertDinero(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real), "esperado " + esperado + " pero fue " + real);
    }

    @Test
    void creaPedidoPendienteConLosDiasDelClienteYSinMoverInventario() {
        int antes = inventario.buscarPorCodigo("ART-001").orElseThrow().existencias();

        PedidoService.Resultado r = pedidoService.crear(
                solicitud(cliente, vendedor, null, LocalDate.now().plusDays(2), "ART-001", 2, false));

        assertEquals(EstadoPedido.PENDIENTE, r.pedido().getEstado());
        assertEquals(30, r.pedido().getDiasCredito());
        assertDinero("232.00", r.pedido().getTotal());
        assertEquals(antes, inventario.buscarPorCodigo("ART-001").orElseThrow().existencias());
    }

    @Test
    void vendedorDeOtraZonaNoPuedeVenderleAlCliente() {
        Zona otra = datos.zona();
        Empleado ajeno = datos.vendedor(otra.getZonaId(), "3.00");
        assertThrows(ReglaNegocioException.class, () -> pedidoService.crear(
                solicitud(cliente, ajeno, null, LocalDate.now().plusDays(2), "ART-001", 1, false)));
    }

    @Test
    void noPermiteExcederElInventarioSinPromesa() {
        assertThrows(ReglaNegocioException.class, () -> pedidoService.crear(
                solicitud(cliente, vendedor, null, LocalDate.now().plusDays(2), "ART-002", 5, false)));
    }

    @Test
    void laPromesaRequiereAlMenosUnDiaDeAnticipacion() {
        assertThrows(ReglaNegocioException.class, () -> pedidoService.crear(
                solicitud(cliente, vendedor, null, LocalDate.now(), "ART-002", 5, true)));
    }

    @Test
    void entregaParcialDejaPendienteLoQueNoHayEnInventario() {
        PedidoService.Resultado creado = pedidoService.crear(
                solicitud(cliente, vendedor, null, LocalDate.now().plusDays(3), "ART-002", 5, true));

        PedidoService.Resultado entregado = pedidoService.entregar(creado.pedido().getPedidoId());

        assertEquals(EstadoPedido.PENDIENTE, entregado.pedido().getEstado());
        assertEquals(2, entregado.detalles().get(0).getCantidadPendiente());
        assertEquals(0, inventario.buscarPorCodigo("ART-002").orElseThrow().existencias());
        assertThrows(ReglaNegocioException.class, () -> pedidoService.entregar(creado.pedido().getPedidoId()));
        assertThrows(ReglaNegocioException.class, () -> pedidoService.cancelar(creado.pedido().getPedidoId()));
    }

    @Test
    void entregaCompletaMarcaElPedidoComoEntregado() {
        PedidoService.Resultado creado = pedidoService.crear(
                solicitud(cliente, vendedor, null, LocalDate.now().plusDays(1), "ART-001", 2, false));
        PedidoService.Resultado entregado = pedidoService.entregar(creado.pedido().getPedidoId());
        assertEquals(EstadoPedido.ENTREGADO, entregado.pedido().getEstado());
    }

    @Test
    void rechazaCreditoQueExcedeElLimiteOLosDiasOtorgados() {
        Cliente chico = datos.cliente(zona.getZonaId(), 30, "100.00");
        assertThrows(ReglaNegocioException.class, () -> pedidoService.crear(
                solicitud(chico, vendedor, null, LocalDate.now().plusDays(1), "ART-001", 2, false)));
        assertThrows(ReglaNegocioException.class, () -> pedidoService.crear(
                solicitud(cliente, vendedor, 45, LocalDate.now().plusDays(1), "ART-001", 1, false)));
    }

    @Test
    void pedidoPendienteSinEntregasSePuedeCancelar() {
        PedidoService.Resultado creado = pedidoService.crear(
                solicitud(cliente, vendedor, 0, LocalDate.now().plusDays(1), "ART-001", 1, false));
        assertEquals(EstadoPedido.CANCELADO, pedidoService.cancelar(creado.pedido().getPedidoId()).getEstado());
    }
}
