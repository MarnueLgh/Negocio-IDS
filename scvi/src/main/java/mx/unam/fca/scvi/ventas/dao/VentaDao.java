package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.edu.scvi.ventas.common.Consultas;
import mx.edu.scvi.ventas.model.EstadoVenta;
import mx.edu.scvi.ventas.model.Venta;
import mx.edu.scvi.ventas.model.VentaDetalle;
import org.springframework.stereotype.Repository;

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
        return Consultas.filas(em, "SELECT c.caja_id AS \"cajaId\", e.nombre AS \"cajero\", "
                + "COUNT(v.venta_id) AS \"ventas\", COALESCE(SUM(v.total), 0) AS \"importeTotal\" "
                + "FROM venta v JOIN corte_caja c ON c.corte_caja_id = v.corte_caja_id "
                + "JOIN empleado e ON e.empleado_id = v.cajero_id "
                + "WHERE v.estado = 'COMPLETADA' AND v.fecha_venta >= ? AND v.fecha_venta < ? "
                + "GROUP BY c.caja_id, e.nombre ORDER BY \"importeTotal\" DESC",
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }
}
