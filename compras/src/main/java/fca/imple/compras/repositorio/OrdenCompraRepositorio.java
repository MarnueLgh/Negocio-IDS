package fca.imple.compras.repositorio;

import fca.imple.compras.modelo.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de repositorio de acceso a datos para la entidad OrdenCompra.
 * Propósito: Gestionar el almacenamiento, consulta y actualización de las órdenes de compra en la base de datos.
 * Responsabilidad principal: Proporcionar métodos de consulta derivados y personalizados para el ciclo de vida de las solicitudes de compra.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Interactúa con la entidad OrdenCompra y la tabla OrdenCompraDetalle.
 * - Es consumido por la capa de servicio para verificar estados antes de recepcionar mercancía o registrar compras.
 * Reglas de negocio:
 * - Las órdenes se filtran conforme a sus estados comerciales: Pendiente, Procesada o Cancelada.
 *
 * @author Óscar González Saucedo Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 07-11-2026
 */
@Repository
public interface OrdenCompraRepositorio extends JpaRepository<OrdenCompra, Integer> {

    /**
     * Obtiene la lista de órdenes de compra en estado pendiente asociadas a un proveedor determinado.
     * Información de Modificación: Ninguna, operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Consulta la tabla OrdenCompra.
     * Reglas de Negocio: Filtra únicamente las solicitudes activas pendientes de surtido por el proveedor.
     *
     * @param idProveedor identificador único del proveedor.
     * @param estado estado comercial a evaluar (por ejemplo: "Pendiente").
     * @return lista de entidades OrdenCompra que cumplen con la condición.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 07-11-2026
     */
    List<OrdenCompra> findByIdProveedorAndEstado(Integer idProveedor, String estado);

    /**
     * Localiza las órdenes de compra pendientes que contienen una partida con un artículo determinado.
     * Información de Modificación: Ninguna, operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Cruza OrdenCompra con OrdenCompraDetalle mediante idOrdenCompra.
     * Reglas de Negocio: Identifica órdenes en trámite que esperan abastecimiento del artículo consultado.
     *
     * @param idArticulo identificador del artículo catalogado en el subsistema de inventario.
     * @return lista de órdenes de compra en estado pendiente asociadas a dicho artículo.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 07-11-2026
     */
    @Query(value = "SELECT oc.* FROM OrdenCompra oc " +
            "JOIN OrdenCompraDetalle ocd ON oc.idOrdenCompra = ocd.idOrdenCompra " +
            "WHERE ocd.idArticulo = :idArticulo AND oc.estado = 'Pendiente'", nativeQuery = true)
    List<OrdenCompra> consultarOrdenesPendientesPorArticulo(@Param("idArticulo") Integer idArticulo);

    /**
     * Recupera las órdenes de compra canceladas para un proveedor dentro de un rango de fechas determinado.
     * Información de Modificación: Ninguna, operación de solo lectura.
     * Información de Tablas, Entidades o Componentes: Consulta la tabla OrdenCompra filtrando por fechaCreacion, idProveedor y estado.
     * Reglas de Negocio: Permite la auditoría histórica de solicitudes no concretadas en un periodo específico.
     *
     * @param fechaInicio fecha inicial del periodo de consulta en formato LocalDate.
     * @param fechaFin fecha final del periodo de consulta en formato LocalDate.
     * @param idProveedor identificador del proveedor asociado a las órdenes.
     * @param estado estado cancelado de la orden (por ejemplo: "Cancelada").
     * @return lista de órdenes de compra canceladas dentro del rango temporal.
     * @author Óscar González Saucedo Equipo 1
     * Fecha creación: 07-11-2026
     * Fecha modificación: 07-11-2026
     */
    List<OrdenCompra> findByFechaCreacionBetweenAndIdProveedorAndEstado(LocalDate fechaInicio, LocalDate fechaFin, Integer idProveedor, String estado);
}