package mx.unam.fca.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.unam.fca.scvi.ventas.common.Consultas;
import org.springframework.stereotype.Repository;

/** Consultas de facturación (reportes 13 a 17). Solo facturas EMITIDAS. */
@Repository
public class ReporteFacturacionDao {

    @PersistenceContext
    private EntityManager em;

    /** 13. Facturación por cliente en el periodo. */
    public List<Map<String, Object>> porCliente(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT c.clienteId AS `clienteId`, c.nombre AS `cliente`, "
                + "COUNT(f.facturaId) AS `facturas`, SUM(f.total) AS `total` "
                + "FROM Factura f JOIN Cliente c ON c.clienteId = f.clienteId "
                + "WHERE f.estado = 'EMITIDA' AND f.fechaFactura BETWEEN ? AND ? "
                + "GROUP BY c.clienteId, c.nombre ORDER BY `total` DESC", desde, hasta);
    }

    /** 14. Facturación por vendedor (o cajero, en ventas directas) en el periodo. */
    public List<Map<String, Object>> porVendedor(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT e.empleadoId AS `vendedorId`, e.nombre AS `vendedor`, "
                + "COUNT(f.facturaId) AS `facturas`, SUM(f.total) AS `total` "
                + "FROM Factura f JOIN Empleado e ON e.empleadoId = f.vendedorId "
                + "WHERE f.estado = 'EMITIDA' AND f.fechaFactura BETWEEN ? AND ? "
                + "GROUP BY e.empleadoId, e.nombre ORDER BY `total` DESC", desde, hasta);
    }

    /** 15 y 16. Facturación por producto; opcionalmente ordenada por unidades vendidas (mayor a menor). */
    public List<Map<String, Object>> porProducto(LocalDate desde, LocalDate hasta, boolean ordenarPorUnidades) {
        String orden = ordenarPorUnidades ? "`unidades` DESC, `importe` DESC" : "`importe` DESC";
        return Consultas.filas(em, "SELECT d.articuloId AS `articuloId`, d.codigo AS `codigo`, "
                + "d.descripcion AS `descripcion`, SUM(d.cantidad) AS `unidades`, SUM(d.importe) AS `importe` "
                + "FROM FacturaDetalle d JOIN Factura f ON f.facturaId = d.facturaId "
                + "WHERE f.estado = 'EMITIDA' AND f.fechaFactura BETWEEN ? AND ? "
                + "GROUP BY d.articuloId, d.codigo, d.descripcion ORDER BY " + orden, desde, hasta);
    }

    /** 17. Facturación por zona (la zona del cliente) en el periodo. */
    public List<Map<String, Object>> porZona(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT z.zonaId AS `zonaId`, z.nombre AS `zona`, "
                + "COUNT(f.facturaId) AS `facturas`, SUM(f.total) AS `total` "
                + "FROM Factura f JOIN Cliente c ON c.clienteId = f.clienteId "
                + "JOIN Zona z ON z.zonaId = c.zonaId "
                + "WHERE f.estado = 'EMITIDA' AND f.fechaFactura BETWEEN ? AND ? "
                + "GROUP BY z.zonaId, z.nombre ORDER BY `total` DESC", desde, hasta);
    }
}
