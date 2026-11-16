package mx.edu.scvi.ventas.dao;

import mx.edu.scvi.ventas.model.TipoEmpleado;
import org.springframework.stereotype.Repository;

@Repository
public class VendedorCajaDao extends EmpleadoDao {

    public VendedorCajaDao() {
        super(TipoEmpleado.CAJERO);
    }
}
