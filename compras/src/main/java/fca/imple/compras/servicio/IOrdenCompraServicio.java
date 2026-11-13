package fca.imple.compras.servicio;

import fca.imple.compras.modelo.OrdenCompra;
import fca.imple.compras.modelo.OrdenCompraDetalle;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz que define las operaciones del servicio de órdenes de compra.
 * Propósito: Establecer las responsabilidadessobre  las solicitudes de compra.
 * Responsabilidad principal: Delimitar el registro, actualización, cancelación y consulta de órdenes de compra.
 * Subsistema: Compras
 * Relación con otros componentes:
 * - Es implementada por OrdenCompraServicio.
 * - Toda orden de compra gestionada debe tener estados válidos y desglosar correctamente sus montos.
 *
 * @author Óscar González Saucedo Equipo 1
 * Fecha creación: 07-11-2026
 * Fecha modificación: 07-11-2026
 */
public interface IOrdenCompraServicio {

    boolean insertarOrdenCompra(OrdenCompra ordenCompra);
    boolean modificarOrdenCompra(OrdenCompra ordenCompra);
    boolean borrarOrdenCompra(int idOrdenCompra);
    OrdenCompra consultarDetalleOrdenCompra(int idOrdenCompra);
    List<OrdenCompraDetalle> consultarPartidasOrdenCompra(int idOrdenCompra);
    List<OrdenCompra> consultarOrdenesPendientesPorProveedor(int idProveedor);
    List<OrdenCompra> consultarOrdenesPendientesPorArticulo(int idArticulo);
    List<OrdenCompra> consultarOrdenesCanceladas(LocalDate inicio, LocalDate fin, int idProveedor);
}