package fca.imple.compras.repositorio;

import fca.imple.compras.modelo.PagoDetalle;
import fca.imple.compras.modelo.PagoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de repositorio de acceso a datos para la entidad Gerente Compras.
 * Propósito: Proveer los mecanismos de persistencia y consultas hacia la tabla Proveedor en MySQL.
 * Responsabilidad principal: Gestionar las operaciones CRUD y búsquedas de los Gerentes de Compra del sistema.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Se comunica directamente con la entidad GerenteCompra mediante Spring Data JPA.
 * - Es inyectado en la capa de servicio para coordinar la lógica de negocio.
 * Reglas de negocio:
 * - Garantiza la unicidad de los registros mediante el identificador único del gerente de compras.
 *
 * @author Islas López Isis Jezabel Equipo 1
 * Fecha creación: 08-11-2026
 * Fecha modificación: 08-11-2026
 */
public interface PagoProveedorRepositorio extends JpaRepository<PagoProveedor, Integer> {

    /**
     * Consulta y recupera la lista de detalles o conceptos asociados a un pago específico.
     * Información de Modificación: Ninguna, sólo operación de lectura.
     * Información de Tablas, Entidades o Componentes: Consulta la entidad y tabla PagoDetalle filtrando por el ID del pago.
     * Reglas de Negocio: Permite recuperar el desglose completo de partidas que componen una transacción de pago.
     *
     * @param idPago Identificador único del pago a consultar.
     * @return Lista de objetos PagoDetalle vinculados al pago especificado.
     * @author Islas López Isis Jezabel Equipo1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Query("SELECT d FROM PagoDetalle d WHERE d.idPago = :idPago")

    List<PagoDetalle> consultarDetallesPorPago(@Param("idPago") int idPago);

    /**
     * Consulta el historial de pagos realizados a un proveedor específico dentro de un rango de fechas.
     * Información de Modificación: Ninguna, sólo operación de lectura.
     * Información de Tablas, Entidades o Componentes: Consulta la entidad y tabla PagoProveedor filtrando por proveedor y periodo de fechas.
     * Reglas de Negocio: Facilita el control y auditoría de los movimientos financieros efectuados a un proveedor en un periodo determinado.
     *
     * @param idProveedor Identificador único del proveedor a consultar.
     * @param inicio Fecha inicial del periodo de búsqueda.
     * @param fin Fecha final del periodo de búsqueda.
     * @return Lista de objetos PagoProveedor que coinciden con los criterios de proveedor y fechas.
     * @author Islas López Isis Jezabel Equipo1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 08-11-2026
     */
    @Query("SELECT p FROM PagoProveedor p WHERE p.idProveedor = :idProveedor AND p.fechaPago BETWEEN :inicio AND :fin")
    List<PagoProveedor> findByProveedorAndFechaBetween(

            @Param("idProveedor") int idProveedor,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin

    );

}
