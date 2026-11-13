package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mx.edu.scvi.ventas.model.EstadoFactura;
import mx.edu.scvi.ventas.model.Factura;
import mx.edu.scvi.ventas.model.FacturaDetalle;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaDao {

    @PersistenceContext
    private EntityManager em;

    public Factura emitirFactura(Factura f) {
        em.persist(f);
        em.flush();
        return f;
    }

    public void insertarDetalle(FacturaDetalle d) {
        em.persist(d);
        em.flush();
    }

    public Optional<Factura> buscarPorId(long facturaId) {
        return Optional.ofNullable(em.find(Factura.class, facturaId));
    }

    public List<FacturaDetalle> detalles(long facturaId) {
        return em.createQuery("SELECT d FROM FacturaDetalle d WHERE d.facturaId = :factura "
                        + "ORDER BY d.facturaDetalleId", FacturaDetalle.class)
                .setParameter("factura", facturaId)
                .getResultList();
    }

    public int actualizarEstado(long facturaId, EstadoFactura estado) {
        Factura factura = em.find(Factura.class, facturaId);
        if (factura == null) {
            return 0;
        }
        factura.setEstado(estado);
        em.flush();
        return 1;
    }

    public Optional<Factura> buscarEmitidaPorVenta(long ventaId) {
        return em.createQuery("SELECT f FROM Factura f WHERE f.ventaId = :venta AND f.estado = :estado",
                        Factura.class)
                .setParameter("venta", ventaId)
                .setParameter("estado", EstadoFactura.EMITIDA)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }

    /** Reporte 12: facturas emitidas durante un periodo. */
    public List<Factura> facturacionPorPeriodo(LocalDate desde, LocalDate hasta) {
        return em.createQuery("SELECT f FROM Factura f WHERE f.estado = :estado AND f.fechaFactura BETWEEN :desde "
                        + "AND :hasta ORDER BY f.fechaFactura, f.facturaId", Factura.class)
                .setParameter("estado", EstadoFactura.EMITIDA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }
}
