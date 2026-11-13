package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.model.Comision;
import org.springframework.stereotype.Repository;

@Repository
public class ComisionDao {

    @PersistenceContext
    private EntityManager em;

    public Comision insertar(Comision c) {
        em.persist(c);
        em.flush();
        return c;
    }

    public int cancelarPorVenta(long ventaId) {
        List<Comision> comisiones = em.createQuery(
                        "SELECT c FROM Comision c WHERE c.ventaId = :venta AND c.estado = 'GENERADA'", Comision.class)
                .setParameter("venta", ventaId)
                .getResultList();
        comisiones.forEach(c -> c.setEstado("CANCELADA"));
        em.flush();
        return comisiones.size();
    }

    public int cancelarPorFactura(long facturaId) {
        List<Comision> comisiones = em.createQuery(
                        "SELECT c FROM Comision c WHERE c.facturaId = :factura AND c.estado = 'GENERADA'",
                        Comision.class)
                .setParameter("factura", facturaId)
                .getResultList();
        comisiones.forEach(c -> c.setEstado("CANCELADA"));
        em.flush();
        return comisiones.size();
    }

    public List<Comision> listarPorEmpleado(long empleadoId, LocalDate desde, LocalDate hasta) {
        return em.createQuery("SELECT c FROM Comision c WHERE c.empleadoId = :empleado AND c.estado = 'GENERADA' "
                        + "AND c.fechaGeneracion BETWEEN :desde AND :hasta "
                        + "ORDER BY c.fechaGeneracion, c.comisionId", Comision.class)
                .setParameter("empleado", empleadoId)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }
}
