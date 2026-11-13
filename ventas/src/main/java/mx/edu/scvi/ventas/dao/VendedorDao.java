package mx.edu.scvi.ventas.dao;

import mx.edu.scvi.ventas.model.TipoEmpleado;
import org.springframework.stereotype.Repository;

@Repository
public class VendedorDao extends EmpleadoDao {

    public VendedorDao() {
        super(TipoEmpleado.VENDEDOR);
    }
}
