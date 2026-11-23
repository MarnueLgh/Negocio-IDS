package mx.unam.fca.scvi.ventas.dao;

import mx.unam.fca.scvi.ventas.model.TipoEmpleado;
import org.springframework.stereotype.Repository;

@Repository
public class VendedorCajaDao extends EmpleadoDao {

    public VendedorCajaDao() {
        super(TipoEmpleado.CAJERO);
    }
}
