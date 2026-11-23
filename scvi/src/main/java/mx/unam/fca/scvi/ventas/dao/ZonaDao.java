package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.Zona;
import org.springframework.stereotype.Repository;

@Repository
public class ZonaDao {

    @PersistenceContext
    private EntityManager em;

    public Zona insertarZona(Zona zona) {
        em.persist(zona);
        em.flush();
        return zona;
    }

    public boolean modificarZona(Zona zona) {
        Zona actual = em.find(Zona.class, zona.getZonaId());
        if (actual == null) {
            return false;
        }
        actual.setNombre(zona.getNombre());
        actual.setActivo(zona.isActivo());
        em.flush();
        return true;
    }

    public boolean borrarZona(long zonaId) {
        Zona actual = em.find(Zona.class, zonaId);
        if (actual == null) {
            return false;
        }
        em.remove(actual);
        em.flush();
        return true;
    }

    public List<Zona> listarZonas() {
        return em.createQuery("SELECT z FROM Zona z ORDER BY z.nombre", Zona.class).getResultList();
    }

    public Optional<Zona> buscarPorId(long zonaId) {
        return Optional.ofNullable(em.find(Zona.class, zonaId));
    }

    public Optional<Zona> buscarPorNombre(String nombre) {
        return em.createQuery("SELECT z FROM Zona z WHERE LOWER(z.nombre) = LOWER(:nombre)", Zona.class)
                .setParameter("nombre", nombre)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }

    public int contarClientes(long zonaId) {
        return em.createQuery("SELECT COUNT(c) FROM Cliente c WHERE c.zonaId = :zona", Long.class)
                .setParameter("zona", zonaId)
                .getSingleResult().intValue();
    }

    public int contarEmpleados(long zonaId) {
        return em.createQuery("SELECT COUNT(e) FROM Empleado e WHERE e.zonaId = :zona", Long.class)
                .setParameter("zona", zonaId)
                .getSingleResult().intValue();
    }

    public boolean clienteEnZona(long clienteId, long zonaId) {
        Long total = em.createQuery("SELECT COUNT(c) FROM Cliente c WHERE c.clienteId = :cliente AND c.zonaId = :zona",
                        Long.class)
                .setParameter("cliente", clienteId)
                .setParameter("zona", zonaId)
                .getSingleResult();
        return total > 0;
    }

    public int moverClientes(long zonaOrigenId, long zonaDestinoId) {
        List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c WHERE c.zonaId = :zona", Cliente.class)
                .setParameter("zona", zonaOrigenId)
                .getResultList();
        clientes.forEach(c -> c.setZonaId(zonaDestinoId));
        em.flush();
        return clientes.size();
    }

    public int moverVendedores(long zonaOrigenId, long zonaDestinoId) {
        List<Empleado> empleados = em.createQuery("SELECT e FROM Empleado e WHERE e.zonaId = :zona", Empleado.class)
                .setParameter("zona", zonaOrigenId)
                .getResultList();
        empleados.forEach(e -> e.setZonaId(zonaDestinoId));
        em.flush();
        return empleados.size();
    }

    public boolean moverCliente(long clienteId, long zonaDestinoId) {
        Cliente cliente = em.find(Cliente.class, clienteId);
        if (cliente == null) {
            return false;
        }
        cliente.setZonaId(zonaDestinoId);
        em.flush();
        return true;
    }
}
