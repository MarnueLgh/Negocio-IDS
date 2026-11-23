package mx.edu.scvi.ventas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.ClienteDao;
import mx.edu.scvi.ventas.dao.ZonaDao;
import mx.edu.scvi.ventas.model.Cliente;
import mx.edu.scvi.ventas.model.Zona;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteService {

    private static final Pattern RFC = Pattern.compile("^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$");

    private final ClienteDao clienteDao;
    private final ZonaDao zonaDao;

    public ClienteService(ClienteDao clienteDao, ZonaDao zonaDao) {
        this.clienteDao = clienteDao;
        this.zonaDao = zonaDao;
    }

    public Cliente registrar(Cliente datos) {
        return clienteDao.insertarCliente(normalizar(null, datos, true));
    }

    public Cliente modificar(long clienteId, Cliente datos) {
        Cliente actual = obtener(clienteId);
        Cliente nuevo = normalizar(clienteId, datos, actual.isActivo());
        clienteDao.modificarCliente(nuevo);
        return nuevo;
    }

    public void baja(long clienteId) {
        obtener(clienteId);
        clienteDao.borrarCliente(clienteId);
    }

    public Cliente obtener(long clienteId) {
        return Reglas.encontrado(clienteDao.buscarCliente(clienteId), "Cliente " + clienteId + " no encontrado");
    }

    public List<Cliente> listar() {
        return clienteDao.listar();
    }

    public List<Cliente> listarPorZona(long zonaId) {
        return clienteDao.listarPorZona(zonaId);
    }

    private Cliente normalizar(Long clienteId, Cliente d, boolean activo) {
        String rfc = Reglas.texto(d.getRfc(), "rfc").toUpperCase();
        Reglas.exigir(RFC.matcher(rfc).matches(), "El RFC no tiene un formato válido");
        clienteDao.buscarPorRfc(rfc).ifPresent(otro ->
                Reglas.exigir(clienteId != null && otro.getClienteId().equals(clienteId),
                        "Ya existe un cliente con ese RFC"));
        BigDecimal limite = d.getLimiteCredito() == null ? BigDecimal.ZERO : d.getLimiteCredito();
        Reglas.exigir(limite.signum() >= 0, "El límite de crédito no puede ser negativo");
        Reglas.exigir(d.getDiasCredito() >= 0, "Los días de crédito no pueden ser negativos");
        Reglas.exigir(d.getZonaId() != null, "El cliente debe pertenecer a una zona");
        Zona zona = Reglas.encontrado(zonaDao.buscarPorId(d.getZonaId()), "Zona " + d.getZonaId() + " no encontrada");
        Reglas.exigir(zona.isActivo(), "La zona " + zona.getNombre() + " está inactiva");
        return new Cliente(clienteId,
                Reglas.texto(d.getNombre(), "nombre"), rfc,
                Reglas.texto(d.getDomicilio(), "domicilio"),
                Reglas.texto(d.getTelefono(), "telefono"),
                d.getTelefonoAlterno(), d.getContacto(), limite, d.getDiasCredito(), activo, d.getZonaId());
    }
}
