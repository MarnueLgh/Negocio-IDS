package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.TipoEmpleado;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PruebaIntegracion
class EmpleadoServiceTest {

    @Autowired
    private EmpleadoService empleadoService;
    @Autowired
    private ZonaService zonaService;

    private Empleado datos(String porcentaje, Long zonaId) {
        return new Empleado(null, "Persona Prueba", "5512345678", "Calle Prueba 1", null,
                porcentaje == null ? null : new BigDecimal(porcentaje), true, zonaId);
    }

    @Test
    void vendedorRequiereZona() {
        assertThrows(ReglaNegocioException.class,
                () -> empleadoService.registrar(TipoEmpleado.VENDEDOR, datos("3.00", null)));
    }

    @Test
    void vendedorSeRegistraEnUnaZonaActiva() {
        Zona zona = zonaService.crear("Zona Vendedores");
        Empleado vendedor = empleadoService.registrar(TipoEmpleado.VENDEDOR, datos("3.50", zona.getZonaId()));
        assertEquals(zona.getZonaId(), vendedor.getZonaId());
        assertEquals(TipoEmpleado.VENDEDOR, vendedor.getTipo());
    }

    @Test
    void cajeroNoTieneZonaAunqueSeEnvie() {
        Zona zona = zonaService.crear("Zona Ignorada");
        Empleado cajero = empleadoService.registrar(TipoEmpleado.CAJERO, datos("2.00", zona.getZonaId()));
        assertNull(cajero.getZonaId());
    }

    @Test
    void rechazaPorcentajeFueraDeRango() {
        Zona zona = zonaService.crear("Zona Porcentaje");
        assertThrows(ReglaNegocioException.class,
                () -> empleadoService.registrar(TipoEmpleado.VENDEDOR, datos("120", zona.getZonaId())));
        assertThrows(ReglaNegocioException.class,
                () -> empleadoService.registrar(TipoEmpleado.VENDEDOR, datos(null, zona.getZonaId())));
    }

    @Test
    void modificarNoCambiaElPorcentajeDeComision() {
        Zona zona = zonaService.crear("Zona Modificar");
        Empleado vendedor = empleadoService.registrar(TipoEmpleado.VENDEDOR, datos("3.00", zona.getZonaId()));
        Empleado cambios = new Empleado(null, "Nombre Nuevo", "5500000001", "Otra calle 2", null,
                new BigDecimal("9.00"), true, zona.getZonaId());

        Empleado actualizado = empleadoService.modificar(TipoEmpleado.VENDEDOR, vendedor.getEmpleadoId(), cambios, null);

        assertEquals("Nombre Nuevo", actualizado.getNombre());
        assertEquals(0, new BigDecimal("3.00").compareTo(actualizado.getPorcentajeComision()));
    }

    @Test
    void bajaEsLogica() {
        Zona zona = zonaService.crear("Zona Baja");
        Empleado vendedor = empleadoService.registrar(TipoEmpleado.VENDEDOR, datos("3.00", zona.getZonaId()));
        empleadoService.baja(TipoEmpleado.VENDEDOR, vendedor.getEmpleadoId());
        assertEquals(false, empleadoService.obtener(TipoEmpleado.VENDEDOR, vendedor.getEmpleadoId()).isActivo());
    }
}
