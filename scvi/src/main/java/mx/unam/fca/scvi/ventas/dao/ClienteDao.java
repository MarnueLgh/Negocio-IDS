package mx.unam.fca.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import mx.unam.fca.scvi.ventas.model.Cliente;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de la tabla Cliente.
 * La baja es lógica: el cliente queda inactivo y se conserva su historial de ventas.
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
public class ClienteDao {

    @PersistenceContext
    private EntityManager em;

    public Cliente insertarCliente(Cliente c) {
        em.persist(c);
        em.flush();
        return c;
    }

    public boolean modificarCliente(Cliente c) {
        Cliente actual = em.find(Cliente.class, c.getClienteId());
        if (actual == null) {
            return false;
        }
        actual.setNombre(c.getNombre());
        actual.setRfc(c.getRfc());
        actual.setDomicilio(c.getDomicilio());
        actual.setTelefono(c.getTelefono());
        actual.setTelefonoAlterno(c.getTelefonoAlterno());
        actual.setContacto(c.getContacto());
        actual.setLimiteCredito(c.getLimiteCredito());
        actual.setDiasCredito(c.getDiasCredito());
        actual.setActivo(c.isActivo());
        actual.setZonaId(c.getZonaId());
        em.flush();
        return true;
    }

    /** Baja lógica: el historial de ventas del cliente se conserva. */
    public boolean borrarCliente(long clienteId) {
        Cliente actual = em.find(Cliente.class, clienteId);
        if (actual == null) {
            return false;
        }
        actual.setActivo(false);
        em.flush();
        return true;
    }

    public Optional<Cliente> buscarCliente(long clienteId) {
        return Optional.ofNullable(em.find(Cliente.class, clienteId));
    }

    public Optional<Cliente> buscarPorRfc(String rfc) {
        return em.createQuery("SELECT c FROM Cliente c WHERE c.rfc = :rfc", Cliente.class)
                .setParameter("rfc", rfc)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }

    public List<Cliente> listar() {
        return em.createQuery("SELECT c FROM Cliente c ORDER BY c.nombre", Cliente.class).getResultList();
    }

    public List<Cliente> listarPorZona(long zonaId) {
        return em.createQuery("SELECT c FROM Cliente c WHERE c.zonaId = :zona ORDER BY c.nombre", Cliente.class)
                .setParameter("zona", zonaId)
                .getResultList();
    }
}
