package mx.unam.fca.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.unam.fca.scvi.ventas.common.Consultas;
import mx.unam.fca.scvi.ventas.model.EstadoVenta;
import mx.unam.fca.scvi.ventas.model.Venta;
import mx.unam.fca.scvi.ventas.model.VentaDetalle;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de las tablas Venta y VentaDetalle, incluida la consulta del reporte 7.
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
public class VentaDao {

    @PersistenceContext
    private EntityManager em;

    public Venta insertarVenta(Venta v) {
        em.persist(v);
        em.flush();
        return v;
    }

    public void insertarDetalle(VentaDetalle d) {
        em.persist(d);
        em.flush();
    }

    public Optional<Venta> getVenta(long ventaId) {
        return Optional.ofNullable(em.find(Venta.class, ventaId));
    }

    public List<VentaDetalle> detalles(long ventaId) {
        return em.createQuery("SELECT d FROM VentaDetalle d WHERE d.ventaId = :venta ORDER BY d.ventaDetalleId",
                        VentaDetalle.class)
                .setParameter("venta", ventaId)
                .getResultList();
    }

    public int actualizarEstado(long ventaId, EstadoVenta estado) {
        Venta venta = em.find(Venta.class, ventaId);
        if (venta == null) {
            return 0;
        }
        venta.setEstado(estado);
        em.flush();
        return 1;
    }

    public List<Venta> listarVentas(LocalDate desde, LocalDate hasta) {
        return em.createQuery("SELECT v FROM Venta v WHERE v.fechaVenta >= :desde AND v.fechaVenta < :hasta "
                        + "ORDER BY v.fechaVenta DESC", Venta.class)
                .setParameter("desde", desde.atStartOfDay())
                .setParameter("hasta", hasta.plusDays(1).atStartOfDay())
                .getResultList();
    }

    /** Reporte 7: ventas directas por caja y cajero en el periodo. */
    public List<Map<String, Object>> ventasPorCaja(LocalDate desde, LocalDate hasta) {
        return Consultas.filas(em, "SELECT c.cajaId AS `cajaId`, e.nombre AS `cajero`, "
                + "COUNT(v.ventaId) AS `ventas`, COALESCE(SUM(v.total), 0) AS `importeTotal` "
                + "FROM Venta v JOIN CorteCaja c ON c.corteCajaId = v.corteCajaId "
                + "JOIN Empleado e ON e.empleadoId = v.cajeroId "
                + "WHERE v.estado = 'COMPLETADA' AND v.fechaVenta >= ? AND v.fechaVenta < ? "
                + "GROUP BY c.cajaId, e.nombre ORDER BY `importeTotal` DESC",
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }
}
