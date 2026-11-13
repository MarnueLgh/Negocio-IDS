package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import mx.edu.scvi.ventas.model.Ticket;
import org.springframework.stereotype.Repository;

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
