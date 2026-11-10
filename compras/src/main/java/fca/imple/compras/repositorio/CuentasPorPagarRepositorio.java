package fca.imple.compras.repositorio;

import fca.imple.compras.modelo.CuentaPorPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 - Interfaz de repositorio de acceso a datos para la entidad Cuenta por Pagar.
 - Propósito: Gestionar las operaciones de persistencia, consulta y filtrado avanzado de las cuentas por pagar en la base de datos.
 - Responsabilidad principal: Proveer métodos de acceso a datos basados en Spring Data JPA para administrar los adeudos, estados y vencimientos de las cuentas con los proveedores.
 - Subsistema: Compras
 - Relación con otros componentes: Se relaciona directamente con la entidad CuentaPorPagar y es utilizada e inyectada dentro de la clase CuentasPorPagarServicio.
 - Reglas de negocio: Facilita la consulta de cuentas pendientes mediante rangos de fechas límite, filtrado por proveedor y validación de estados operativos (pendiente, cancelada).
 - @author Becerra Cruz Lizeth Adriana Equipo 1
 - Fecha creación: 08-11-2026
 - Fecha modificación: 09-11-2026
 */


@Repository
public interface CuentasPorPagarRepositorio extends JpaRepository<CuentaPorPagar, Integer> {

    List<CuentaPorPagar> findByIdProveedor(Integer idProveedor);

    List<CuentaPorPagar> findByFechaLimiteBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<CuentaPorPagar> findByIdProveedorAndEstadoAndFechaLimiteBetween(
            Integer idProveedor, String estado, LocalDate fechaInicio, LocalDate fechaFin
    );

    List<CuentaPorPagar> findByEstado(String estado);

}