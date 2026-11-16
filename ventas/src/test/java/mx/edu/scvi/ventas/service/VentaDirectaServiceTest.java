package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import mx.edu.scvi.ventas.PruebaDatos;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.NoEncontradoException;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.integracion.InventarioGateway;
import mx.edu.scvi.ventas.model.EstadoVenta;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.FormaPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@PruebaIntegracion
class VentaDirectaServiceTest {

    @Autowired
    private VentaDirectaService ventaService;
    @Autowired
    private CorteCajaService corteService;
    @Autowired
    private InventarioGateway inventario;
    @Autowired
    private PruebaDatos datos;
    @Autowired
    private JdbcTemplate jdbc;

    private Empleado cajero;

    @BeforeEach
    void abrirCaja() {
        cajero = datos.cajero();
        corteService.abrir(cajero.getEmpleadoId(), 1, new BigDecimal("500.00"));
    }

    private VentaDirectaService.Solicitud solicitud(String codigo, int cantidad, FormaPago forma, String recibido) {
        return new VentaDirectaService.Solicitud(cajero.getEmpleadoId(),
                List.of(new VentaDirectaService.Solicitud.Linea(codigo, cantidad)), forma,
                recibido == null ? null : new BigDecimal(recibido), null, false);
    }

    private void assertDinero(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real), "esperado " + esperado + " pero fue " + real);
    }

    @Test
    void calculaSubtotalIvaTotalYCambioYDescuentaInventario() {
        int antes = inventario.buscarPorCodigo("ART-001").orElseThrow().existencias();

        VentaDirectaService.ResultadoVenta r = ventaService.registrar(solicitud("ART-001", 2, FormaPago.EFECTIVO, "300.00"));

        assertDinero("200.00", r.venta().getSubtotal());
        assertDinero("32.00", r.venta().getIva());
        assertDinero("232.00", r.venta().getTotal());
        assertDinero("68.00", r.pago().getCambio());
        assertNotNull(r.ticket());
        assertEquals(antes - 2, inventario.buscarPorCodigo("ART-001").orElseThrow().existencias());
    }

    @Test
    void sinExistenciasNoPermiteLaVenta() {
        assertThrows(ReglaNegocioException.class,
                () -> ventaService.registrar(solicitud("ART-002", 4, FormaPago.TARJETA, null)));
        assertEquals(3, inventario.buscarPorCodigo("ART-002").orElseThrow().existencias());
    }

    @Test
    void articuloInexistenteDa404() {
        assertThrows(NoEncontradoException.class,
                () -> ventaService.registrar(solicitud("NO-EXISTE", 1, FormaPago.TARJETA, null)));
    }

    @Test
    void efectivoInsuficienteSeRechaza() {
        assertThrows(ReglaNegocioException.class,
                () -> ventaService.registrar(solicitud("ART-001", 2, FormaPago.EFECTIVO, "100.00")));
    }

    @Test
    void sinCajaAbiertaNoSeVende() {
        Empleado otro = datos.cajero();
        VentaDirectaService.Solicitud sinCaja = new VentaDirectaService.Solicitud(otro.getEmpleadoId(),
                List.of(new VentaDirectaService.Solicitud.Linea("ART-001", 1)), FormaPago.TARJETA, null, null, false);
        assertThrows(ReglaNegocioException.class, () -> ventaService.registrar(sinCaja));
    }

    @Test
    void solicitarFacturaExigeCliente() {
        VentaDirectaService.Solicitud sinCliente = new VentaDirectaService.Solicitud(cajero.getEmpleadoId(),
                List.of(new VentaDirectaService.Solicitud.Linea("ART-001", 1)), FormaPago.TARJETA, null, null, true);
        assertThrows(ReglaNegocioException.class, () -> ventaService.registrar(sinCliente));
    }

    @Test
    void generaComisionDelCajeroSobreElSubtotal() {
        VentaDirectaService.ResultadoVenta r = ventaService.registrar(solicitud("ART-001", 2, FormaPago.TARJETA, null));
        BigDecimal comision = jdbc.queryForObject("SELECT importe_comision FROM comision WHERE venta_id = ?",
                BigDecimal.class, r.venta().getVentaId());
        assertDinero("4.00", comision);
    }

    @Test
    void cancelarRegresaInventarioYCancelaComision() {
        int antes = inventario.buscarPorCodigo("ART-001").orElseThrow().existencias();
        VentaDirectaService.ResultadoVenta r = ventaService.registrar(solicitud("ART-001", 3, FormaPago.TARJETA, null));

        assertEquals(EstadoVenta.CANCELADA, ventaService.cancelar(r.venta().getVentaId()).getEstado());

        assertEquals(antes, inventario.buscarPorCodigo("ART-001").orElseThrow().existencias());
        String estado = jdbc.queryForObject("SELECT estado FROM comision WHERE venta_id = ?", String.class,
                r.venta().getVentaId());
        assertEquals("CANCELADA", estado);
        assertThrows(ReglaNegocioException.class, () -> ventaService.cancelar(r.venta().getVentaId()));
    }
}
