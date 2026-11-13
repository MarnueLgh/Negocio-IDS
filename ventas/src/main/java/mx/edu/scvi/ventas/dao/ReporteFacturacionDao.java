package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.common.Consultas;
import org.springframework.stereotype.Repository;

/** Consultas de facturación (reportes 13 a 17). Solo facturas EMITIDAS. */
@Repository
public class ReporteFacturacionDao {

    @PersistenceContext
    private EntityManager em;

    /** 13. Facturación por cliente en el periodo. */
    public List<Map<String, Object>> porCliente(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT c.cliente_id AS \"clienteId\", c.nombre AS \"cliente\", "
                + "COUNT(f.factura_id) AS \"facturas\", SUM(f.total) AS \"total\" "
                + "FROM factura f JOIN cliente c ON c.cliente_id = f.cliente_id "
                + "WHERE f.estado = 'EMITIDA' AND f.fecha_factura BETWEEN ? AND ? "
                + "GROUP BY c.cliente_id, c.nombre ORDER BY \"total\" DESC", desde, hasta);
    }

    /** 14. Facturación por vendedor (o cajero, en ventas directas) en el periodo. */
    public List<Map<String, Object>> porVendedor(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT e.empleado_id AS \"vendedorId\", e.nombre AS \"vendedor\", "
                + "COUNT(f.factura_id) AS \"facturas\", SUM(f.total) AS \"total\" "
                + "FROM factura f JOIN empleado e ON e.empleado_id = f.vendedor_id "
                + "WHERE f.estado = 'EMITIDA' AND f.fecha_factura BETWEEN ? AND ? "
                + "GROUP BY e.empleado_id, e.nombre ORDER BY \"total\" DESC", desde, hasta);
    }

    /** 15 y 16. Facturación por producto; opcionalmente ordenada por unidades vendidas (mayor a menor). */
    public List<Map<String, Object>> porProducto(LocalDate desde, LocalDate hasta, boolean ordenarPorUnidades) {
        String orden = ordenarPorUnidades ? "\"unidades\" DESC, \"importe\" DESC" : "\"importe\" DESC";
        return Consultas.filas(em, "SELECT d.articulo_id AS \"articuloId\", d.codigo AS \"codigo\", "
                + "d.descripcion AS \"descripcion\", SUM(d.cantidad) AS \"unidades\", SUM(d.importe) AS \"importe\" "
                + "FROM factura_detalle d JOIN factura f ON f.factura_id = d.factura_id "
                + "WHERE f.estado = 'EMITIDA' AND f.fecha_factura BETWEEN ? AND ? "
                + "GROUP BY d.articulo_id, d.codigo, d.descripcion ORDER BY " + orden, desde, hasta);
    }

    /** 17. Facturación por zona (la zona del cliente) en el periodo. */
    public List<Map<String, Object>> porZona(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT z.zona_id AS \"zonaId\", z.nombre AS \"zona\", "
                + "COUNT(f.factura_id) AS \"facturas\", SUM(f.total) AS \"total\" "
                + "FROM factura f JOIN cliente c ON c.cliente_id = f.cliente_id "
                + "JOIN zona z ON z.zona_id = c.zona_id "
                + "WHERE f.estado = 'EMITIDA' AND f.fecha_factura BETWEEN ? AND ? "
                + "GROUP BY z.zona_id, z.nombre ORDER BY \"total\" DESC", desde, hasta);
    }
}
