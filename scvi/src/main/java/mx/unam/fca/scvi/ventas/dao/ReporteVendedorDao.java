package mx.unam.fca.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.unam.fca.scvi.ventas.common.Consultas;
import org.springframework.stereotype.Repository;

/**
 * Consultas de información del subsistema: vendedores, clientes y zonas (reportes 1 a 6).
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
@Repository
public class ReporteVendedorDao {

    @PersistenceContext
    private EntityManager em;

    /** 1. Comisiones por empleado en el periodo. */
    public List<Map<String, Object>> comisionesPorEmpleado(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT e.empleadoId AS `empleadoId`, e.nombre AS `nombre`, "
                + "e.tipo AS `tipo`, COUNT(c.comisionId) AS `comisiones`, "
                + "COALESCE(SUM(c.importeComision), 0) AS `totalComision` "
                + "FROM Empleado e LEFT JOIN Comision c ON c.empleadoId = e.empleadoId "
                + "AND c.estado = 'GENERADA' AND c.fechaGeneracion BETWEEN ? AND ? "
                + "WHERE e.activo = TRUE OR c.comisionId IS NOT NULL "
                + "GROUP BY e.empleadoId, e.nombre, e.tipo ORDER BY `totalComision` DESC", desde, hasta);
    }

    /** 2. Ventas por vendedor del periodo, de mayor a menor importe. */
    public List<Map<String, Object>> ventasPorVendedor(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT e.empleadoId AS `empleadoId`, e.nombre AS `nombre`, "
                + "e.tipo AS `tipo`, COUNT(*) AS `ventas`, SUM(c.importeVenta) AS `importeVendido` "
                + "FROM Comision c JOIN Empleado e ON e.empleadoId = c.empleadoId "
                + "WHERE c.estado = 'GENERADA' AND c.fechaGeneracion BETWEEN ? AND ? "
                + "GROUP BY e.empleadoId, e.nombre, e.tipo ORDER BY `importeVendido` DESC", desde, hasta);
    }

    /** 3. Clientes que atiende un vendedor (los de su zona). */
    public List<Map<String, Object>> clientesDeVendedor(long vendedorId) {
        return Consultas.filas(em, "SELECT c.clienteId AS `clienteId`, c.nombre AS `nombre`, "
                + "z.nombre AS `zona` FROM Cliente c "
                + "JOIN Zona z ON z.zonaId = c.zonaId "
                + "JOIN Empleado e ON e.zonaId = c.zonaId "
                + "WHERE e.empleadoId = ? AND e.tipo = 'VENDEDOR' AND c.activo = TRUE ORDER BY c.nombre",
                vendedorId);
    }

    /** 4. Vendedores que atienden a un cliente (los de la zona del cliente). */
    public List<Map<String, Object>> vendedoresDeCliente(long clienteId) {
        return Consultas.filas(em, "SELECT e.empleadoId AS `vendedorId`, e.nombre AS `nombre`, "
                + "z.nombre AS `zona` FROM Empleado e "
                + "JOIN Zona z ON z.zonaId = e.zonaId "
                + "JOIN Cliente c ON c.zonaId = e.zonaId "
                + "WHERE c.clienteId = ? AND e.tipo = 'VENDEDOR' AND e.activo = TRUE ORDER BY e.nombre",
                clienteId);
    }

    /** 5. Vendedores por zona. */
    public List<Map<String, Object>> vendedoresPorZona() {
        return Consultas.filas(em, "SELECT z.zonaId AS `zonaId`, z.nombre AS `zona`, "
                + "e.empleadoId AS `vendedorId`, e.nombre AS `vendedor` FROM Empleado e "
                + "JOIN Zona z ON z.zonaId = e.zonaId "
                + "WHERE e.tipo = 'VENDEDOR' AND e.activo = TRUE ORDER BY z.nombre, e.nombre");
    }

    /** 6. Clientes por zona. */
    public List<Map<String, Object>> clientesPorZona() {
        return Consultas.filas(em, "SELECT z.zonaId AS `zonaId`, z.nombre AS `zona`, "
                + "c.clienteId AS `clienteId`, c.nombre AS `cliente` FROM Cliente c "
                + "JOIN Zona z ON z.zonaId = c.zonaId "
                + "WHERE c.activo = TRUE ORDER BY z.nombre, c.nombre");
    }
}
