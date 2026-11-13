package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@PruebaIntegracion
class ZonaServiceTest {

    @Autowired
    private ZonaService zonaService;
    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void creaZonaActivaYRechazaNombreDuplicado() {
        Zona zona = zonaService.crear("Zona Centro Prueba");
        assertTrue(zona.isActivo());
        assertThrows(ReglaNegocioException.class, () -> zonaService.crear("zona centro prueba"));
    }

    @Test
    void noPermiteEliminarZonaConClientes() {
        Zona zona = zonaService.crear("Zona Con Clientes");
        insertarCliente("ZCC010101AB1", zona.getZonaId());
        assertThrows(ReglaNegocioException.class, () -> zonaService.eliminar(zona.getZonaId()));
    }

    @Test
    void eliminaZonaVacia() {
        Zona zona = zonaService.crear("Zona Vacia");
        zonaService.eliminar(zona.getZonaId());
        assertThrows(RuntimeException.class, () -> zonaService.obtener(zona.getZonaId()));
    }

    @Test
    void unirMueveClientesYDesactivaLaZonaOrigen() {
        Zona origen = zonaService.crear("Zona Origen");
        Zona destino = zonaService.crear("Zona Destino");
        insertarCliente("ZOR010101AB1", origen.getZonaId());
        insertarCliente("ZOR020202AB2", origen.getZonaId());

        ZonaService.MovimientoZona resultado = zonaService.unir(origen.getZonaId(), destino.getZonaId());

        assertEquals(2, resultado.clientesMovidos());
        assertFalse(zonaService.obtener(origen.getZonaId()).isActivo());
        assertEquals(2, clientesEn(destino.getZonaId()));
    }

    @Test
    void dividirCreaZonaNuevaConLosClientesIndicados() {
        Zona zona = zonaService.crear("Zona Grande");
        long c1 = insertarCliente("ZGR010101AB1", zona.getZonaId());
        insertarCliente("ZGR020202AB2", zona.getZonaId());

        Zona nueva = zonaService.dividir(zona.getZonaId(), "Zona Grande 2", List.of(c1));

        assertEquals(1, clientesEn(nueva.getZonaId()));
        assertEquals(1, clientesEn(zona.getZonaId()));
    }

    @Test
    void dividirRechazaClientesDeOtraZona() {
        Zona zona = zonaService.crear("Zona A");
        Zona otra = zonaService.crear("Zona B");
        long ajeno = insertarCliente("ZAJ010101AB1", otra.getZonaId());
        assertThrows(ReglaNegocioException.class,
                () -> zonaService.dividir(zona.getZonaId(), "Zona A2", List.of(ajeno)));
    }

    private long insertarCliente(String rfc, long zonaId) {
        jdbc.update("INSERT INTO cliente (nombre, rfc, domicilio, telefono, limite_credito, dias_credito, activo, zona_id) "
                + "VALUES (?, ?, 'Calle X', '5500000000', 0, 0, TRUE, ?)", "Cliente " + rfc, rfc, zonaId);
        Long id = jdbc.queryForObject("SELECT cliente_id FROM cliente WHERE rfc = ?", Long.class, rfc);
        return id == null ? -1 : id;
    }

    private int clientesEn(long zonaId) {
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM cliente WHERE zona_id = ?", Integer.class, zonaId);
        return total == null ? 0 : total;
    }
}
