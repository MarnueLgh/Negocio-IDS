package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mx.edu.scvi.ventas.model.CuentaPorCobrar;
import mx.edu.scvi.ventas.model.EstadoCuenta;
import mx.edu.scvi.ventas.model.PagoCuentaPorCobrar;
import org.springframework.stereotype.Repository;

@Repository
public class CuentaPorCobrarDao {

    private static final List<EstadoCuenta> CON_SALDO = List.of(EstadoCuenta.PENDIENTE, EstadoCuenta.PARCIAL);

    @PersistenceContext
    private EntityManager em;

    public CuentaPorCobrar insertarCuenta(CuentaPorCobrar c) {
        em.persist(c);
        em.flush();
        return c;
    }

    public Optional<CuentaPorCobrar> buscarPorId(long cuentaId) {
        return Optional.ofNullable(em.find(CuentaPorCobrar.class, cuentaId));
    }

    public Optional<CuentaPorCobrar> buscarPorFactura(long facturaId) {
        return em.createQuery("SELECT c FROM CuentaPorCobrar c WHERE c.facturaId = :factura", CuentaPorCobrar.class)
                .setParameter("factura", facturaId)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }

    public boolean actualizarSaldo(long cuentaId, BigDecimal importePendiente, EstadoCuenta estado) {
        CuentaPorCobrar cuenta = em.find(CuentaPorCobrar.class, cuentaId);
        if (cuenta == null) {
            return false;
        }
        cuenta.setImportePendiente(importePendiente);
        cuenta.setEstado(estado);
        em.flush();
        return true;
    }

    public int cancelarPorFactura(long facturaId) {
        Optional<CuentaPorCobrar> cuenta = buscarPorFactura(facturaId);
        cuenta.ifPresent(c -> c.setEstado(EstadoCuenta.CANCELADA));
        em.flush();
        return cuenta.isPresent() ? 1 : 0;
    }

    public PagoCuentaPorCobrar insertarPago(PagoCuentaPorCobrar p) {
        em.persist(p);
        em.flush();
        return p;
    }

    /** Saldo que el cliente debe hoy (cuentas pendientes o con abonos parciales). */
    public BigDecimal saldoPendienteCliente(long clienteId) {
        return em.createQuery("SELECT c FROM CuentaPorCobrar c WHERE c.clienteId = :cliente AND c.estado IN :estados",
                        CuentaPorCobrar.class)
                .setParameter("cliente", clienteId)
                .setParameter("estados", CON_SALDO)
                .getResultStream()
                .map(CuentaPorCobrar::getImportePendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Reporte 18: cuentas por cobrar de un cliente. */
    public List<CuentaPorCobrar> buscarCuentasCliente(long clienteId) {
        return em.createQuery("SELECT c FROM CuentaPorCobrar c WHERE c.clienteId = :cliente "
                        + "ORDER BY c.fechaLimite, c.cuentaId", CuentaPorCobrar.class)
                .setParameter("cliente", clienteId)
                .getResultList();
    }

    /** Reporte 19: cuentas por cobrar generadas en un periodo. */
    public List<CuentaPorCobrar> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
        return em.createQuery("SELECT c FROM CuentaPorCobrar c WHERE c.fechaCuenta BETWEEN :desde AND :hasta "
                        + "ORDER BY c.fechaCuenta, c.cuentaId", CuentaPorCobrar.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }

    /** Reporte 20: cuentas que ya debieron pagarse y siguen con saldo. */
    public List<CuentaPorCobrar> buscarCuentasVencidas(LocalDate hoy) {
        return em.createQuery("SELECT c FROM CuentaPorCobrar c WHERE c.fechaLimite < :hoy "
                        + "AND c.importePendiente > :cero AND c.estado IN :estados "
                        + "ORDER BY c.fechaLimite, c.cuentaId", CuentaPorCobrar.class)
                .setParameter("hoy", hoy)
                .setParameter("cero", BigDecimal.ZERO)
                .setParameter("estados", CON_SALDO)
                .getResultList();
    }
}
