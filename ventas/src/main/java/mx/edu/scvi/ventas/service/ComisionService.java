package mx.edu.scvi.ventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mx.edu.scvi.ventas.common.Dinero;
import mx.edu.scvi.ventas.common.NoEncontradoException;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.ComisionDao;
import mx.edu.scvi.ventas.dao.VendedorCajaDao;
import mx.edu.scvi.ventas.dao.VendedorDao;
import mx.edu.scvi.ventas.model.Comision;
import mx.edu.scvi.ventas.model.Empleado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comisiones de vendedores y cajeros. Todos los tipos de venta generan comisión:
 * la venta directa al completarse (cajero) y la venta por pedido al facturarse (vendedor).
 * Si se cancela la venta o la factura, la comisión se cancela.
 */
@Service
@Transactional
public class ComisionService {

    private final ComisionDao comisionDao;
    private final VendedorDao vendedorDao;
    private final VendedorCajaDao cajeroDao;

    public ComisionService(ComisionDao comisionDao, VendedorDao vendedorDao, VendedorCajaDao cajeroDao) {
        this.comisionDao = comisionDao;
        this.vendedorDao = vendedorDao;
        this.cajeroDao = cajeroDao;
    }

    public Comision generarPorVenta(long ventaId, long empleadoId, BigDecimal importeVenta) {
        return generar(ventaId, null, empleadoId, importeVenta);
    }

    public Comision generarPorFactura(long facturaId, long empleadoId, BigDecimal importeVenta) {
        return generar(null, facturaId, empleadoId, importeVenta);
    }

    public int cancelarPorVenta(long ventaId) {
        return comisionDao.cancelarPorVenta(ventaId);
    }

    public int cancelarPorFactura(long facturaId) {
        return comisionDao.cancelarPorFactura(facturaId);
    }

    public List<Comision> listarPorEmpleado(long empleadoId, LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return comisionDao.listarPorEmpleado(empleadoId, desde, hasta);
    }

    private Comision generar(Long ventaId, Long facturaId, long empleadoId, BigDecimal importeVenta) {
        Empleado empleado = vendedorDao.buscarPorId(empleadoId)
                .or(() -> cajeroDao.buscarPorId(empleadoId))
                .orElseThrow(() -> new NoEncontradoException("Empleado " + empleadoId + " no encontrado"));
        BigDecimal porcentaje = empleado.getPorcentajeComision();
        BigDecimal importe = Dinero.porcentaje(importeVenta, porcentaje);
        return comisionDao.insertar(new Comision(null, LocalDate.now(), importe, importeVenta, porcentaje,
                ventaId, facturaId, empleado.getEmpleadoId(), "GENERADA"));
    }
}
