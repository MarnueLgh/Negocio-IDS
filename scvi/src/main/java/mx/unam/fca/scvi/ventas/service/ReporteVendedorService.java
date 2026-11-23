package mx.unam.fca.scvi.ventas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.unam.fca.scvi.ventas.common.Reglas;
import mx.unam.fca.scvi.ventas.dao.ClienteDao;
import mx.unam.fca.scvi.ventas.dao.ReporteVendedorDao;
import mx.unam.fca.scvi.ventas.dao.VendedorDao;
import mx.unam.fca.scvi.ventas.dao.ZonaDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reportes 1 a 6: comisiones, ventas por vendedor y relación entre vendedores, clientes y zonas.
 * Avisa cuando no hay datos o cuando el Id buscado no existe.
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
@Service
@Transactional
public class ReporteVendedorService {

    private final ReporteVendedorDao reporteDao;
    private final VendedorDao vendedorDao;
    private final ClienteDao clienteDao;
    private final ZonaDao zonaDao;

    public ReporteVendedorService(ReporteVendedorDao reporteDao, VendedorDao vendedorDao, ClienteDao clienteDao,
                                  ZonaDao zonaDao) {
        this.reporteDao = reporteDao;
        this.vendedorDao = vendedorDao;
        this.clienteDao = clienteDao;
        this.zonaDao = zonaDao;
    }

    public List<Map<String, Object>> comisionesPorEmpleado(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return Reglas.conDatos(reporteDao.comisionesPorEmpleado(desde, hasta));
    }

    public List<Map<String, Object>> ventasPorVendedor(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return Reglas.conDatos(reporteDao.ventasPorVendedor(desde, hasta));
    }

    public List<Map<String, Object>> clientesDeVendedor(long vendedorId) {
        Reglas.existe(vendedorDao.buscarPorId(vendedorId));
        return Reglas.conDatos(reporteDao.clientesDeVendedor(vendedorId));
    }

    public List<Map<String, Object>> vendedoresDeCliente(long clienteId) {
        Reglas.existe(clienteDao.buscarCliente(clienteId));
        return Reglas.conDatos(reporteDao.vendedoresDeCliente(clienteId));
    }

    public List<Map<String, Object>> vendedoresPorZona(Long zonaId) {
        validarZona(zonaId);
        return Reglas.conDatos(filtrarZona(reporteDao.vendedoresPorZona(), zonaId));
    }

    public List<Map<String, Object>> clientesPorZona(Long zonaId) {
        validarZona(zonaId);
        return Reglas.conDatos(filtrarZona(reporteDao.clientesPorZona(), zonaId));
    }

    private void validarZona(Long zonaId) {
        if (zonaId != null) {
            Reglas.existe(zonaDao.buscarPorId(zonaId));
        }
    }

    private List<Map<String, Object>> filtrarZona(List<Map<String, Object>> filas, Long zonaId) {
        if (zonaId == null) {
            return filas;
        }
        return filas.stream()
                .filter(fila -> zonaId.equals(((Number) fila.get("zonaId")).longValue()))
                .toList();
    }
}
