package mx.edu.scvi.ventas.service;

import java.util.List;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.ZonaDao;
import mx.edu.scvi.ventas.model.Zona;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ZonaService {

    public record MovimientoZona(long zonaDestinoId, int clientesMovidos, int vendedoresMovidos) {
    }

    private final ZonaDao zonaDao;

    public ZonaService(ZonaDao zonaDao) {
        this.zonaDao = zonaDao;
    }

    public Zona crear(String nombre) {
        String limpio = Reglas.texto(nombre, "nombre");
        Reglas.exigir(zonaDao.buscarPorNombre(limpio).isEmpty(), "Ya existe una zona con ese nombre");
        return zonaDao.insertarZona(new Zona(null, limpio, true));
    }

    public Zona modificar(long zonaId, String nombre, boolean activo) {
        obtener(zonaId);
        String limpio = Reglas.texto(nombre, "nombre");
        zonaDao.buscarPorNombre(limpio).ifPresent(otra ->
                Reglas.exigir(otra.getZonaId().equals(zonaId), "Ya existe una zona con ese nombre"));
        Reglas.exigir(activo || estaVacia(zonaId),
                "No se puede desactivar una zona con clientes o vendedores; únala a otra zona primero");
        Zona zona = new Zona(zonaId, limpio, activo);
        zonaDao.modificarZona(zona);
        return zona;
    }

    public void eliminar(long zonaId) {
        obtener(zonaId);
        Reglas.exigir(estaVacia(zonaId),
                "No se puede eliminar una zona con clientes o vendedores; únala a otra zona o desactívela");
        zonaDao.borrarZona(zonaId);
    }

    public Zona obtener(long zonaId) {
        return Reglas.encontrado(zonaDao.buscarPorId(zonaId), "Zona " + zonaId + " no encontrada");
    }

    public List<Zona> listar() {
        return zonaDao.listarZonas();
    }

    /** Une dos zonas (p. ej. por baja de clientes): todo pasa a la zona destino y la origen se desactiva. */
    public MovimientoZona unir(long zonaOrigenId, long zonaDestinoId) {
        Reglas.exigir(zonaOrigenId != zonaDestinoId, "La zona origen y la destino deben ser distintas");
        Zona origen = obtener(zonaOrigenId);
        Zona destino = obtener(zonaDestinoId);
        Reglas.exigir(destino.isActivo(), "La zona destino está inactiva");
        int clientes = zonaDao.moverClientes(zonaOrigenId, zonaDestinoId);
        int vendedores = zonaDao.moverVendedores(zonaOrigenId, zonaDestinoId);
        zonaDao.modificarZona(new Zona(origen.getZonaId(), origen.getNombre(), false));
        return new MovimientoZona(zonaDestinoId, clientes, vendedores);
    }

    /** Divide una zona con muchos clientes: crea una nueva y le asigna los clientes indicados. */
    public Zona dividir(long zonaId, String nuevoNombre, List<Long> clienteIds) {
        Zona origen = obtener(zonaId);
        Reglas.exigir(origen.isActivo(), "La zona a dividir está inactiva");
        Reglas.exigir(clienteIds != null && !clienteIds.isEmpty(), "Indique al menos un cliente para la nueva zona");
        for (Long clienteId : clienteIds) {
            Reglas.exigir(zonaDao.clienteEnZona(clienteId, zonaId),
                    "El cliente " + clienteId + " no pertenece a la zona " + zonaId);
        }
        Zona nueva = crear(nuevoNombre);
        for (Long clienteId : clienteIds) {
            zonaDao.moverCliente(clienteId, nueva.getZonaId());
        }
        return nueva;
    }

    private boolean estaVacia(long zonaId) {
        return zonaDao.contarClientes(zonaId) == 0 && zonaDao.contarEmpleados(zonaId) == 0;
    }
}
