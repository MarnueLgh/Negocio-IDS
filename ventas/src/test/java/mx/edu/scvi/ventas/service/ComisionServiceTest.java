package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.PruebaDatos;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.model.Comision;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PruebaIntegracion
class ComisionServiceTest {

    @Autowired
    private ComisionService comisionService;
    @Autowired
    private ReporteVendedorService reporteService;
    @Autowired
    private PruebaDatos datos;

    @Test
    void calculaLaComisionConElPorcentajeDelEmpleado() {
        Zona zona = datos.zona();
        Empleado vendedor = datos.vendedor(zona.getZonaId(), "3.00");

        Comision comision = comisionService.generarPorFactura(501L, vendedor.getEmpleadoId(), new BigDecimal("1000.00"));

        assertEquals(0, new BigDecimal("30.00").compareTo(comision.getImporteComision()));
        assertEquals("GENERADA", comision.getEstado());
    }

    @Test
    void cancelarPorFacturaLaExcluyeDelReporte() {
        Zona zona = datos.zona();
        Empleado vendedor = datos.vendedor(zona.getZonaId(), "5.00");
        comisionService.generarPorFactura(601L, vendedor.getEmpleadoId(), new BigDecimal("200.00"));
        comisionService.generarPorFactura(602L, vendedor.getEmpleadoId(), new BigDecimal("100.00"));

        assertEquals(1, comisionService.cancelarPorFactura(601L));

        LocalDate hoy = LocalDate.now();
        List<Comision> vigentes = comisionService.listarPorEmpleado(vendedor.getEmpleadoId(), hoy, hoy);
        assertEquals(1, vigentes.size());
        assertEquals(0, new BigDecimal("5.00").compareTo(vigentes.get(0).getImporteComision()));
    }

    @Test
    void reporteDeVentasPorVendedorVaDeMayorAMenorImporte() {
        Zona zona = datos.zona();
        Empleado chico = datos.vendedor(zona.getZonaId(), "3.00");
        Empleado grande = datos.vendedor(zona.getZonaId(), "3.00");
        comisionService.generarPorFactura(701L, chico.getEmpleadoId(), new BigDecimal("100.00"));
        comisionService.generarPorFactura(702L, grande.getEmpleadoId(), new BigDecimal("900.00"));

        LocalDate hoy = LocalDate.now();
        List<Map<String, Object>> filas = reporteService.ventasPorVendedor(hoy, hoy);

        assertEquals(grande.getEmpleadoId().longValue(), ((Number) filas.get(0).get("empleadoId")).longValue());
    }

    @Test
    void reporteDeClientesYVendedoresPorZona() {
        Zona zona = datos.zona();
        Empleado vendedor = datos.vendedor(zona.getZonaId(), "3.00");
        datos.cliente(zona.getZonaId(), 30, "5000.00");

        assertEquals(1, reporteService.clientesPorZona(zona.getZonaId()).size());
        assertEquals(1, reporteService.clientesDeVendedor(vendedor.getEmpleadoId()).size());
        assertEquals(1, reporteService.vendedoresPorZona(zona.getZonaId()).size());
    }
}
