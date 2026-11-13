package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.common.NoEncontradoException;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PruebaIntegracion
class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ZonaService zonaService;

    private Cliente datos(String rfc, Long zonaId) {
        return new Cliente(null, "Cliente Prueba", rfc, "Calle 1", "5511223344", "5599887766", "Ana Contacto",
                new BigDecimal("10000.00"), 30, true, zonaId);
    }

    @Test
    void registraClienteConRfcNormalizado() {
        Zona zona = zonaService.crear("Zona Clientes");
        Cliente cliente = clienteService.registrar(datos("abc010101xy1", zona.getZonaId()));
        assertEquals("ABC010101XY1", cliente.getRfc());
        assertEquals(zona.getZonaId(), cliente.getZonaId());
    }

    @Test
    void rechazaRfcInvalidoYDuplicado() {
        Zona zona = zonaService.crear("Zona Rfc");
        assertThrows(ReglaNegocioException.class, () -> clienteService.registrar(datos("123", zona.getZonaId())));
        clienteService.registrar(datos("DUP010101AB1", zona.getZonaId()));
        assertThrows(ReglaNegocioException.class,
                () -> clienteService.registrar(datos("DUP010101AB1", zona.getZonaId())));
    }

    @Test
    void exigeZonaExistente() {
        assertThrows(NoEncontradoException.class, () -> clienteService.registrar(datos("ZEX010101AB1", 999999L)));
        assertThrows(ReglaNegocioException.class, () -> clienteService.registrar(datos("ZEX020202AB1", null)));
    }

    @Test
    void bajaEsLogica() {
        Zona zona = zonaService.crear("Zona Baja Cliente");
        Cliente cliente = clienteService.registrar(datos("BAJ010101AB1", zona.getZonaId()));
        clienteService.baja(cliente.getClienteId());
        assertFalse(clienteService.obtener(cliente.getClienteId()).isActivo());
    }

    @Test
    void validarCreditoRespetaLimiteYDias() {
        Cliente cliente = datos("VAL010101AB1", 1L);
        assertTrue(cliente.validarCredito(new BigDecimal("10000.00"), 30));
        assertFalse(cliente.validarCredito(new BigDecimal("10000.01"), 30));
        assertFalse(cliente.validarCredito(new BigDecimal("500.00"), 45));
    }
}
