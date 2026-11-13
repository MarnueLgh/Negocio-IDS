package mx.edu.scvi.ventas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.PedidoDao;
import mx.edu.scvi.ventas.dao.ReporteFacturacionDao;
import mx.edu.scvi.ventas.model.Pedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reportes de pedidos pendientes (9 a 11) y de facturación (13 a 17). Los reportes 12 y 18 a 20 viven en sus servicios. */
@Service
@Transactional
public class ReporteFacturacionService {

    private final PedidoDao pedidoDao;
    private final ReporteFacturacionDao reporteDao;

    public ReporteFacturacionService(PedidoDao pedidoDao, ReporteFacturacionDao reporteDao) {
        this.pedidoDao = pedidoDao;
        this.reporteDao = reporteDao;
    }

    public List<Pedido> pedidosPendientes(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return pedidoDao.pendientesPorPeriodo(desde, hasta);
    }

    public List<Pedido> pedidosPendientesDeCliente(long clienteId) {
        return pedidoDao.pendientesDeCliente(clienteId);
    }

    public List<Pedido> pedidosPendientesDeVendedor(long vendedorId) {
        return pedidoDao.pendientesDeVendedor(vendedorId);
    }

    public List<Map<String, Object>> facturacionPorCliente(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return reporteDao.porCliente(desde, hasta);
    }

    public List<Map<String, Object>> facturacionPorVendedor(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return reporteDao.porVendedor(desde, hasta);
    }

    /** Reporte 15: por producto; si se indica articuloId, solo ese producto. */
    public List<Map<String, Object>> facturacionPorProducto(LocalDate desde, LocalDate hasta, Long articuloId) {
        Reglas.periodo(desde, hasta);
        List<Map<String, Object>> filas = reporteDao.porProducto(desde, hasta, false);
        if (articuloId == null) {
            return filas;
        }
        return filas.stream()
                .filter(fila -> articuloId.equals(((Number) fila.get("articuloId")).longValue()))
                .toList();
    }

    /** Reporte 16: artículos de mayor a menor número de unidades facturadas. */
    public List<Map<String, Object>> rankingArticulos(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return reporteDao.porProducto(desde, hasta, true);
    }

    public List<Map<String, Object>> facturacionPorZona(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return reporteDao.porZona(desde, hasta);
    }
}
