package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.common.Consultas;
import org.springframework.stereotype.Repository;

/** Consultas de información del subsistema: vendedores, clientes y zonas (reportes 1 a 6). */
@Repository
public class ReporteVendedorDao {

    @PersistenceContext
    private EntityManager em;

    /** 1. Comisiones por empleado en el periodo. */
    public List<Map<String, Object>> comisionesPorEmpleado(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT e.empleado_id AS \"empleadoId\", e.nombre AS \"nombre\", "
                + "e.tipo AS \"tipo\", COUNT(*) AS \"comisiones\", SUM(c.importe_comision) AS \"totalComision\" "
                + "FROM comision c JOIN empleado e ON e.empleado_id = c.empleado_id "
                + "WHERE c.estado = 'GENERADA' AND c.fecha_generacion BETWEEN ? AND ? "
                + "GROUP BY e.empleado_id, e.nombre, e.tipo ORDER BY \"totalComision\" DESC", desde, hasta);
    }

    /** 2. Ventas por vendedor del periodo, de mayor a menor importe. */
    public List<Map<String, Object>> ventasPorVendedor(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT e.empleado_id AS \"empleadoId\", e.nombre AS \"nombre\", "
                + "e.tipo AS \"tipo\", COUNT(*) AS \"ventas\", SUM(c.importe_venta) AS \"importeVendido\" "
                + "FROM comision c JOIN empleado e ON e.empleado_id = c.empleado_id "
                + "WHERE c.estado = 'GENERADA' AND c.fecha_generacion BETWEEN ? AND ? "
                + "GROUP BY e.empleado_id, e.nombre, e.tipo ORDER BY \"importeVendido\" DESC", desde, hasta);
    }

    /** 3. Clientes que atiende un vendedor (los de su zona). */
    public List<Map<String, Object>> clientesDeVendedor(long vendedorId) {
        return Consultas.filas(em, "SELECT c.cliente_id AS \"clienteId\", c.nombre AS \"nombre\", "
                + "z.nombre AS \"zona\" FROM cliente c "
                + "JOIN zona z ON z.zona_id = c.zona_id "
                + "JOIN empleado e ON e.zona_id = c.zona_id "
                + "WHERE e.empleado_id = ? AND e.tipo = 'VENDEDOR' AND c.activo = TRUE ORDER BY c.nombre",
                vendedorId);
    }

    /** 4. Vendedores que atienden a un cliente (los de la zona del cliente). */
    public List<Map<String, Object>> vendedoresDeCliente(long clienteId) {
        return Consultas.filas(em, "SELECT e.empleado_id AS \"vendedorId\", e.nombre AS \"nombre\", "
                + "z.nombre AS \"zona\" FROM empleado e "
                + "JOIN zona z ON z.zona_id = e.zona_id "
                + "JOIN cliente c ON c.zona_id = e.zona_id "
                + "WHERE c.cliente_id = ? AND e.tipo = 'VENDEDOR' AND e.activo = TRUE ORDER BY e.nombre",
                clienteId);
    }

    /** 5. Vendedores por zona. */
    public List<Map<String, Object>> vendedoresPorZona() {
        return Consultas.filas(em, "SELECT z.zona_id AS \"zonaId\", z.nombre AS \"zona\", "
                + "e.empleado_id AS \"vendedorId\", e.nombre AS \"vendedor\" FROM empleado e "
                + "JOIN zona z ON z.zona_id = e.zona_id "
                + "WHERE e.tipo = 'VENDEDOR' AND e.activo = TRUE ORDER BY z.nombre, e.nombre");
    }

    /** 6. Clientes por zona. */
    public List<Map<String, Object>> clientesPorZona() {
        return Consultas.filas(em, "SELECT z.zona_id AS \"zonaId\", z.nombre AS \"zona\", "
                + "c.cliente_id AS \"clienteId\", c.nombre AS \"cliente\" FROM cliente c "
                + "JOIN zona z ON z.zona_id = c.zona_id "
                + "WHERE c.activo = TRUE ORDER BY z.nombre, c.nombre");
    }
}
