package mx.unam.fca.scvi.ventas.dao;

import mx.unam.fca.scvi.ventas.model.TipoEmpleado;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de los empleados de tipo CAJERO en la tabla Empleado.
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
public class VendedorCajaDao extends EmpleadoDao {

    public VendedorCajaDao() {
        super(TipoEmpleado.CAJERO);
    }
}
