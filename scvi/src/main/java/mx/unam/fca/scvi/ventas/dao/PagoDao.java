package mx.unam.fca.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import mx.unam.fca.scvi.ventas.model.Pago;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de la tabla Pago (pago de una venta directa).
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
public class PagoDao {

    @PersistenceContext
    private EntityManager em;

    public Pago insertarPago(Pago p) {
        em.persist(p);
        em.flush();
        return p;
    }

    public Optional<Pago> getPagoPorVenta(long ventaId) {
        return em.createQuery("SELECT p FROM Pago p WHERE p.ventaId = :venta", Pago.class)
                .setParameter("venta", ventaId)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }
}
