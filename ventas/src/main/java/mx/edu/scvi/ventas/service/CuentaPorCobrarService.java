package mx.edu.scvi.ventas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import mx.edu.scvi.ventas.common.Dinero;
import mx.edu.scvi.ventas.common.Reglas;
import mx.edu.scvi.ventas.dao.CuentaPorCobrarDao;
import mx.edu.scvi.ventas.model.CuentaPorCobrar;
import mx.edu.scvi.ventas.model.EstadoCuenta;
import mx.edu.scvi.ventas.model.FormaPago;
import mx.edu.scvi.ventas.model.PagoCuentaPorCobrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Seguimiento de cuentas por cobrar: abonos antes o después de la fecha límite y consulta de vencidas. */
@Service
@Transactional
public class CuentaPorCobrarService {

    public record ResultadoPago(CuentaPorCobrar cuenta, PagoCuentaPorCobrar pago, String ticket) {
    }

    public record CuentaVencida(CuentaPorCobrar cuenta, long diasRetraso) {
    }

    private final CuentaPorCobrarDao cuentaDao;

    public CuentaPorCobrarService(CuentaPorCobrarDao cuentaDao) {
        this.cuentaDao = cuentaDao;
    }

    public ResultadoPago registrarPago(long cuentaId, BigDecimal importe, FormaPago formaPago) {
        CuentaPorCobrar cuenta = obtener(cuentaId);
        Reglas.exigir(cuenta.getEstado() == EstadoCuenta.PENDIENTE || cuenta.getEstado() == EstadoCuenta.PARCIAL,
                "La cuenta no admite pagos (estado " + cuenta.getEstado() + ")");
        Reglas.exigir(formaPago != null, "La forma de pago es obligatoria");
        Reglas.exigir(importe != null && importe.signum() > 0, "El importe del pago debe ser mayor a cero");
        BigDecimal abono = Dinero.redondear(importe);
        Reglas.exigir(abono.compareTo(cuenta.getImportePendiente()) <= 0,
                "El pago excede el saldo pendiente (" + cuenta.getImportePendiente() + ")");

        BigDecimal restante = cuenta.getImportePendiente().subtract(abono);
        EstadoCuenta estado = restante.signum() == 0 ? EstadoCuenta.LIQUIDADA : EstadoCuenta.PARCIAL;
        cuentaDao.actualizarSaldo(cuentaId, restante, estado);
        PagoCuentaPorCobrar pago = cuentaDao.insertarPago(
                new PagoCuentaPorCobrar(null, cuentaId, LocalDate.now(), abono, formaPago));
        CuentaPorCobrar actualizada = new CuentaPorCobrar(cuenta.getCuentaId(), cuenta.getFechaCuenta(),
                cuenta.getFechaFacturacion(), cuenta.getFechaLimite(), estado, cuenta.getImporteOriginal(), restante,
                cuenta.getFacturaId(), cuenta.getClienteId());
        return new ResultadoPago(actualizada, pago, construirTicket(actualizada, pago));
    }

    public static String construirTicket(CuentaPorCobrar cuenta, PagoCuentaPorCobrar pago) {
        StringBuilder sb = new StringBuilder();
        sb.append("PAGO DE CUENTA POR COBRAR\n");
        sb.append("Cuenta: ").append(cuenta.getCuentaId()).append("  Factura: ").append(cuenta.getFacturaId()).append('\n');
        sb.append("Fecha: ").append(pago.getFechaPago()).append('\n');
        sb.append("Forma de pago: ").append(pago.getFormaPago()).append('\n');
        sb.append(String.format(Locale.US, "Abono: $%.2f\n", pago.getImporte()));
        sb.append(String.format(Locale.US, "Restante: $%.2f\n", cuenta.getImportePendiente()));
        return sb.toString();
    }

    public CuentaPorCobrar obtener(long cuentaId) {
        return Reglas.encontrado(cuentaDao.buscarPorId(cuentaId), "Cuenta por cobrar " + cuentaId + " no encontrada");
    }

    /** Reporte 18. */
    public List<CuentaPorCobrar> porCliente(long clienteId) {
        return cuentaDao.buscarCuentasCliente(clienteId);
    }

    /** Reporte 19. */
    public List<CuentaPorCobrar> porPeriodo(LocalDate desde, LocalDate hasta) {
        Reglas.periodo(desde, hasta);
        return cuentaDao.listarPorPeriodo(desde, hasta);
    }

    /** Reporte 20: cuentas que ya debieron pagarse, con sus días de retraso. */
    public List<CuentaVencida> vencidas() {
        LocalDate hoy = LocalDate.now();
        return cuentaDao.buscarCuentasVencidas(hoy).stream()
                .map(c -> new CuentaVencida(c, c.calcularDiasRetraso(hoy)))
                .toList();
    }
}
