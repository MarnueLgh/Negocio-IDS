package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.PruebaDatos;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.model.CorteCaja;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.FormaPago;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PruebaIntegracion
class CorteCajaServiceTest {

    @Autowired
    private CorteCajaService corteService;
    @Autowired
    private VentaDirectaService ventaService;
    @Autowired
    private PruebaDatos datos;

    private void vender(Empleado cajero, FormaPago forma, String recibido) {
        ventaService.registrar(new VentaDirectaService.Solicitud(cajero.getEmpleadoId(),
                List.of(new VentaDirectaService.Solicitud.Linea("ART-001", 2)), forma,
                recibido == null ? null : new BigDecimal(recibido), null, false));
    }

    private void assertDinero(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real), "esperado " + esperado + " pero fue " + real);
    }

    @Test
    void noPermiteDosCajasAbiertasParaElMismoCajero() {
        Empleado cajero = datos.cajero();
        corteService.abrir(cajero.getEmpleadoId(), 11, new BigDecimal("100.00"));
        assertThrows(ReglaNegocioException.class,
                () -> corteService.abrir(cajero.getEmpleadoId(), 12, new BigDecimal("100.00")));
    }

    @Test
    void cuadraCuandoElEfectivoFisicoCoincide() {
        Empleado cajero = datos.cajero();
        CorteCaja abierto = corteService.abrir(cajero.getEmpleadoId(), 21, new BigDecimal("500.00"));
        vender(cajero, FormaPago.EFECTIVO, "232.00");
        vender(cajero, FormaPago.TARJETA, null);

        CorteCaja cerrado = corteService.cerrar(abierto.getCorteCajaId(), new BigDecimal("732.00"));

        assertEquals(2, cerrado.getTotalVentas());
        assertDinero("732.00", cerrado.getImporteEsperado());
        assertDinero("0.00", cerrado.getDiferencia());
        assertFalse(cerrado.isHayDiferencia());
    }

    @Test
    void registraLaDiferenciaCuandoFaltaEfectivo() {
        Empleado cajero = datos.cajero();
        CorteCaja abierto = corteService.abrir(cajero.getEmpleadoId(), 31, new BigDecimal("500.00"));
        vender(cajero, FormaPago.EFECTIVO, "232.00");

        CorteCaja cerrado = corteService.cerrar(abierto.getCorteCajaId(), new BigDecimal("700.00"));

        assertDinero("-32.00", cerrado.getDiferencia());
        assertTrue(cerrado.isHayDiferencia());

        LocalDate hoy = LocalDate.now();
        assertTrue(corteService.cortes(hoy, hoy, true).stream()
                .anyMatch(c -> c.getCorteCajaId().equals(cerrado.getCorteCajaId())));
    }

    @Test
    void noSePuedeCerrarDosVecesNiVenderDespuesDelCorte() {
        Empleado cajero = datos.cajero();
        CorteCaja abierto = corteService.abrir(cajero.getEmpleadoId(), 41, new BigDecimal("100.00"));
        corteService.cerrar(abierto.getCorteCajaId(), new BigDecimal("100.00"));

        assertThrows(ReglaNegocioException.class,
                () -> corteService.cerrar(abierto.getCorteCajaId(), new BigDecimal("100.00")));
        assertThrows(ReglaNegocioException.class, () -> vender(cajero, FormaPago.TARJETA, null));
    }

    @Test
    void reporteDeCajaAgrupaVentasDelPeriodo() {
        Empleado cajero = datos.cajero();
        corteService.abrir(cajero.getEmpleadoId(), 51, new BigDecimal("100.00"));
        vender(cajero, FormaPago.TARJETA, null);
        LocalDate hoy = LocalDate.now();
        assertFalse(corteService.ventasPorCaja(hoy, hoy).isEmpty());
    }
}
