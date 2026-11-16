package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.TipoEmpleado;

/** Base de VendedorDao y VendedorCajaDao: misma tabla 'empleado', filtrada por tipo. */
public abstract class EmpleadoDao {

    @PersistenceContext
    private EntityManager em;

    private final TipoEmpleado tipo;

    protected EmpleadoDao(TipoEmpleado tipo) {
        this.tipo = tipo;
    }

    public Empleado insertar(Empleado e) {
        e.setTipo(tipo);
        em.persist(e);
        em.flush();
        return e;
    }

    /** El porcentaje de comisión no se modifica: se define al ingresar el empleado. */
    public boolean modificar(Empleado e) {
        Optional<Empleado> actual = buscarPorId(e.getEmpleadoId());
        if (actual.isEmpty()) {
            return false;
        }
        Empleado empleado = actual.get();
        empleado.setNombre(e.getNombre());
        empleado.setTelefono(e.getTelefono());
        empleado.setDomicilio(e.getDomicilio());
        empleado.setActivo(e.isActivo());
        empleado.setZonaId(e.getZonaId());
        em.flush();
        return true;
    }

    /** Baja lógica. */
    public boolean borrar(long empleadoId) {
        Optional<Empleado> actual = buscarPorId(empleadoId);
        if (actual.isEmpty()) {
            return false;
        }
        actual.get().setActivo(false);
        em.flush();
        return true;
    }

    public Optional<Empleado> buscarPorId(long empleadoId) {
        return Optional.ofNullable(em.find(Empleado.class, empleadoId))
                .filter(empleado -> empleado.getTipo() == tipo);
    }

    public List<Empleado> listar() {
        return em.createQuery("SELECT e FROM Empleado e WHERE e.tipo = :tipo ORDER BY e.nombre", Empleado.class)
                .setParameter("tipo", tipo)
                .getResultList();
    }
}
