package mx.unam.fca.scvi.ventas.dao;

import mx.unam.fca.scvi.ventas.model.TipoEmpleado;
import org.springframework.stereotype.Repository;

@Repository
public class VendedorDao extends EmpleadoDao {

    public VendedorDao() {
        super(TipoEmpleado.VENDEDOR);
    }
}
