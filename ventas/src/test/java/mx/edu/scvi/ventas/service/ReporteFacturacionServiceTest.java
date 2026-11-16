package mx.edu.scvi.ventas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.PruebaDatos;
import mx.edu.scvi.ventas.PruebaIntegracion;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PruebaIntegracion
class ReporteFacturacionServiceTest {

    @Autowired
    private ReporteFacturacionService reportes;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private FacturacionService facturacion;
    @Autowired
    private PruebaDatos datos;

    private Zona zona;
    private Empleado vendedor;
    private Cliente cliente;
    private long pedidoId;

    @BeforeEach
    void facturarUnPedido() {
        zona = datos.zona();
        vendedor = datos.vendedor(zona.getZonaId(), "3.00");
        cliente = datos.cliente(zona.getZonaId(), 30, "10000.00");
        PedidoService.Resultado creado = pedidoService.crear(new PedidoService.Solicitud(cliente.getClienteId(),
                vendedor.getEmpleadoId(), null, LocalDate.now().plusDays(1),
                List.of(new PedidoService.Solicitud.Linea("ART-003", 10, false))));
        pedidoId = creado.pedido().getPedidoId();
        pedidoService.entregar(pedidoId);
        facturacion.facturarPedido(pedidoId, null);
    }

    private boolean contiene(List<Map<String, Object>> filas, String llave, long valor) {
        return filas.stream().anyMatch(f -> ((Number) f.get(llave)).longValue() == valor);
    }

    @Test
    void facturacionPorClienteVendedorYZona() {
        LocalDate hoy = LocalDate.now();
        assertTrue(contiene(reportes.facturacionPorCliente(hoy, hoy), "clienteId", cliente.getClienteId()));
        assertTrue(contiene(reportes.facturacionPorVendedor(hoy, hoy), "vendedorId", vendedor.getEmpleadoId()));
        assertTrue(contiene(reportes.facturacionPorZona(hoy, hoy), "zonaId", zona.getZonaId()));
    }

    @Test
    void facturacionPorProductoYRankingDeArticulos() {
        LocalDate hoy = LocalDate.now();
        List<Map<String, Object>> productos = reportes.facturacionPorProducto(hoy, hoy, null);
        assertTrue(productos.stream().anyMatch(f -> "ART-003".equals(f.get("codigo"))));

        List<Map<String, Object>> ranking = reportes.rankingArticulos(hoy, hoy);
        long primero = ((Number) ranking.get(0).get("unidades")).longValue();
        long ultimo = ((Number) ranking.get(ranking.size() - 1).get("unidades")).longValue();
        assertTrue(primero >= ultimo);
    }

    @Test
    void facturadoYaNoApareceComoPedidoPendiente() {
        LocalDate hoy = LocalDate.now();
        assertEquals(0, reportes.pedidosPendientesDeCliente(cliente.getClienteId()).size());
        assertTrue(reportes.pedidosPendientes(hoy, hoy).stream().noneMatch(p -> p.getPedidoId() == pedidoId));
    }
}
