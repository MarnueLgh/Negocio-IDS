package mx.unam.fca.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import mx.unam.fca.scvi.ventas.model.Ticket;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de la tabla Ticket (comprobante de una venta directa).
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
public class TicketDao {

    @PersistenceContext
    private EntityManager em;

    public Ticket insertar(Ticket t) {
        em.persist(t);
        em.flush();
        return t;
    }

    public Optional<Ticket> porVenta(long ventaId) {
        return em.createQuery("SELECT t FROM Ticket t WHERE t.ventaId = :venta", Ticket.class)
                .setParameter("venta", ventaId)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }
}
