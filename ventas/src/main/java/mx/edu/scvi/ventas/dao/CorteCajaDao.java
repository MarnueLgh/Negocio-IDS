package mx.edu.scvi.ventas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mx.edu.scvi.ventas.common.Consultas;
import mx.edu.scvi.ventas.model.CorteCaja;
import mx.edu.scvi.ventas.model.EstadoCorte;
import org.springframework.stereotype.Repository;

@Repository
public class CorteCajaDao {

    /** Resumen de las ventas completadas de un corte: cuántas, cuánto y cuánto fue en efectivo. */
    public record ResumenCorte(int ventas, BigDecimal totalVentas, BigDecimal efectivo) {
    }

    @PersistenceContext
    private EntityManager em;

    public CorteCaja insertar(CorteCaja c) {
        em.persist(c);
        em.flush();
        return c;
    }

    public Optional<CorteCaja> buscarPorId(long corteId) {
        return Optional.ofNullable(em.find(CorteCaja.class, corteId));
    }

    public Optional<CorteCaja> buscarAbiertoDeCajero(long cajeroId) {
        return em.createQuery("SELECT c FROM CorteCaja c WHERE c.cajeroId = :cajero AND c.estado = :estado",
                        CorteCaja.class)
                .setParameter("cajero", cajeroId)
                .setParameter("estado", EstadoCorte.ABIERTA)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }

    public Optional<CorteCaja> buscarAbiertoDeCaja(int cajaId) {
        return em.createQuery("SELECT c FROM CorteCaja c WHERE c.cajaId = :caja AND c.estado = :estado",
                        CorteCaja.class)
                .setParameter("caja", cajaId)
                .setParameter("estado", EstadoCorte.ABIERTA)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }

    public boolean registrarCierre(CorteCaja c) {
        CorteCaja actual = em.find(CorteCaja.class, c.getCorteCajaId());
        if (actual == null) {
            return false;
        }
        actual.setHoraCorte(c.getHoraCorte());
        actual.setTotalVentas(c.getTotalVentas());
        actual.setImporteEsperado(c.getImporteEsperado());
        actual.setImporteFisico(c.getImporteFisico());
        actual.setDiferencia(c.getDiferencia());
        actual.setHayDiferencia(c.isHayDiferencia());
        actual.setEstado(c.getEstado());
        em.flush();
        return true;
    }

    public ResumenCorte resumenVentas(long corteId) {
        Object[] fila = (Object[]) em.createNativeQuery("SELECT COUNT(*), COALESCE(SUM(v.total), 0), "
                        + "COALESCE(SUM(CASE WHEN p.forma_pago = 'EFECTIVO' THEN p.importe ELSE 0 END), 0) "
                        + "FROM venta v LEFT JOIN pago p ON p.venta_id = v.venta_id "
                        + "WHERE v.corte_caja_id = ?1 AND v.estado = 'COMPLETADA'")
                .setParameter(1, corteId)
                .getSingleResult();
        return new ResumenCorte(((Number) fila[0]).intValue(), Consultas.decimal(fila[1]), Consultas.decimal(fila[2]));
    }

    /** Reporte 8: cortes cerrados del periodo; opcionalmente solo los que tuvieron diferencia. */
    public List<CorteCaja> listarCortes(LocalDate desde, LocalDate hasta, boolean soloConDiferencia) {
        String jpql = "SELECT c FROM CorteCaja c WHERE c.estado = :estado AND c.horaCorte >= :desde "
                + "AND c.horaCorte < :hasta" + (soloConDiferencia ? " AND c.hayDiferencia = TRUE" : "")
                + " ORDER BY c.horaCorte DESC";
        return em.createQuery(jpql, CorteCaja.class)
                .setParameter("estado", EstadoCorte.CERRADA)
                .setParameter("desde", desde.atStartOfDay())
                .setParameter("hasta", hasta.plusDays(1).atStartOfDay())
                .getResultList();
    }
}
