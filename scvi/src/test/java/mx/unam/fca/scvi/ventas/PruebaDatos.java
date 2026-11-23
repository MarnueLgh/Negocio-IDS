package mx.edu.scvi.ventas;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.TipoEmpleado;
import mx.edu.scvi.ventas.model.Zona;
import mx.edu.scvi.ventas.service.ClienteService;
import mx.edu.scvi.ventas.service.EmpleadoService;
import mx.edu.scvi.ventas.service.ZonaService;
import org.springframework.stereotype.Component;

/** Fábrica de datos de prueba (zonas, vendedores, cajeros y clientes únicos) para las pruebas de integración. */
@Component
public class PruebaDatos {

    private static final AtomicInteger SECUENCIA = new AtomicInteger(100000);

    private final ZonaService zonaService;
    private final EmpleadoService empleadoService;
    private final ClienteService clienteService;

    public PruebaDatos(ZonaService zonaService, EmpleadoService empleadoService, ClienteService clienteService) {
        this.zonaService = zonaService;
        this.empleadoService = empleadoService;
        this.clienteService = clienteService;
    }

    public Zona zona() {
        return zonaService.crear("Zona prueba " + SECUENCIA.incrementAndGet());
    }

    public Empleado vendedor(long zonaId, String porcentaje) {
        int n = SECUENCIA.incrementAndGet();
        return empleadoService.registrar(TipoEmpleado.VENDEDOR, new Empleado(null, "Vendedor " + n, "5500000000",
                "Calle Prueba 1", TipoEmpleado.VENDEDOR, new BigDecimal(porcentaje), true, zonaId));
    }

    public Empleado cajero() {
        int n = SECUENCIA.incrementAndGet();
        return empleadoService.registrar(TipoEmpleado.CAJERO, new Empleado(null, "Cajero " + n, "5500000000",
                "Calle Caja 1", TipoEmpleado.CAJERO, new BigDecimal("2.00"), true, null));
    }

    public Cliente cliente(long zonaId, int diasCredito, String limiteCredito) {
        int n = SECUENCIA.incrementAndGet();
        String rfc = "TST" + String.format("%06d", n) + "AB1";
        return clienteService.registrar(new Cliente(null, "Cliente " + n, rfc, "Calle Cliente 1", "5511111111",
                null, "Contacto " + n, new BigDecimal(limiteCredito), diasCredito, true, zonaId));
    }
}
