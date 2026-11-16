package mx.edu.scvi.ventas.service;

import java.math.BigDecimal;
import java.util.List;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.EmpleadoDao;
import mx.edu.scvi.ventas.dao.VendedorCajaDao;
import mx.edu.scvi.ventas.dao.VendedorDao;
import mx.edu.scvi.ventas.dao.ZonaDao;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.TipoEmpleado;
import mx.edu.scvi.ventas.model.Zona;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD de vendedores (con zona) y cajeros (sin zona). */
@Service
@Transactional
public class EmpleadoService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final VendedorDao vendedorDao;
    private final VendedorCajaDao cajeroDao;
    private final ZonaDao zonaDao;

    public EmpleadoService(VendedorDao vendedorDao, VendedorCajaDao cajeroDao, ZonaDao zonaDao) {
        this.vendedorDao = vendedorDao;
        this.cajeroDao = cajeroDao;
        this.zonaDao = zonaDao;
    }

    public Empleado registrar(TipoEmpleado tipo, Empleado datos) {
        BigDecimal porcentaje = datos.getPorcentajeComision();
        Reglas.exigir(porcentaje != null && porcentaje.signum() >= 0 && porcentaje.compareTo(CIEN) <= 0,
                "El porcentaje de comisión debe estar entre 0 y 100");
        Empleado nuevo = new Empleado(null,
                Reglas.texto(datos.getNombre(), "nombre"),
                Reglas.texto(datos.getTelefono(), "telefono"),
                Reglas.texto(datos.getDomicilio(), "domicilio"),
                tipo, porcentaje, true, validarZona(tipo, datos.getZonaId()));
        return dao(tipo).insertar(nuevo);
    }

    /** La comisión no cambia al modificar: se definió al ingresar. */
    public Empleado modificar(TipoEmpleado tipo, long empleadoId, Empleado datos, Boolean activo) {
        Empleado actual = obtener(tipo, empleadoId);
        Long zonaId = tipo == TipoEmpleado.VENDEDOR && datos.getZonaId() != null ? datos.getZonaId() : actual.getZonaId();
        Empleado nuevo = new Empleado(empleadoId,
                Reglas.texto(datos.getNombre(), "nombre"),
                Reglas.texto(datos.getTelefono(), "telefono"),
                Reglas.texto(datos.getDomicilio(), "domicilio"),
                tipo, actual.getPorcentajeComision(),
                activo == null ? actual.isActivo() : activo,
                validarZona(tipo, zonaId));
        dao(tipo).modificar(nuevo);
        return nuevo;
    }

    public void baja(TipoEmpleado tipo, long empleadoId) {
        obtener(tipo, empleadoId);
        dao(tipo).borrar(empleadoId);
    }

    public Empleado obtener(TipoEmpleado tipo, long empleadoId) {
        return Reglas.encontrado(dao(tipo).buscarPorId(empleadoId),
                tipo.name().toLowerCase() + " " + empleadoId + " no encontrado");
    }

    public List<Empleado> listar(TipoEmpleado tipo) {
        return dao(tipo).listar();
    }

    private EmpleadoDao dao(TipoEmpleado tipo) {
        return tipo == TipoEmpleado.VENDEDOR ? vendedorDao : cajeroDao;
    }

    /** Un vendedor pertenece a una sola zona activa; el cajero no tiene zona. */
    private Long validarZona(TipoEmpleado tipo, Long zonaId) {
        if (tipo == TipoEmpleado.CAJERO) {
            return null;
        }
        Reglas.exigir(zonaId != null, "Un vendedor debe pertenecer a una zona");
        Zona zona = Reglas.encontrado(zonaDao.buscarPorId(zonaId), "Zona " + zonaId + " no encontrada");
        Reglas.exigir(zona.isActivo(), "La zona " + zona.getNombre() + " está inactiva");
        return zonaId;
    }
}
