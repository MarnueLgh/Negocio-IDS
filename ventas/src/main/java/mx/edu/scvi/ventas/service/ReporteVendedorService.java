package mx.edu.scvi.ventas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.ReporteVendedorDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReporteVendedorService {

    private final ReporteVendedorDao reporteDao;

    public ReporteVendedorService(ReporteVendedorDao reporteDao) {
        this.reporteDao = reporteDao;
    }

    public List<Map<String, Object>> comisionesPorEmpleado(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return reporteDao.comisionesPorEmpleado(desde, hasta);
    }

    public List<Map<String, Object>> ventasPorVendedor(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return reporteDao.ventasPorVendedor(desde, hasta);
    }

    public List<Map<String, Object>> clientesDeVendedor(long vendedorId) {
        return reporteDao.clientesDeVendedor(vendedorId);
    }

    public List<Map<String, Object>> vendedoresDeCliente(long clienteId) {
        return reporteDao.vendedoresDeCliente(clienteId);
    }

    public List<Map<String, Object>> vendedoresPorZona(Long zonaId) {
        return filtrarZona(reporteDao.vendedoresPorZona(), zonaId);
    }

    public List<Map<String, Object>> clientesPorZona(Long zonaId) {
        return filtrarZona(reporteDao.clientesPorZona(), zonaId);
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
