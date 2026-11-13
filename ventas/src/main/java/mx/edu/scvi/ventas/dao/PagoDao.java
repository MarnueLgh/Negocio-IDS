package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import mx.edu.scvi.ventas.model.Pago;
import org.springframework.stereotype.Repository;

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
