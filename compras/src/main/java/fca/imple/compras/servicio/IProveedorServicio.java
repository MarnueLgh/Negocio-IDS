package fca.imple.compras.servicio;

import fca.imple.compras.modelo.Proveedor;
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

public interface IProveedorServicio {

    boolean insertarProveedor(Proveedor proveedor);
    boolean modificarProveedor(Proveedor proveedor);
    boolean borrarProveedor(int idProveedor);
    List<Proveedor> consultarListaProveedores();
    Proveedor consultarProveedorPorIdONombre(int idProveedor, String nombre);
    List<Integer> consultarArticulosPorProveedor(int idProveedor);
}