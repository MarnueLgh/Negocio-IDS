package mx.unam.fca.scvi.ventas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.unam.fca.scvi.ventas.common.Reglas;
import mx.unam.fca.scvi.ventas.dao.ClienteDao;
import mx.unam.fca.scvi.ventas.dao.PedidoDao;
import mx.unam.fca.scvi.ventas.dao.ReporteFacturacionDao;
import mx.unam.fca.scvi.ventas.dao.VendedorDao;
import mx.unam.fca.scvi.ventas.integracion.InventarioGateway;
import mx.unam.fca.scvi.ventas.model.Pedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reportes de pedidos pendientes (9 a 11) y de facturación (13 a 17). Los reportes 12 y 18 a 20 viven en sus servicios.
 *
 * Subsistema: Ventas.
 *
 * @author Álvarez Aguilar Saúl Antonio Equipo 2
 * @author Pérez Flores Roberto Carlos Equipo 2
 * @author Pérez Muñoz Diego Equipo 2
 *
 * Fecha creación: 15-11-2026
 *
 * Fecha modificación: 23-11-2026
 */
@Service
@Transactional
public class ReporteFacturacionService {

    private final PedidoDao pedidoDao;
    private final ReporteFacturacionDao reporteDao;
    private final ClienteDao clienteDao;
    private final VendedorDao vendedorDao;
    private final InventarioGateway inventario;

    public ReporteFacturacionService(PedidoDao pedidoDao, ReporteFacturacionDao reporteDao, ClienteDao clienteDao,
                                     VendedorDao vendedorDao, InventarioGateway inventario) {
        this.clienteDao = clienteDao;
        this.vendedorDao = vendedorDao;
        this.inventario = inventario;
        this.pedidoDao = pedidoDao;
        this.reporteDao = reporteDao;
    }

    public List<Pedido> pedidosPendientes(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return Reglas.conDatos(pedidoDao.pendientesPorPeriodo(desde, hasta));
    }

    public List<Pedido> pedidosPendientesDeCliente(long clienteId) {
        Reglas.existe(clienteDao.buscarCliente(clienteId));
        return Reglas.conDatos(pedidoDao.pendientesDeCliente(clienteId));
    }

    public List<Pedido> pedidosPendientesDeVendedor(long vendedorId) {
        Reglas.existe(vendedorDao.buscarPorId(vendedorId));
        return Reglas.conDatos(pedidoDao.pendientesDeVendedor(vendedorId));
    }

    public List<Map<String, Object>> facturacionPorCliente(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return Reglas.conDatos(reporteDao.porCliente(desde, hasta));
    }

    public List<Map<String, Object>> facturacionPorVendedor(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return Reglas.conDatos(reporteDao.porVendedor(desde, hasta));
    }

    /** Reporte 15: por producto; si se indica articuloId, solo ese producto. */
    public List<Map<String, Object>> facturacionPorProducto(LocalDate desde, LocalDate hasta, Long articuloId) {
        Reglas.periodo(desde, hasta);
        List<Map<String, Object>> filas = reporteDao.porProducto(desde, hasta, false);
        if (articuloId == null) {
            return Reglas.conDatos(filas);
        }
        Reglas.existe(inventario.buscarPorId(articuloId));
        return Reglas.conDatos(filas.stream()
                .filter(fila -> articuloId.equals(((Number) fila.get("articuloId")).longValue()))
                .toList());
    }

    /** Reporte 16: artículos de mayor a menor número de unidades facturadas. */
    public List<Map<String, Object>> rankingArticulos(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return Reglas.conDatos(reporteDao.porProducto(desde, hasta, true));
    }

    public List<Map<String, Object>> facturacionPorZona(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return Reglas.conDatos(reporteDao.porZona(desde, hasta));
    }
}
