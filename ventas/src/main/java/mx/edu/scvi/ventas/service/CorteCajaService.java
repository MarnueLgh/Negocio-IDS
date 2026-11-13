package mx.edu.scvi.ventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import mx.edu.scvi.ventas.common.ReglaNegocioException;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.CorteCajaDao;
import mx.edu.scvi.ventas.dao.VentaDao;
import mx.edu.scvi.ventas.model.CorteCaja;
import mx.edu.scvi.ventas.model.Empleado;
import mx.edu.scvi.ventas.model.EstadoCorte;
import mx.edu.scvi.ventas.model.TipoEmpleado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inicio y corte de caja. Al inicio del día se entrega un importe al cajero; al corte debe coincidir
 * importe inicial + ventas cobradas en efectivo con el efectivo físico. Si no, queda registrada la diferencia.
 */
@Service
@Transactional
public class CorteCajaService {

    private final CorteCajaDao corteDao;
    private final VentaDao ventaDao;
    private final EmpleadoService empleadoService;

    public CorteCajaService(CorteCajaDao corteDao, VentaDao ventaDao, EmpleadoService empleadoService) {
        this.corteDao = corteDao;
        this.ventaDao = ventaDao;
        this.empleadoService = empleadoService;
    }

    public CorteCaja abrir(Long cajeroId, int cajaId, BigDecimal importeInicial) {
        Reglas.exigir(cajeroId != null, "El cajero es obligatorio");
        Reglas.exigir(importeInicial != null && importeInicial.signum() >= 0,
                "El importe inicial de caja no puede ser negativo");
        Empleado cajero = empleadoService.obtener(TipoEmpleado.CAJERO, cajeroId);
        Reglas.exigir(cajero.isActivo(), "El cajero está dado de baja");
        Reglas.exigir(corteDao.buscarAbiertoDeCajero(cajeroId).isEmpty(), "El cajero ya tiene una caja abierta");
        Reglas.exigir(corteDao.buscarAbiertoDeCaja(cajaId).isEmpty(), "La caja " + cajaId + " ya está abierta");
        return corteDao.insertar(new CorteCaja(null, cajaId, cajeroId, LocalDateTime.now(), null, importeInicial,
                0, null, null, null, false, EstadoCorte.ABIERTA));
    }

    public CorteCaja cerrar(long corteId, BigDecimal importeFisico) {
        CorteCaja corte = obtener(corteId);
        Reglas.exigir(corte.getEstado() == EstadoCorte.ABIERTA, "El corte ya fue realizado");
        Reglas.exigir(importeFisico != null && importeFisico.signum() >= 0,
                "El efectivo físico no puede ser negativo");
        CorteCajaDao.ResumenCorte resumen = corteDao.resumenVentas(corteId);
        BigDecimal esperado = corte.getImporteInicial().add(resumen.efectivo());
        BigDecimal diferencia = importeFisico.subtract(esperado);
        CorteCaja cerrado = new CorteCaja(corteId, corte.getCajaId(), corte.getCajeroId(), corte.getFechaApertura(),
                LocalDateTime.now(), corte.getImporteInicial(), resumen.ventas(), esperado, importeFisico, diferencia,
                diferencia.signum() != 0, EstadoCorte.CERRADA);
        corteDao.registrarCierre(cerrado);
        return cerrado;
    }

    public CorteCaja obtener(long corteId) {
        return Reglas.encontrado(corteDao.buscarPorId(corteId), "Corte de caja " + corteId + " no encontrado");
    }

    /** Reporte 8: diferencias al corte de caja en un periodo. */
    public List<CorteCaja> cortes(LocalDate desde, LocalDate hasta, boolean soloConDiferencia) {
        Reglas.periodo(desde, hasta);
        return corteDao.listarCortes(desde, hasta, soloConDiferencia);
    }

    /** Reporte 7: caja de ventas directas por periodo. */
    public List<Map<String, Object>> ventasPorCaja(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return ventaDao.ventasPorCaja(desde, hasta);
    }
}
